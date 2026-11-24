/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.playback.stream

import android.content.Context
import android.net.ConnectivityManager
import moe.rukamori.archivetune.innertube.YouTube
import moe.rukamori.archivetune.morideobfuscator.youtubei.YoutubeiNetworkConfiguration
import okhttp3.Call
import okhttp3.ConnectionPool
import okhttp3.Credentials
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.net.Inet6Address
import java.net.Proxy

internal fun playbackNetworkConfiguration(context: Context): YoutubeiNetworkConfiguration {
    val manager = requireNotNull(context.getSystemService(ConnectivityManager::class.java))
    val network = manager.activeNetwork
    val networkIdentity = "${network?.networkHandle}:${network?.let(manager::getLinkProperties)}"
    val proxy = YouTube.streamProxy
    return YoutubeiNetworkConfiguration(
        proxy = proxy,
        proxyUsername = YouTube.proxyUsername,
        proxyPassword = YouTube.proxyPassword,
        dns = if (proxy == null) playbackAddressFamilyFallback.dns(networkIdentity, YouTube.dns) else YouTube.dns,
        networkIdentity = networkIdentity,
    )
}

/*
ORIGINAL CODE:
internal class PlaybackHttpClient(
    private val context: Context,
    private val baseClient: OkHttpClient,
    private val connectionPool: () -> ConnectionPool = { ConnectionPool() },
) : Call.Factory {
    private var configuration: YoutubeiNetworkConfiguration? = null
    private var client: OkHttpClient? = null
    private var streamIpv6: Boolean? = null

    @Synchronized
    override fun newCall(request: Request): Call {
*/
// [VoidTune Patch: Shortened Keep-Alive & Proactive Eviction for Media Sockets to Prevent CGNAT Zombie Sockets]
// REASON: Default ConnectionPool keeps sockets alive for 5 minutes. Carrier NAT tables drop idle connections
// after 2-3 minutes. Setting keepAlive to 45 seconds and evicting when waking after >45s idle prevents sending
// requests over dead half-open sockets when playing music after 7 minutes of idle.
internal class PlaybackHttpClient(
    private val context: Context,
    private val baseClient: OkHttpClient,
    private val connectionPool: () -> ConnectionPool = { ConnectionPool(8, 45, java.util.concurrent.TimeUnit.SECONDS) },
) : Call.Factory {
    private var configuration: YoutubeiNetworkConfiguration? = null
    private var client: OkHttpClient? = null
    private var streamIpv6: Boolean? = null
    private var lastCallTimeMs: Long = 0L

    @Synchronized
    override fun newCall(request: Request): Call {
        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastCallTimeMs > 45_000L) {
            client?.connectionPool?.evictAll()
        }
        lastCallTimeMs = now
        val current = playbackNetworkConfiguration(context)
        val requestedIpv6 = request.url.queryParameter("ip")
            ?.takeIf { it.isNotBlank() && request.url.host.isGoogleVideoHost() && current.proxy == null }
            ?.contains(':')
        if (current != configuration || requestedIpv6 != streamIpv6) {
            client?.connectionPool?.evictAll()
            val username = current.proxyUsername
            val password = current.proxyPassword
            val dns = if (requestedIpv6 == null) current.dns else Dns { hostname ->
                val addresses = current.dns.lookup(hostname)
                if (hostname.isGoogleVideoHost()) {
                    addresses.filter { (it is Inet6Address) == requestedIpv6 }
                        .ifEmpty { addresses }
                } else {
                    addresses
                }
            }
            /*
            ORIGINAL CODE:
            client = baseClient.newBuilder()
                .connectionPool(connectionPool())
                .proxy(current.proxy ?: Proxy.NO_PROXY)
                .dns(dns)
            */
            // [VoidTune Patch: Enforce retryOnConnectionFailure for Media Socket Streaming]
            // REASON: If a connection fails midway or was dropped by NAT, OkHttp should immediately retry on
            // another route rather than hanging the playback thread.
            client = baseClient.newBuilder()
                .retryOnConnectionFailure(true)
                .connectionPool(connectionPool())
                .proxy(current.proxy ?: Proxy.NO_PROXY)
                .dns(dns)
                .eventListener(
                    playbackAddressFamilyFallback.connectionListener(current.networkIdentity) {
                        Timber.w("IPv6 connection to YouTube media failed; using IPv4 on this network")
                    },
                )
                .proxyAuthenticator { _, response ->
                    if (current.proxy == null || username.isNullOrBlank() || password.isNullOrBlank() ||
                        response.request.header("Proxy-Authorization") != null
                    ) {
                        null
                    } else {
                        response.request.newBuilder()
                            .header("Proxy-Authorization", Credentials.basic(username, password))
                            .build()
                    }
                }
                .build()
            configuration = current
            streamIpv6 = requestedIpv6
        }
        return requireNotNull(client).newCall(request)
    }
}
