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

# --- Patch 3: morideobfuscator JS bundle Fast Direct Client & PoToken support ---
# [VoidTune Patch: Fast Direct Stream (VISIONOS) for Logged-In Users & PoToken Preservation]
# CAUSE: In logged-in mode, morideobfuscator only used WEB_CREATOR [q4], which downloads a 4MB player script
# and spends 47+ seconds AST parsing in QuickJS, causing ExoPlayer to timeout (JobCancellationException 58s).
# Furthermore, VISIONOS stripped PoToken (supportsGvsPoToken was false for lJ), causing bot detection on cellular.
# REASON:
# 1. Allow logged-in users to resolve via fast direct VISIONOS first ([lJ,q4]), falling back to WEB_CREATOR.
# 2. Allow VISIONOS to keep the valid PoToken (supportsGvsPoToken: o || i === lJ) without downloading 4MB base.js.
mori_bundle_file = "morideobfuscator/src/main/assets/youtubei/youtubei.bundle.js"
if os.path.exists(mori_bundle_file):
    with open(mori_bundle_file, "r", encoding="utf-8") as f:
        bundle_content = f.read()

    if "let o=!!ne(e.cookie)?[q4]:[AH,lJ]" in bundle_content:
        bundle_content = bundle_content.replace(
            "let o=!!ne(e.cookie)?[q4]:[AH,lJ]",
            "let o=!!ne(e.cookie)?[lJ,q4]:[lJ,AH]",
            1,
        )
        print("Applied youtubei.bundle.js logged-in VISIONOS-first patch.")
    elif "let o=!!ne(e.cookie)?[q4]:[lJ,AH]" in bundle_content:
        bundle_content = bundle_content.replace(
            "let o=!!ne(e.cookie)?[q4]:[lJ,AH]",
            "let o=!!ne(e.cookie)?[lJ,q4]:[lJ,AH]",
            1,
        )
        print("Applied youtubei.bundle.js logged-in VISIONOS-first patch.")
    elif "let o=!!ne(e.cookie)?[lJ,q4]:[lJ,AH]" in bundle_content:
        print("youtubei.bundle.js already has logged-in VISIONOS-first patch.")

    target_gvs_pot = "supportsGvsPoToken:o,"
    patch_gvs_pot = "supportsGvsPoToken:o||i===lJ,"
    if target_gvs_pot in bundle_content:
        bundle_content = bundle_content.replace(target_gvs_pot, patch_gvs_pot, 1)
        print("Applied youtubei.bundle.js VISIONOS PoToken preservation patch.")
    elif patch_gvs_pot in bundle_content:
        print("youtubei.bundle.js already has VISIONOS PoToken preservation patch.")

    # [VoidTune Patch: Pass PoToken to getBasicInfo for VISIONOS to prevent anti-bot trigger on rapid song switch]
    target_basic_info_pot = "po_token:o?ne(e.videoPoToken):void 0"
    patch_basic_info_pot = "po_token:(o||i===lJ)?ne(e.videoPoToken):void 0"
    if target_basic_info_pot in bundle_content:
        bundle_content = bundle_content.replace(target_basic_info_pot, patch_basic_info_pot, 1)
        print("Applied youtubei.bundle.js VISIONOS basicInfo PoToken patch.")
    elif patch_basic_info_pot in bundle_content:
        print("youtubei.bundle.js already has VISIONOS basicInfo PoToken patch.")

    # [VoidTune Patch: Robust multi-client fallback in OJ]
    # CAUSE: If primary client (VISIONOS) throws any error other than NO_FORMAT (e.g. LOGIN_REQUIRED),
    # OJ previously aborted immediately ('if(bo(r)!=="NO_FORMAT")throw r;').
    # REASON: Allow fallback to secondary client (WEB_CREATOR) on ANY recoverable error.
    target_oj_fallback = 'if(!s){if(bo(r)!=="NO_FORMAT")throw r;s=r;continue}'
    patch_oj_fallback = 'if(!s){s=r;continue}'
    if target_oj_fallback in bundle_content:
        bundle_content = bundle_content.replace(target_oj_fallback, patch_oj_fallback, 1)
        print("Applied youtubei.bundle.js multi-client fallback patch.")
    elif patch_oj_fallback in bundle_content:
        print("youtubei.bundle.js already has multi-client fallback patch.")

    # [VoidTune Patch: Strip onBehalfOfUser during VISIONOS getBasicInfo to prevent HTTP 400/401]
    # CAUSE: When logged in, onBehalfOfUser is attached to session.context.user.
    # VISIONOS is an Apple client that rejects onBehalfOfUser with HTTP 400/401.
    # REASON: Temporarily omit onBehalfOfUser while resolving VISIONOS, restoring it for WEB_CREATOR.
    target_sh = 'async function SH(t,e,i,s){let o=i===q4||i===AH;o&&!s&&await EH(t);let r=s||await t.getBasicInfo(e.mediaId,{client:i,po_token:(o||i===lJ)?ne(e.videoPoToken):void 0}),a=ne(r.playability_status?.status),c=ne(r.playability_status?.reason);'
    patch_sh = 'async function SH(t,e,i,s){let o=i===q4||i===AH,uO=t.session?.context?.user?.onBehalfOfUser;i===lJ&&t.session?.context?.user&&delete t.session.context.user.onBehalfOfUser;o&&!s&&await EH(t);let r;try{r=s||await t.getBasicInfo(e.mediaId,{client:i,po_token:(o||i===lJ)?ne(e.videoPoToken):void 0})}finally{uO&&t.session?.context?.user&&(t.session.context.user.onBehalfOfUser=uO)}let a=ne(r.playability_status?.status),c=ne(r.playability_status?.reason);'
    if target_sh in bundle_content:
        bundle_content = bundle_content.replace(target_sh, patch_sh, 1)
        print("Applied youtubei.bundle.js VISIONOS onBehalfOfUser isolation patch.")
    elif patch_sh in bundle_content:
        print("youtubei.bundle.js already has VISIONOS onBehalfOfUser isolation patch.")

    with open(mori_bundle_file, "w", encoding="utf-8") as f:
        f.write(bundle_content)

