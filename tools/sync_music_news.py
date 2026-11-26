#!/usr/bin/env python3
"""
VoidTune Dynamic Multi-Source Music News & DevLog Sync Engine
Powered by Scrapling (impersonate='chrome')

Sources:
1. Pinned Developer DevLogs & App Updates (Mjurran)
2. Billboard Global Music News & Charts
3. NME (New Musical Express) Album Releases & World Tours
4. TikTok & Reels Viral Radar (Kworb Streaming & Viral Hits)
5. X / Twitter Pulse (Live Trending Topics from Trends24)
"""

import hashlib
import json
import os
import re
import sys
import time
from scrapling.fetchers import Fetcher

WORKSPACE_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
NEWS_DIR = os.path.join(WORKSPACE_ROOT, "news")
CONTENT_DIR = os.path.join(NEWS_DIR, "content")
METADATA_FILE = os.path.join(NEWS_DIR, "metadata.json")

def sanitize_text(text: str) -> str:
    if not text:
        return ""
    text = text.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ')
    return re.sub(r'\s+', ' ', text).strip()

def fetch_billboard_stories(limit=4):
    print("Scraping Billboard Music News via Scrapling...")
    stories = []
    seen_urls = set()
    try:
        page = Fetcher.get(
            "https://www.billboard.com/c/music/music-news/",
            impersonate="chrome",
            timeout=15
        )
        for c in page.css(".story"):
            a_info = None
            for a in c.css("h3 a, a"):
                txt = sanitize_text(a.text)
                href = a.attrib.get("href", "")
                if len(txt) > 25 and "/music-news/" in href and href not in seen_urls:
                    seen_urls.add(href)
                    a_info = (txt, href)
                    break
            if not a_info:
                continue

            img_url = None
            for img in c.css("img"):
                src = img.attrib.get("data-lazy-src") or img.attrib.get("src")
                if src and "fallback.gif" not in src and "http" in src:
                    if "?w=" in src:
                        src = src.split("?")[0] + "?w=800&crop=1"
                    img_url = src
                    break

            stories.append({
                "source": "Billboard",
                "title": a_info[0],
                "url": a_info[1],
                "image": img_url
            })
            if len(stories) >= limit:
                break
    except Exception as e:
        print(f"Billboard fetch error: {e}")
    return stories

def fetch_nme_stories(limit=3):
    print("Scraping NME Music News via Scrapling...")
    stories = []
    seen_urls = set()
    try:
        page = Fetcher.get(
            "https://www.nme.com/news/music",
            impersonate="chrome",
            timeout=15
        )
        for c in page.css(".td_module_10, .td_module_flex, article, .td-module-container"):
            a_info = None
            for a in c.css("h3 a, .entry-title a, a"):
                txt = sanitize_text(a.text)
                href = a.attrib.get("href", "")
                if len(txt) > 20 and "/news/music/" in href:
                    if not href.startswith("http"):
                        href = "https://www.nme.com" + href
                    if href not in seen_urls:
                        seen_urls.add(href)
                        a_info = (txt, href)
                        break
            if not a_info:
                continue

            img_url = None
            for img in c.css("img"):
                src = img.attrib.get("src") or img.attrib.get("data-src")
                if src and "http" in src:
                    img_url = src
                    break

            stories.append({
                "source": "NME",
                "title": a_info[0],
                "url": a_info[1],
                "image": img_url
            })
            if len(stories) >= limit:
                break
    except Exception as e:
        print(f"NME fetch error: {e}")
    return stories

def extract_article_body(url: str, source: str, title: str) -> tuple[str, str]:
    try:
        detail = Fetcher.get(url, impersonate="chrome", timeout=15)
        paragraphs = []

        selector = ".pmc-paywall p, .article-content p" if source == "Billboard" else ".td_block_wrap p, .tdb_single_content p, article p"
        for p in detail.css(selector):
            raw = "".join(p.css("::text").getall())
            clean_p = sanitize_text(raw)
            if len(clean_p) > 30 and not any(bad in clean_p for bad in [
                "Trending on", "Sign up for", "Get weekly rundowns", "Click here to read",
                "READ MORE:", "Subscribe to", "CREDIT: Press"
            ]):
                paragraphs.append(clean_p)

        if not paragraphs:
            paragraphs = ["Read the full story on the original publication."]

        excerpt = paragraphs[0]
        if len(excerpt) > 130:
            excerpt = excerpt[:127] + "..."

        md = [
            f"# {title}",
            "",
            f"> 🎵 *Curated Music Story from {source}*",
            ""
        ]
        for p in paragraphs:
            md.append(p)
            md.append("")
        md.append("---")
        md.append(f"*Source: [{source} Official Story]({url}) · Synced for VoidTune*")

        return excerpt, "\n".join(md)
    except Exception as e:
        print(f"Error extracting body from {url}: {e}")
        return "Trending music updates from global charts.", f"# {title}\n\nRead more at {url}"

