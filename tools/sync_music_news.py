#!/usr/bin/env python3
"""
VoidTune Dynamic Music News & Developer DevLog Sync Tool
Powered by Scrapling (impersonate='chrome')
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
    text = text.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ')
    return re.sub(r'\s+', ' ', text).strip()

def fetch_billboard_trends(limit=4):
    print("Scraping Billboard Music News with Scrapling...")
    stories = []
    seen_urls = set()
    
    try:
        page = Fetcher.get(
            "https://www.billboard.com/c/music/music-news/",
            impersonate="chrome",
            timeout=15
        )
        cards = page.css(".story")
        
        for c in cards:
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
                "title": a_info[0],
                "url": a_info[1],
                "image": img_url
            })
            if len(stories) >= limit:
                break
    except Exception as e:
        print(f"Error fetching Billboard: {e}")
        
    return stories

def fetch_story_content(url: str, title: str) -> tuple[str, str]:
    """
    Returns (description_excerpt, full_markdown_body)
    """
    try:
        detail = Fetcher.get(url, impersonate="chrome", timeout=15)
        paragraphs = []
        
        for p in detail.css(".pmc-paywall p, .article-content p"):
            # Use inner text of paragraph
            raw_text = "".join(p.css("::text").getall())
            clean_p = sanitize_text(raw_text)
            if len(clean_p) > 30:
                if any(bad in clean_p for bad in [
                    "Trending on", "Sign up for", "Get weekly rundowns", "Click here to read"
                ]):
                    continue
                paragraphs.append(clean_p)
                
        if not paragraphs:
            paragraphs = ["Read the full article and music trends on Billboard."]
            
        excerpt = paragraphs[0]
        if len(excerpt) > 130:
            excerpt = excerpt[:127] + "..."
            
        md_lines = [
            f"# {title}",
            "",
            "> 🎵 *Curated Music Trends from Billboard Music News*",
            ""
        ]
        for p in paragraphs:
            md_lines.append(p)
            md_lines.append("")
            
        md_lines.append("---")
        md_lines.append(f"*Source: [Billboard Music]({url}) · Synced for VoidTune*")
        
        return excerpt, "\n".join(md_lines)
    except Exception as e:
        print(f"Error extracting story content from {url}: {e}")
        return "Trending music updates from global charts.", f"# {title}\n\nRead more at {url}"

def sync_news(limit=4):
    os.makedirs(CONTENT_DIR, exist_ok=True)
    
    # 1. Load existing metadata
    existing_items = []
    if os.path.exists(METADATA_FILE):
        try:
            with open(METADATA_FILE, "r", encoding="utf-8") as f:
                existing_items = json.load(f)
        except Exception as e:
            print(f"Could not load existing metadata: {e}")
            
    # 2. Extract developer announcements (PINNED)
    dev_announcements = [
        item for item in existing_items
        if item.get("Author") == "Mjurran" or item.get("Important") is True or item.get("id", "").startswith("voidtune_")
    ]
    
    # If no dev announcements found, create initial default one
    if not dev_announcements:
        dev_announcements = [{
            "id": "voidtune_arc_v1",
            "Title": "VoidTune Arcane Edition — The Rebirth",
            "Description": "Optimized network throughput for slow connections and completely overhauled the UI for a fluid, lag-free experience.",
            "ImageURL": [],
            "Important": True,
            "Author": "Mjurran",
            "Date": 1795450000
        }]
        
    print(f"Preserving {len(dev_announcements)} pinned developer announcement(s).")
    
    # 3. Fetch latest music stories using Scrapling
    stories = fetch_billboard_trends(limit=limit)
    print(f"Found {len(stories)} stories to sync.")
    
    music_items = []
    now = int(time.time())
    
    for i, s in enumerate(stories):
        url_hash = hashlib.md5(s["url"].encode()).hexdigest()[:10]
        item_id = f"trend_billboard_{url_hash}"
        content_path = os.path.join(CONTENT_DIR, item_id)
        
        print(f"[{i+1}/{len(stories)}] Processing: {s['title']}")
        excerpt, full_md = fetch_story_content(s["url"], s["title"])
        
        # Save content file
        with open(content_path, "w", encoding="utf-8") as f:
            f.write(full_md)
            
        music_items.append({
            "id": item_id,
            "Title": f"🎵 {s['title']}",
            "Description": excerpt,
            "ImageURL": [s["image"]] if s["image"] else [],
            "Important": False,
            "Author": "Billboard Trends",
            "Date": now - (i * 120)  # staggered so ordering is consistent
        })
        time.sleep(1)  # polite delay
        
    # 4. Combine pinned dev announcements + fresh music trends
    final_items = dev_announcements + music_items
    
    # 5. Write metadata.json
    with open(METADATA_FILE, "w", encoding="utf-8") as f:
        json.dump(final_items, f, indent=2, ensure_ascii=False)
        
    print(f"\nSuccessfully synced {len(final_items)} total catalog items to {METADATA_FILE}!")
    for item in final_items:
        print(f" - [{item.get('Author')}] {item.get('Title')} (Images: {len(item.get('ImageURL', []))})")

if __name__ == "__main__":
    sync_news(limit=4)
