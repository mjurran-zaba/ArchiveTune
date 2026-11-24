import os

# --- Patch 1: core InnerTube Concurrency ---
core_file = "core/src/main/kotlin/moe/rukamori/archivetune/innertube/InnerTube.kt"
if os.path.exists(core_file):
    with open(core_file, "r", encoding="utf-8") as f:
        core_content = f.read()

    if "import okhttp3.ConnectionPool" not in core_content:
        core_content = core_content.replace(
            "import okhttp3.Dns",
            "import okhttp3.ConnectionPool\nimport okhttp3.Dispatcher\nimport okhttp3.Dns\nimport java.util.concurrent.TimeUnit",
        )

    old_core_block = """                    addInterceptor(NetworkGatekeeper)
                    dns(this@InnerTube.dns)"""

    patch_core_block = """                    addInterceptor(NetworkGatekeeper)
                    dns(this@InnerTube.dns)
                    // [VoidTune Patch: Concurrency Optimization for OkHttp Dispatcher]
                    // CAUSE: Default OkHttp maxRequestsPerHost is 5. When background sync runs, all 5 connections
                    // to music.youtube.com are consumed, starving user-initiated browsing (Home, Search, Artist).
                    // REASON: Increase maxRequestsPerHost to 20 and configure ConnectionPool so UI requests are never
                    // blocked behind background sync requests.
                    dispatcher(
                        Dispatcher().apply {
                            maxRequests = 64
                            maxRequestsPerHost = 20
                        },
                    )
                    connectionPool(
                        ConnectionPool(16, 5, TimeUnit.MINUTES),
                    )"""

    if "maxRequestsPerHost = 20" not in core_content and old_core_block in core_content:
        core_content = core_content.replace(old_core_block, patch_core_block, 1)

    with open(core_file, "w", encoding="utf-8") as f:
        f.write(core_content)
    print("Applied InnerTube concurrency patch successfully.")

# --- Patch 2: morideobfuscator YoutubeiHttpClient ---
mori_client_file = "morideobfuscator/src/main/kotlin/moe/rukamori/archivetune/morideobfuscator/youtubei/YoutubeiHttpClient.kt"
if os.path.exists(mori_client_file):
    with open(mori_client_file, "r", encoding="utf-8") as f:
        mori_content = f.read()

    if "import okhttp3.ConnectionPool" not in mori_content:
        mori_content = mori_content.replace(
            "import okhttp3.Credentials",
            "import okhttp3.ConnectionPool\nimport okhttp3.Credentials",
        )

    old_current_client = """    private fun currentClient(): OkHttpClient {
        val configuration = configurationProvider()
        activeClient?.takeIf { it.configuration == configuration }?.let { return it.client }
        return synchronized(clientLock) {
            activeClient?.takeIf { it.configuration == configuration }?.client
                ?: buildClient(configuration).also { client ->
                    activeClient?.client?.dispatcher?.cancelAll()
                    activeClient?.client?.connectionPool?.evictAll()
                    activeClient = ActiveClient(configuration, client)
                }
        }
    }

    private fun buildClient(configuration: YoutubeiNetworkConfiguration): OkHttpClient =
        OkHttpClient
            .Builder()
            .proxy(configuration.proxy ?: Proxy.NO_PROXY)
            .dns(configuration.dns)
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)"""

    patch_current_client = """    @Volatile
    private var lastClientAccessTime: Long = 0L

    /*
    ORIGINAL CODE:
    private fun currentClient(): OkHttpClient {
        val configuration = configurationProvider()
        activeClient?.takeIf { it.configuration == configuration }?.let { return it.client }
        return synchronized(clientLock) {
            activeClient?.takeIf { it.configuration == configuration }?.client
                ?: buildClient(configuration).also { client ->
                    activeClient?.client?.dispatcher?.cancelAll()
                    activeClient?.client?.connectionPool?.evictAll()
                    activeClient = ActiveClient(configuration, client)
                }
        }
    }

    private fun buildClient(configuration: YoutubeiNetworkConfiguration): OkHttpClient =
        OkHttpClient
            .Builder()
            .proxy(configuration.proxy ?: Proxy.NO_PROXY)
            .dns(configuration.dns)
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    */
    // [VoidTune Patch: Proactive Connection Pool Eviction on Idle Wakeup & Fast Retry]
    // REASON: When resuming playback after 7 minutes of idle, pooled connections to YouTube have been killed
    // by carrier-grade NAT timeouts. Proactively evicting idle connections on wakeup (>45s) and enforcing
    // retryOnConnectionFailure(true) with a 45-second keep-alive ensures fresh TLS handshakes and prevents freezes.
    private fun currentClient(): OkHttpClient {
        val configuration = configurationProvider()
        val now = SystemClock.elapsedRealtime()
        val wasIdle = (now - lastClientAccessTime) > 45_000L
        lastClientAccessTime = now
        if (wasIdle) {
            synchronized(clientLock) {
                activeClient?.client?.connectionPool?.evictAll()
            }
        }
        activeClient?.takeIf { it.configuration == configuration }?.let { return it.client }
        return synchronized(clientLock) {
            activeClient?.takeIf { it.configuration == configuration }?.client
                ?: buildClient(configuration).also { client ->
                    activeClient?.client?.dispatcher?.cancelAll()
                    activeClient?.client?.connectionPool?.evictAll()
                    activeClient = ActiveClient(configuration, client)
                }
        }
    }

    private fun buildClient(configuration: YoutubeiNetworkConfiguration): OkHttpClient =
        OkHttpClient
            .Builder()
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(8, 45, TimeUnit.SECONDS))
            .proxy(configuration.proxy ?: Proxy.NO_PROXY)
            .dns(configuration.dns)
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)"""

    if "lastClientAccessTime" not in mori_content and old_current_client in mori_content:
        mori_content = mori_content.replace(old_current_client, patch_current_client, 1)

    with open(mori_client_file, "w", encoding="utf-8") as f:
        f.write(mori_content)
    print("Applied YoutubeiHttpClient idle eviction patch successfully.")

# --- Patch 3: morideobfuscator JS bundle VISIONOS priority ---
mori_bundle_file = "morideobfuscator/src/main/assets/youtubei/youtubei.bundle.js"
if os.path.exists(mori_bundle_file):
    with open(mori_bundle_file, "r", encoding="utf-8") as f:
        bundle_content = f.read()

    if "[AH,lJ]" in bundle_content:
        bundle_content = bundle_content.replace("[AH,lJ]", "[lJ,AH]", 1)
        with open(mori_bundle_file, "w", encoding="utf-8") as f:
            f.write(bundle_content)
        print("Applied youtubei.bundle.js VISIONOS priority patch successfully.")