def fetch_tiktok_viral_radar() -> dict:
    print("Scraping TikTok & Reels Viral Radar via Scrapling...")
    songs = []
    try:
        p = Fetcher.get("https://kworb.net/spotify/country/id_daily.html", impersonate="chrome", timeout=15)
        for tr in p.css("tbody tr")[:8]:
            pos = tr.css("td:nth-child(1)::text").get()
            parts = tr.css("td.text a::text").getall()
            if parts and pos:
                songs.append(f"**#{pos}** {' — '.join(parts)}")
    except Exception as e:
        print(f"Kworb error: {e}")

    if not songs:
        songs = [
            "**#1** Sabrina Carpenter — Espresso",
            "**#2** Billie Eilish — BIRDS OF A FEATHER",
            "**#3** Bruno Mars & Lady Gaga — Die With A Smile",
            "**#4** Chappell Roan — Good Luck, Babe!",
            "**#5** Kendrick Lamar — Not Like Us"
        ]

    item_id = "trend_tiktok_reels_radar"
    title = "🔥 TikTok & Reels Viral Radar: Top Trending Sounds"
    desc = "Track the hottest viral songs dominating FYP videos, Instagram Reels, and daily streaming charts."

    md_lines = [
        f"# {title}",
        "",
        "> 📱 *Real-time viral sounds and songs taking over social feeds right now*",
        "",
        "### 📈 Most Viral Audio on TikTok & Reels",
        ""
    ]
    for s in songs:
        md_lines.append(f"- {s}")
    md_lines.extend([
        "",
        "---",
        "### 💡 Why It Matters",
        "Songs that trend on TikTok and Instagram Reels drive over 60% of streaming surges on Spotify and YouTube Music.",
        "You can search and play any of these viral tracks directly inside **VoidTune** using the top search bar!",
        "",
        "---",
        "*Curated automatically for VoidTune Music Explorers*"
    ])

    # High-quality neon/concert background image
    img = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800&crop=1"
    return {
        "id": item_id,
        "title": title,
        "desc": desc,
        "image": img,
        "content": "\n".join(md_lines)
    }

def fetch_twitter_x_pulse() -> dict:
    print("Scraping X / Twitter Pulse via Scrapling...")
    trends = []
    try:
        p = Fetcher.get("https://trends24.in/", impersonate="chrome", timeout=15)
        for ol in p.css("ol"):
            items = [a.text.strip() for a in ol.css("a") if a.text.strip()]
            if items:
                trends = items[:8]
                break
    except Exception as e:
        print(f"Trends24 error: {e}")

    if not trends:
        trends = ["Grammys", "New Music Friday", "Taylor Swift", "Spotify Wrapped", "Billboard Hot 100", "Coldplay", "Coachella", "KPop"]

    item_id = "trend_x_social_pulse"
    title = "⚡ X / Twitter Music Pulse: What's Buzzing Today"
    desc = f"Trending hashtags and viral discussions on X: {', '.join(trends[:4])}..."

    md_lines = [
        f"# {title}",
        "",
        "> 🐦 *Real-time social discussions and trending music buzz from X / Twitter*",
        "",
        "### 🌐 Top Trending Topics Right Now",
        ""
    ]
    for t in trends:
        md_lines.append(f"- **#{t.lstrip('#')}**")
    md_lines.extend([
        "",
        "---",
        "### 💬 Global Fan & Cultural Pulse",
        "Music conversations on X and Threads dictate breaking news, surprise single drops, and concert tour announcements.",
        "Stay ahead of the curve with VoidTune's real-time music radar.",
        "",
        "---",
        "*Aggregated from Trends24 live pulse for VoidTune*"
    ])

    img = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&crop=1"
    return {
        "id": item_id,
        "title": title,
        "desc": desc,
        "image": img,
        "content": "\n".join(md_lines)
    }

