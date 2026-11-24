import os

target_file = "core/src/main/kotlin/moe/rukamori/archivetune/innertube/InnerTube.kt"
if not os.path.exists(target_file):
    print(f"Target file {target_file} not found, skipping patch.")
    exit(0)

with open(target_file, "r", encoding="utf-8") as f:
    content = f.read()

# Add imports if not present
if "import okhttp3.ConnectionPool" not in content:
    content = content.replace(
        "import okhttp3.Dns",
        "import okhttp3.ConnectionPool\nimport okhttp3.Dispatcher\nimport okhttp3.Dns\nimport java.util.concurrent.TimeUnit",
    )

old_block = """                    addInterceptor(NetworkGatekeeper)
                    dns(this@InnerTube.dns)"""

patch_block = """                    addInterceptor(NetworkGatekeeper)
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

if "maxRequestsPerHost = 20" not in content and old_block in content:
    content = content.replace(old_block, patch_block, 1)

with open(target_file, "w", encoding="utf-8") as f:
    f.write(content)

print("Applied InnerTube concurrency patch successfully.")
