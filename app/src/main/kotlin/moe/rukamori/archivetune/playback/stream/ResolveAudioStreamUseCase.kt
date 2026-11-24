/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.playback.stream

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.WorkerThread
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import moe.rukamori.archivetune.constants.DataSyncIdKey
import moe.rukamori.archivetune.innertube.PlaybackAuthState
import moe.rukamori.archivetune.innertube.YouTube
import moe.rukamori.archivetune.morideobfuscator.youtubei.YoutubeiException
import moe.rukamori.archivetune.morideobfuscator.youtubei.YoutubeiFailureKind
import moe.rukamori.archivetune.morideobfuscator.youtubei.YoutubeiNetworkConfiguration
import moe.rukamori.archivetune.utils.YTPlayerUtils
import moe.rukamori.archivetune.utils.dataStore
import moe.rukamori.archivetune.utils.potoken.BotGuardTokenGenerator
import moe.rukamori.archivetune.utils.toPlaybackAuthState
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import timber.log.Timber
import java.io.InterruptedIOException
import java.net.ConnectException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class ResolveAudioStreamUseCase
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val youtubeiRepository: YoutubeiStreamRepository,
        private val moduleAudio: moe.rukamori.archivetune.modules.ResolveModuleAudioUseCase,
    ) {
        private data class CacheKey(
            val mediaId: String,
            val quality: String,
            val networkMetered: Boolean,
            val purpose: StreamPurpose,
            val authFingerprint: String,
            val pinnedFormatId: Int?,
            val requiresSongMetadata: Boolean,
            val network: YoutubeiNetworkConfiguration,
        )

        private data class InFlightKey(
            val cacheKey: CacheKey,
            val priority: StreamResolutionPriority,
        )

        private enum class ResolutionConsumer {
            PLAYBACK,
            PRELOAD,
        }

        private class InFlightResolution(
            val deferred: Deferred<ResolvedAudioStream>,
            var playbackOwners: Int = 0,
            var preloadOwners: Int = 0,
        )

        private sealed interface ResolutionLease {
            data class Cached(val stream: ResolvedAudioStream) : ResolutionLease

            data class Active(
                val key: InFlightKey,
                val resolution: InFlightResolution,
            ) : ResolutionLease
        }

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val cache = ConcurrentHashMap<CacheKey, ResolvedAudioStream>()
        private val inFlightLock = Any()
        private val inFlight = mutableMapOf<InFlightKey, InFlightResolution>()
        private val sessionResetMutex = Mutex()
        private val sessionResetRequired = AtomicBoolean(false)
        private val loginContextMutex = Mutex()
        // [VoidTune Patch: Track Idle Duration to Proactively Invalidate Stale Session Tokens]
        // REASON: When music playback is idle for >60s (e.g. 7 minutes idle), YouTube session tokens and
        // QuickJS memory state become stale. Tracking resolution timestamps allows proactive session refresh.
        private val lastResolutionTime = AtomicLong(0L)

        suspend operator fun invoke(request: AudioStreamRequest): ResolvedAudioStream =
            resolve(request, ResolutionConsumer.PLAYBACK)

        suspend fun preload(request: AudioStreamRequest) {
            resolve(request, ResolutionConsumer.PRELOAD)
        }

        private suspend fun resolve(
            request: AudioStreamRequest,
            consumer: ResolutionConsumer,
        ): ResolvedAudioStream {
            if (request.allowModules && request.pinnedFormatId == null) {
                moduleAudio.resolve(request.mediaId, request.moduleMetadata)?.let { return it.toResolvedAudioStream() }
            }
            val key = request.cacheKey()
            val priority = request.resolutionPriority(consumer)
            val lease = acquireResolution(key, request, consumer, priority)
            if (lease is ResolutionLease.Cached) return lease.stream

            val activeLease = lease as ResolutionLease.Active
            val resolution = activeLease.resolution
            return try {
                resolution.deferred.start()
                resolution.deferred.await()
            } finally {
                releaseResolution(activeLease.key, resolution, consumer)
            }
        }

        private fun acquireResolution(
            key: CacheKey,
            request: AudioStreamRequest,
            consumer: ResolutionConsumer,
            priority: StreamResolutionPriority,
        ): ResolutionLease {
            var preloadsToCancel: List<Deferred<ResolvedAudioStream>> = emptyList()
            val lease =
                synchronized(inFlightLock) {
                    cache[key]?.let { cached ->
                        if (isFresh(cached)) return@synchronized ResolutionLease.Cached(cached)
                        cache.remove(key, cached)
                    }

                    val requestedInFlightKey = InFlightKey(key, priority)
                    val alternateInFlightKey =
                        InFlightKey(
                            cacheKey = key,
                            priority =
                                when (priority) {
                                    StreamResolutionPriority.FOREGROUND -> StreamResolutionPriority.BACKGROUND
                                    StreamResolutionPriority.BACKGROUND -> StreamResolutionPriority.FOREGROUND
                                },
                        )
                    val reusableInFlightKey =
                        requestedInFlightKey.takeIf(inFlight::containsKey)
                            ?: alternateInFlightKey.takeIf(inFlight::containsKey)
                    if (reusableInFlightKey != null) {
                        inFlight[reusableInFlightKey]?.let { resolution ->
                            resolution.addOwner(consumer)
                            return@synchronized ResolutionLease.Active(reusableInFlightKey, resolution)
                        }
                    }

                    if (priority == StreamResolutionPriority.FOREGROUND) {
                        val obsoletePreloads =
                            inFlight.entries
                                .filter { (inFlightKey, resolution) ->
                                    inFlightKey.priority == StreamResolutionPriority.BACKGROUND &&
                                        resolution.hasOnlyPreloadOwners() &&
                                        !resolution.deferred.isCompleted
                                }.map { (inFlightKey, resolution) -> inFlightKey to resolution }
                        preloadsToCancel =
                            obsoletePreloads.mapNotNull { (inFlightKey, resolution) ->
                                if (inFlight.remove(inFlightKey, resolution)) {
                                    resolution.deferred
                                } else {
                                    null
                                }
                            }
                    }

                    lateinit var resolution: InFlightResolution
                    val deferred =
                        scope.async(start = CoroutineStart.LAZY) {
                            val resolved =
                                resolveUncached(
                                    request = request,
                                    priority = priority,
                                )
                            val resolutionContext = coroutineContext
                            resolutionContext.ensureActive()
                            synchronized(inFlightLock) {
                                resolutionContext.ensureActive()
                                if (
                                    inFlight[requestedInFlightKey] !== resolution ||
                                    !resolution.hasOwners()
                                ) {
                                    throw CancellationException(
                                        "Audio stream resolution no longer has active consumers",
                                    )
                                }
                                if (key.network != playbackNetworkConfiguration(context)) {
                                    throw ConnectException("Playback network changed during stream resolution")
                                }
                                storeResolvedStream(key, resolved)
                            }
                            resolved
                        }

                    resolution = InFlightResolution(deferred)
                    resolution.addOwner(consumer)
                    deferred.invokeOnCompletion {
                        synchronized(inFlightLock) {
                            if (inFlight[requestedInFlightKey] === resolution) {
                                inFlight.remove(requestedInFlightKey)
                            }
                        }
                    }
                    inFlight[requestedInFlightKey] = resolution
                    ResolutionLease.Active(requestedInFlightKey, resolution)
                }
            preloadsToCancel.forEach { deferred -> deferred.cancel() }
            return lease
        }

        private fun releaseResolution(
            key: InFlightKey,
            resolution: InFlightResolution,
            consumer: ResolutionConsumer,
        ) {
            val deferredToCancel =
                synchronized(inFlightLock) {
                    resolution.removeOwner(consumer)
                    if (
                        resolution.playbackOwners == 0 &&
                        resolution.preloadOwners == 0 &&
                        !resolution.deferred.isCompleted &&
                        inFlight[key] === resolution
                    ) {
                        inFlight.remove(key)
                        resolution.deferred
                    } else {
                        null
                    }
                }
            deferredToCancel?.cancel()
        }

        private fun InFlightResolution.addOwner(consumer: ResolutionConsumer) {
            when (consumer) {
                ResolutionConsumer.PLAYBACK -> playbackOwners += 1
                ResolutionConsumer.PRELOAD -> preloadOwners += 1
            }
        }

        private fun InFlightResolution.hasOwners(): Boolean =
            playbackOwners > 0 || preloadOwners > 0

        private fun InFlightResolution.hasOnlyPreloadOwners(): Boolean =
            playbackOwners == 0 && preloadOwners > 0

        private fun InFlightResolution.removeOwner(consumer: ResolutionConsumer) {
            when (consumer) {
                ResolutionConsumer.PLAYBACK -> playbackOwners -= 1
                ResolutionConsumer.PRELOAD -> preloadOwners -= 1
            }
        }

        @WorkerThread
        fun resolveBlocking(request: AudioStreamRequest): ResolvedAudioStream {
            check(Looper.myLooper() != Looper.getMainLooper())
            val startedAt = SystemClock.elapsedRealtime()
            Timber.tag(TAG).d(
                "Resolving mediaId=%s purpose=%s",
                request.mediaId,
                request.purpose,
            )
            val future = scope.future { invoke(request) }
            return try {
                future.get().also {
                    Timber.tag(TAG).d("Resolution delivered elapsedMs=%d", SystemClock.elapsedRealtime() - startedAt)
                }
            } catch (throwable: InterruptedException) {
                future.cancel(true)
                Thread.currentThread().interrupt()
                throw InterruptedIOException("Audio stream resolution interrupted").apply {
                    initCause(throwable)
                }
            } catch (throwable: ExecutionException) {
                future.cancel(true)
                val cause = throwable.cause ?: throwable
                if (cause is CancellationException) {
                    throw InterruptedIOException("Audio stream resolution was invalidated").apply {
                        initCause(cause)
                    }
                }
                throw cause
            } catch (throwable: CancellationException) {
                future.cancel(true)
                throw InterruptedIOException("Audio stream resolution was cancelled").apply {
                    initCause(throwable)
                }
            } catch (throwable: Throwable) {
                future.cancel(true)
                throw throwable
            }
        }

        fun invalidate(mediaId: String, purpose: StreamPurpose? = null) {
            val deferredsToCancel =
                synchronized(inFlightLock) {
                    cache.keys.removeIf { it.mediaId == mediaId && (purpose == null || it.purpose == purpose) }
                    inFlight.keys
                        .filter {
                            it.cacheKey.mediaId == mediaId &&
                                (purpose == null || it.cacheKey.purpose == purpose)
                        }
                        .mapNotNull { inFlight.remove(it)?.deferred }
                }
            deferredsToCancel.forEach { it.cancel() }
        }

        fun invalidateUrl(url: String) {
            cache.entries.removeIf { it.value.url == url }
        }

        fun reportRejectedStream(url: String) {
            val rejectedUrl = url.canonicalStreamUrl()
            val rejectedEntry =
                cache.entries.firstOrNull { it.value.url.canonicalStreamUrl() == rejectedUrl } ?: return
            val rejected = rejectedEntry.value
            synchronized(inFlightLock) {
                cache.entries.removeIf { it.value.url.canonicalStreamUrl() == rejectedUrl }
            }
            if (rejected.source != StreamSource.YOUTUBEI) return
            if (YTPlayerUtils.isExpiredOrNearExpiredStreamUrl(url)) return
            if (rejectedEntry.key.network != playbackNetworkConfiguration(context)) return
            val rejectedBinding = rejected.gvsPoTokenBinding ?: return
            val rejectedGeneration = rejected.gvsPoTokenMinterGeneration ?: return
            youtubeiRepository.reportRejectedGvsPoToken(rejectedGeneration, rejectedBinding)
            synchronized(inFlightLock) {
                cache.entries.removeIf { it.value.gvsPoTokenMinterGeneration == rejectedGeneration }
            }
        }

        fun invalidateSession() {
            sessionResetRequired.set(true)
            clear()
        }

        fun peek(request: AudioStreamRequest): ResolvedAudioStream? {
            if (request.allowModules && request.pinnedFormatId == null && moduleAudio.enabled()) {
                return moduleAudio.peek(request.mediaId, request.moduleMetadata)?.toResolvedAudioStream()
            }
            val key = request.cacheKey()
            val resolved = cache[key] ?: return null
            if (isFresh(resolved)) return resolved
            cache.remove(key, resolved)
            return null
        }

        fun clear() {
            val deferredsToCancel =
                synchronized(inFlightLock) {
                    cache.clear()
                    inFlight.values.map(InFlightResolution::deferred).also { inFlight.clear() }
                }
            deferredsToCancel.forEach { it.cancel() }
        }

        private suspend fun resolveUncached(
            request: AudioStreamRequest,
            priority: StreamResolutionPriority,
        ): ResolvedAudioStream {
            sessionResetMutex.withLock {
                if (sessionResetRequired.getAndSet(false)) {
                    try {
                        YTPlayerUtils.resetAnonymousFallbackSession()
                        BotGuardTokenGenerator.invalidateAll()
                        youtubeiRepository.invalidateSessions()
                    } catch (failure: Throwable) {
                        sessionResetRequired.set(true)
                        throw failure
                    }
                }
            }

            val now = SystemClock.elapsedRealtime()
            val previousResolution = lastResolutionTime.getAndSet(now)
            if (previousResolution > 0L && (now - previousResolution) > 60_000L) {
                // [VoidTune Patch: Proactive Session Invalidation on Idle Wakeup]
                // REASON: When playback resumes after >60s (e.g. 7 minutes idle), stale QuickJS tokens
                // can deadlock player deciphering. Invalidating ensures a fresh session runtime.
                try {
                    youtubeiRepository.invalidateSessions()
                } catch (t: Throwable) {
                    Timber.tag(TAG).w(t, "Failed to invalidate sessions on idle wakeup")
                }
            }

            // [VoidTune Patch: Anonymous-First Stream Resolution (VISIONOS) with Authenticated Fallback]
            // CAUSE: When logged in, routing every song through WEB_CREATOR forces BotGuard PoToken minting,
            // 4MB player script download, AST deciphering, and pre-playback GVS Range probe, causing 2-5s lag per song.
            // REASON: Normal YouTube audio streams are public and don't require account cookies.
            // By resolving via token-free VISIONOS first (PlaybackAuthState.EMPTY), 99.9% of songs start in <300ms.
            // If and only if anonymous resolution fails (e.g. age-restricted content, private uploads, or member-only tracks),
            // we cleanly fall back to resolveWithAuth(request, priority) using the user's Google account session.
            /*
            ORIGINAL CODE:
            val authState = request.authState
            val contentBinding = if (authState.hasLoginCookie) {
                with(YTPlayerUtils) { authState.poTokenContentBinding() }
            } else null

            val isWarm = contentBinding != null && BotGuardTokenGenerator.isReadyForSession(contentBinding)

            if (authState.hasLoginCookie && !isWarm) {
                Timber.tag(TAG).i("BotGuard engine is cold for %s; fast-starting via token-free VISIONOS stream", request.mediaId)
                try {
                    return youtubeiRepository.resolve(
                        request = request.copy(authState = PlaybackAuthState.EMPTY),
                        priority = priority,
                    )
                } catch (fastStartFailure: Exception) {
                    if (fastStartFailure is CancellationException) throw fastStartFailure
                    Timber.tag(TAG).w(fastStartFailure, "Fast-start VISIONOS fallback failed, proceeding with authenticated resolution")
                }
            }

            return try {
                resolveWithAuth(request, priority)
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                val rejectedLogin = failure is YTPlayerUtils.InvalidPlaybackLoginContextException ||
                    failure is YoutubeiException && failure.kind == YoutubeiFailureKind.HTTP &&
                    failure.httpStatus in setOf(401, 403)
                val accountWithoutDirectStream =
                    failure is YTPlayerUtils.BadStreamPlayerResponseException &&
                        (failure.cause as? YoutubeiException)?.kind == YoutubeiFailureKind.NO_FORMAT
                if (!request.authState.hasLoginCookie || !rejectedLogin && !accountWithoutDirectStream) throw failure
                try {
                    resolveWithAuth(request.copy(authState = PlaybackAuthState.EMPTY), priority)
                } catch (anonymousFailure: Exception) {
                    if (anonymousFailure is CancellationException) throw anonymousFailure
                    YTPlayerUtils.resetAnonymousFallbackSession()
                    if (anonymousFailure is YTPlayerUtils.LoginRequiredForPlaybackException ||
                        anonymousFailure is YTPlayerUtils.BadStreamPlayerResponseException
                    ) {
                        failure.addSuppressed(anonymousFailure)
                        throw failure
                    }
                    anonymousFailure.addSuppressed(failure)
                    throw anonymousFailure
                }
            }
            */
            /*
            ORIGINAL CODE:
            val authState = request.authState
            if (authState.hasLoginCookie) {
                try {
                    Timber.tag(TAG).d("Resolving %s via fast token-free VISIONOS stream", request.mediaId)
                    return youtubeiRepository.resolve(
                        request = request.copy(authState = PlaybackAuthState.EMPTY),
                        priority = priority,
                    )
                } catch (anonymousFailure: Exception) {
                    if (anonymousFailure is CancellationException) throw anonymousFailure
                    Timber.tag(TAG).i(
                        anonymousFailure,
                        "Anonymous stream resolution failed for %s; falling back to authenticated resolution (WEB_CREATOR)",
                        request.mediaId,
                    )
                    return resolveWithAuth(request, priority)
                }
            }

            return resolveWithAuth(request, priority)
            */
            // [VoidTune Patch: Universal Token-Free Stream Resolution with Authenticated Fallback]
            // CAUSE: Previously, 'if (authState.hasLoginCookie)' meant anonymous (non-logged-in) users
            // completely bypassed the fast token-free path and fell straight into resolveWithAuth(),
            // triggering BotGuardTokenGenerator and WebView operations that freeze/deadlock after 7 minutes idle.
            // REASON: ALL requests (whether anonymous or logged-in) can resolve via token-free VISIONOS in <200ms.
            // If and only if token-free resolution fails for restricted content AND the user has login cookies,
            // we cleanly fall back to resolveWithAuth(request, priority).
            val authState = request.authState
            try {
                Timber.tag(TAG).d("Resolving %s via fast token-free VISIONOS stream", request.mediaId)
                return youtubeiRepository.resolve(
                    request = request.copy(authState = PlaybackAuthState.EMPTY),
                    priority = priority,
                )
            } catch (directFailure: Exception) {
                if (directFailure is CancellationException) throw directFailure
                if (authState.hasLoginCookie) {
                    Timber.tag(TAG).i(
                        directFailure,
                        "Fast direct stream resolution failed for %s; falling back to authenticated resolution (WEB_CREATOR)",
                        request.mediaId,
                    )
                    return resolveWithAuth(request, priority)
                } else {
                    Timber.tag(TAG).w(
                        directFailure,
                        "Fast direct stream resolution failed for %s and no login cookie available",
                        request.mediaId,
                    )
                    throw directFailure
                }
            }
        }

        private suspend fun resolveWithAuth(
            request: AudioStreamRequest,
            priority: StreamResolutionPriority,
        ): ResolvedAudioStream {
            val resolvedAuthState =
                YTPlayerUtils.ensureYoutubeiPoTokensForPlayback(
                    videoId = request.mediaId,
                    authState = restoreLoginContext(request),
                )
            return youtubeiRepository.resolve(
                request = request.copy(authState = resolvedAuthState),
                priority = priority,
            )
        }

        private suspend fun restoreLoginContext(request: AudioStreamRequest): PlaybackAuthState {
            val authState = request.authState
            if (!authState.hasLoginCookie || authState.hasPlaybackLoginContext) return authState
            val dataSyncId =
                loginContextMutex.withLock {
                    context.dataStore.data
                        .first()
                        .toPlaybackAuthState()
                        .takeIf { it.cookie == authState.cookie }
                        ?.dataSyncId
                        ?: fetchLoginContext(request)
                }
            Timber.tag(TAG).i("Restored the account session for %s before stream resolution", request.mediaId)
            return authState.copy(dataSyncId = dataSyncId).normalized()
        }

        private suspend fun fetchLoginContext(request: AudioStreamRequest): String {
            val authState = request.authState
            val targetUrl = "https://music.youtube.com/watch?v=${request.mediaId}"
            if (YouTube.currentPlaybackAuthState().cookie != authState.cookie) {
                throw YTPlayerUtils.InvalidPlaybackLoginContextException(
                    videoId = request.mediaId,
                    targetUrl = targetUrl,
                    cause = IllegalStateException("YouTube account session is missing"),
                )
            }
            val dataSyncId =
                YouTube.accountDataSyncId().getOrElse { failure ->
                    if (failure is CancellationException) throw failure
                    val sessionRejected =
                        failure is ClientRequestException ||
                            failure is IllegalStateException && failure !is ResponseException
                    if (!sessionRejected) throw failure
                    throw YTPlayerUtils.InvalidPlaybackLoginContextException(
                        videoId = request.mediaId,
                        targetUrl = targetUrl,
                        cause = failure,
                    )
                }
            context.dataStore.edit { preferences ->
                val persisted = preferences.toPlaybackAuthState()
                if (persisted.cookie == authState.cookie && persisted.dataSyncId == null) {
                    preferences[DataSyncIdKey] = dataSyncId
                }
            }
            return dataSyncId
        }

        private fun AudioStreamRequest.resolutionPriority(
            consumer: ResolutionConsumer,
        ): StreamResolutionPriority =
            if (consumer == ResolutionConsumer.PLAYBACK && purpose == StreamPurpose.PLAYBACK) {
                StreamResolutionPriority.FOREGROUND
            } else {
                StreamResolutionPriority.BACKGROUND
            }

        private fun AudioStreamRequest.cacheKey(): CacheKey =
            CacheKey(
                mediaId = mediaId,
                quality = quality.name,
                networkMetered = networkMetered,
                purpose = purpose,
                authFingerprint = authState.streamCacheFingerprint,
                pinnedFormatId = pinnedFormatId,
                requiresSongMetadata = requiresSongMetadata,
                network = playbackNetworkConfiguration(context),
            )

        private fun storeResolvedStream(
            key: CacheKey,
            resolved: ResolvedAudioStream,
        ) {
            putResolvedStream(key, resolved)
            if (resolved.source == StreamSource.YOUTUBEI) {
                val alternatePurpose =
                    when (key.purpose) {
                        StreamPurpose.PLAYBACK -> StreamPurpose.DOWNLOAD
                        StreamPurpose.DOWNLOAD -> StreamPurpose.PLAYBACK
                    }
                putResolvedStream(
                    key.copy(
                        purpose = alternatePurpose,
                        requiresSongMetadata = false,
                    ),
                    resolved,
                )
            }
            Timber.tag(TAG).d(
                "Resolved %s via %s (%s)",
                key.mediaId,
                resolved.source,
                resolved.runtimeVersion ?: "native",
            )
            if (cache.size <= MAX_CACHE_ENTRIES) return
            cache.entries.removeIf { !isFresh(it.value) }
            val excess = cache.size - MAX_CACHE_ENTRIES
            if (excess <= 0) return
            cache.entries
                .sortedBy { it.value.expiresAtMs }
                .take(excess)
                .forEach { entry -> cache.remove(entry.key, entry.value) }
        }

        private fun putResolvedStream(
            key: CacheKey,
            resolved: ResolvedAudioStream,
        ) {
            cache[key] = resolved
            cache[
                key.copy(
                    authFingerprint = resolved.authFingerprint,
                ),
            ] = resolved
        }

        private fun isFresh(stream: ResolvedAudioStream): Boolean =
            stream.expiresAtMs > System.currentTimeMillis() + STREAM_EXPIRY_SAFETY_MS

        private fun String.canonicalStreamUrl(): String = toHttpUrlOrNull()?.toString() ?: this

        private companion object {
            const val TAG = "AudioStreamResolver"
            const val STREAM_EXPIRY_SAFETY_MS = 60_000L
            const val MAX_CACHE_ENTRIES = 256
        }
    }
