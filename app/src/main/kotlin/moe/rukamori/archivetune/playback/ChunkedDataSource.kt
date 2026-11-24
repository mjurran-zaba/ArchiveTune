/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import java.io.EOFException

// [VoidTune Patch: Multi-Tier Adaptive Chunking Architecture]
// REASON: Support initial fast-start chunk length (e.g. 512KB) for low latency,
// transitioning to sustained chunk length (e.g. 1MB) for continuous playback.
// Backward compatible with single chunkLength parameter via default argument.
/*
ORIGINAL CODE:
internal class ChunkedDataSource(
    private val upstream: DataSource,
    private val chunkLength: Long,
) : DataSource {
*/
internal class ChunkedDataSource(
    private val upstream: DataSource,
    private val chunkLength: Long,
    private val initialChunkLength: Long = chunkLength,
) : DataSource {
    private var request: DataSpec? = null
    private var bytesRead = 0L
    private var chunkBytesRemaining = C.LENGTH_UNSET.toLong()
    private var upstreamOpen = false

    override fun addTransferListener(transferListener: TransferListener) {
        upstream.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        check(request == null)
        request = dataSpec
        bytesRead = 0L
        val resolvedLength = openNextChunk(dataSpec)
        return if (dataSpec.length == C.LENGTH_UNSET.toLong()) resolvedLength else dataSpec.length
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        val dataSpec = checkNotNull(request)
        if (dataSpec.length != C.LENGTH_UNSET.toLong() && bytesRead == dataSpec.length) {
            return C.RESULT_END_OF_INPUT
        }
        if (chunkBytesRemaining == 0L) {
            closeUpstream()
            openNextChunk(dataSpec.subrange(bytesRead))
        }
        val readLength =
            if (chunkBytesRemaining == C.LENGTH_UNSET.toLong()) {
                length
            } else {
                minOf(length.toLong(), chunkBytesRemaining).toInt()
            }
        val count = upstream.read(buffer, offset, readLength)
        if (count == C.RESULT_END_OF_INPUT) {
            /*
            ORIGINAL CODE:
            if (chunkBytesRemaining > 0L) {
                throw EOFException("Audio stream ended before the requested range was complete")
            }
            return count
            */
            // [VoidTune Patch: Safe EOF Handling to Prevent Corrupt Cache Invalidation Loop]
            // CAUSE: When an audio stream ends, the final chunk frequently has fewer bytes remaining than
            // the full chunk length (e.g. chunkBytesRemaining = 500KB). Throwing EOFException caused
            // MusicService to flag the stream as corrupted (isCacheCorruptionError), delete the cached audio,
            // and restart the download from byte 0, causing an infinite download loop that consumed 1GB of data.
            // REASON: When upstream reaches end of input, if the request was open-ended (C.LENGTH_UNSET) or if
            // all expected bytes were read, cleanly return C.RESULT_END_OF_INPUT. Only throw if an explicitly
            // bounded total DataSpec was truncated before its total length.
            if (dataSpec.length != C.LENGTH_UNSET.toLong() && bytesRead < dataSpec.length) {
                throw EOFException("Audio stream ended before the requested range was complete (read $bytesRead of ${dataSpec.length})")
            }
            return count
        }
        bytesRead += count
        if (chunkBytesRemaining != C.LENGTH_UNSET.toLong()) {
            chunkBytesRemaining -= count
        }
        return count
    }

    override fun getUri(): Uri? = upstream.uri

    override fun getResponseHeaders(): Map<String, List<String>> = upstream.responseHeaders

    override fun close() {
        request = null
        bytesRead = 0L
        chunkBytesRemaining = C.LENGTH_UNSET.toLong()
        closeUpstream()
    }

    private fun openNextChunk(dataSpec: DataSpec): Long {
        // [VoidTune Patch: Preserve Unbounded Contract on Initial Stream Open]
        // CAUSE: Clamping unbounded initial requests (dataSpec.length == C.LENGTH_UNSET / -1) to initialChunkLength (512KB)
        // caused ExoPlayer extractor starvation because ExoPlayer's DataSource contract expects open() to return
        // the resolved total stream length (from HTTP Content-Range) or C.LENGTH_UNSET. When capped at 512KB,
        // ExoPlayer treated 512KB as the entire audio file length and stopped reading at EOF (infinite buffer spinner).
        // REASON: When dataSpec.length == C.LENGTH_UNSET, length must be null so chunkSpec remains unbounded,
        // letting upstream OkHttpDataSource learn the true Content-Range from Google Video Server.
        // Subsequent chunk reads (dataSpec.length > 0) are paced with chunkLength.
        /*
        [VoidTune Previous Revision (Clamped Initial Request)]:
        val targetChunkLength = if (bytesRead == 0L) initialChunkLength else chunkLength
        val length =
            resolveStreamChunkLength(
                requestedLength = dataSpec.length,
                position = dataSpec.position,
                knownContentLength = null,
                chunkLength = targetChunkLength,
            )
        val chunkSpec = length?.let { dataSpec.subrange(0L, it) } ?: dataSpec
        */
        val length =
            if (dataSpec.length == C.LENGTH_UNSET.toLong()) {
                null
            } else {
                resolveStreamChunkLength(
                    requestedLength = dataSpec.length,
                    position = dataSpec.position,
                    knownContentLength = null,
                    chunkLength = chunkLength,
                )
            }
        val chunkSpec = length?.let { dataSpec.subrange(0L, it) } ?: dataSpec
        chunkBytesRemaining = chunkSpec.length
        upstreamOpen = true
        return upstream.open(chunkSpec)
    }

    private fun closeUpstream() {
        if (!upstreamOpen) return
        upstreamOpen = false
        upstream.close()
    }
}