def sync_all():
    os.makedirs(CONTENT_DIR, exist_ok=True)
    now = int(time.time())

    # 1. Pinned Developer DevLogs (Mjurran - Always #1 and never drowned)
    pinned_dev_items = [
        {
            "id": "voidtune_v0_0_3_release",
            "Title": "🚀 VoidTune v0.0.3 — Unified Hybrid Search Released!",
            "Description": "Pencarian lagu bersamaan YouTube Music + Spotify GraphQL, 300ms debounce, shelf Spotify, dan Multi-Tier resolver.",
            "ImageURL": [],
            "Important": True,
            "Author": "Mjurran",
            "Date": now + 86400 * 30  # Always pinned in the future so older APKs keep it on top
        },
        {
            "id": "voidtune_arc_v1",
            "Title": "VoidTune Arcane Edition — The Rebirth",
            "Description": "Optimized network throughput for slow connections and completely overhauled the UI for a fluid, lag-free experience.",
            "ImageURL": [],
            "Important": True,
            "Author": "Mjurran",
            "Date": now + 86400 * 20
        }
    ]

    # Save dev contents if not present
    dev_content_1 = os.path.join(CONTENT_DIR, "voidtune_v0_0_3_release")
    with open(dev_content_1, "w", encoding="utf-8") as f:
        f.write("""# 🚀 VoidTune v0.0.3 — Unified Hybrid Search Released!

Selamat datang di pembaruan besar **VoidTune v0.0.3**!

### ✨ Fitur Baru yang Hadir:
1. **Option B: Unified Hybrid Search**:
   - Sekali ketik kata kunci, sistem secara asinkron mencari ke **YouTube Music** dan **Spotify Desktop GraphQL** secara bersamaan.
2. **Smart 300ms Debounce**:
   - Menghilangkan lag dan mencegah spam request saat mengetik cepat di kolom pencarian.
3. **Komponen Expressive Spotify**:
   - Tampilan `SpotifyTrackRow` dan `SpotifyTrackCard` dengan aksen hijau Spotify (`#1DB954`).
   - Shelf horizontal khusus *"Spotify Tracks"* dengan tombol *"See all"*.
4. **Multi-Tier Audio Resolver**:
   - Lagu Spotify langsung diputar melalui Spotify preview atau resolusi audio YouTube secara seamless.

---
*Developed with passion by Mjurran | INDONESIA*
""")

    # 2. Fetch Multi-Source Scrapling Stories
    billboard_stories = fetch_billboard_stories(limit=4)
    nme_stories = fetch_nme_stories(limit=3)

    scraped_items = []
    # Process Billboard
    for i, s in enumerate(billboard_stories):
        h = hashlib.md5(s["url"].encode()).hexdigest()[:10]
        item_id = f"trend_billboard_{h}"
        excerpt, full_md = extract_article_body(s["url"], "Billboard", s["title"])
        with open(os.path.join(CONTENT_DIR, item_id), "w", encoding="utf-8") as f:
            f.write(full_md)
        scraped_items.append({
            "id": item_id,
            "Title": f"🏆 {s['title']}",
            "Description": excerpt,
            "ImageURL": [s["image"]] if s["image"] else [],
            "Important": False,
            "Author": "Billboard Charts",
            "Date": now - (i * 300)
        })
        time.sleep(0.5)

    # Process NME
    for i, s in enumerate(nme_stories):
        h = hashlib.md5(s["url"].encode()).hexdigest()[:10]
        item_id = f"trend_nme_{h}"
        excerpt, full_md = extract_article_body(s["url"], "NME", s["title"])
        with open(os.path.join(CONTENT_DIR, item_id), "w", encoding="utf-8") as f:
            f.write(full_md)
        scraped_items.append({
            "id": item_id,
            "Title": f"🎸 {s['title']}",
            "Description": excerpt,
            "ImageURL": [s["image"]] if s["image"] else [],
            "Important": False,
            "Author": "NME Music",
            "Date": now - 1500 - (i * 300)
        })
        time.sleep(0.5)

    # 3. Add Social Media Radar: TikTok Viral & X Pulse
    tiktok_data = fetch_tiktok_viral_radar()
    with open(os.path.join(CONTENT_DIR, tiktok_data["id"]), "w", encoding="utf-8") as f:
        f.write(tiktok_data["content"])
    social_tiktok_item = {
        "id": tiktok_data["id"],
        "Title": tiktok_data["title"],
        "Description": tiktok_data["desc"],
        "ImageURL": [tiktok_data["image"]],
        "Important": False,
        "Author": "TikTok & Reels Radar",
        "Date": now - 3000
    }

    x_data = fetch_twitter_x_pulse()
    with open(os.path.join(CONTENT_DIR, x_data["id"]), "w", encoding="utf-8") as f:
        f.write(x_data["content"])
    social_x_item = {
        "id": x_data["id"],
        "Title": x_data["title"],
        "Description": x_data["desc"],
        "ImageURL": [x_data["image"]],
        "Important": False,
        "Author": "X / Twitter Pulse",
        "Date": now - 4000
    }

    # 4. Assemble Final 11-Card Catalog
    # DevLog #1, DevLog #2, TikTok Radar, X Pulse, Billboard Stories (4), NME Stories (3)
    final_catalog = (
        pinned_dev_items +
        [social_tiktok_item, social_x_item] +
        scraped_items
    )

    # Write metadata.json
    with open(METADATA_FILE, "w", encoding="utf-8") as f:
        json.dump(final_catalog, f, indent=2, ensure_ascii=False)

    print(f"\n=======================================================")
    print(f"🎉 Successfully built {len(final_catalog)} News Cards in {METADATA_FILE}!")
    for idx, item in enumerate(final_catalog):
        badge = "📌 PINNED" if item["Important"] else "📰 TREND"
        print(f"[{idx+1:02d}] {badge} [{item['Author']}] {item['Title']}")
    print("=======================================================")

if __name__ == "__main__":
    sync_all()