# --- Patch 4: morideobfuscator YoutubeiRequestAuthentication (Isolate VISIONOS from web cookies & Origin match) ---
mori_auth_file = "morideobfuscator/src/main/kotlin/moe/rukamori/archivetune/morideobfuscator/youtubei/YoutubeiRequestAuthentication.kt"
if os.path.exists(mori_auth_file):
    with open(mori_auth_file, "r", encoding="utf-8") as f:
        auth_content = f.read()

    old_apply_to = """    fun applyTo(request: Request): Request {
        if (request.url.host !in AUTHENTICATED_HOSTS ||
            !request.url.encodedPath.startsWith("/youtubei/") ||
            !request.url.isHttps ||
            request.header("Cookie").isNullOrBlank()
        ) {
            return request
        }"""

    patch_apply_to = """    fun applyTo(request: Request): Request {
        // [VoidTune Patch: Never send web cookies/auth to VISIONOS client (101)]
        // CAUSE: Apple VisionOS client (101) rejects web session credentials (SAPISID, X-Goog-PageId) with HTTP 400.
        // REASON: Keep VISIONOS requests clean and anonymous with matching origin so they succeed in <200ms.
        if (request.header("X-Youtube-Client-Name") == "101") {
            return request.newBuilder()
                .removeHeader("Cookie")
                .removeHeader("Authorization")
                .removeHeader("X-Goog-AuthUser")
                .removeHeader("X-Youtube-Bootstrap-Logged-In")
                .removeHeader("X-Goog-PageId")
                .header("Origin", "https://${request.url.host}")
                .header("X-Origin", "https://${request.url.host}")
                .build()
        }

        if (request.url.host !in AUTHENTICATED_HOSTS ||
            !request.url.encodedPath.startsWith("/youtubei/") ||
            !request.url.isHttps ||
            request.header("Cookie").isNullOrBlank()
        ) {
            return request
        }"""

    if "X-Youtube-Client-Name" not in auth_content and old_apply_to in auth_content:
        auth_content = auth_content.replace(old_apply_to, patch_apply_to, 1)
        with open(mori_auth_file, "w", encoding="utf-8") as f:
            f.write(auth_content)
        print("Applied YoutubeiRequestAuthentication VISIONOS isolation patch.")

# --- Patch 5: morideobfuscator YoutubeiHttpClient Origin host match ---
if os.path.exists(mori_client_file):
    with open(mori_client_file, "r", encoding="utf-8") as f:
        mori_client_content = f.read()

    old_build_request_header = """        headers?.keys()?.forEach { name ->
            val value = headers.optString(name)
            if (isAllowedHeader(name, value)) builder.header(name, value)
        }"""

    patch_build_request_header = """        headers?.keys()?.forEach { name ->
            val value = headers.optString(name)
            if (isAllowedHeader(name, value)) {
                if (name.equals("origin", ignoreCase = true) || name.equals("x-origin", ignoreCase = true)) {
                    builder.header(name, "https://${url.host}")
                } else {
                    builder.header(name, value)
                }
            }
        }"""

    if "https://${url.host}" not in mori_client_content and old_build_request_header in mori_client_content:
        mori_client_content = mori_client_content.replace(old_build_request_header, patch_build_request_header, 1)
        with open(mori_client_file, "w", encoding="utf-8") as f:
            f.write(mori_client_content)
        print("Applied YoutubeiHttpClient Origin host match patch.")


