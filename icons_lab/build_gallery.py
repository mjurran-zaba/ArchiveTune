import base64
import os

ICONS_DIR = "/home/user/VoidTune/icons_lab"

icons_data = [
    {
        "id": "01",
        "file": "icon_01_singularity.png",
        "title": "The Sound Singularity",
        "concept": "Event Horizon & Radial Waveform",
        "desc": "Disk akresi audio 48-bar yang meliuk mengitari lubang hitam kosmik (Void) dengan starlight diamond di pusat singularitas.",
        "colors": ["#06B6D4", "#8B5CF6", "#EC4899", "#080511"]
    },
    {
        "id": "02",
        "file": "icon_02_monolith_v.png",
        "title": "Neon Monolith V",
        "concept": "Geometric Faceted Cyber Gem",
        "desc": "Monolith V 3D bersudut presisi dengan faset kristal Polar Cyan dan Cosmic Violet di atas grid frekuensi audio gelap.",
        "colors": ["#38BDF8", "#4F46E5", "#A855F7", "#070A12"]
    },
    {
        "id": "03",
        "file": "icon_03_sound_eclipse.png",
        "title": "Sound Eclipse",
        "concept": "Vinyl Microgrooves & Solar Corona",
        "desc": "Piringan hitam piringan vinyl minimalis berpadu gerhana suara hangat (Amber/Coral) dan jarum sonik laser diagonal.",
        "colors": ["#F59E0B", "#F43F5E", "#38BDF8", "#0C0D11"]
    },
    {
        "id": "05",
        "file": "icon_05_equalizer_v.png",
        "title": "Equalizer V-Pulse",
        "concept": "Spectrum Bars & Sonic Envelope",
        "desc": "21 bar spektrum equalizer dinamis yang membentuk siluet kurva V simetris dengan gradasi spektrum neon Cyan ke Amber.",
        "colors": ["#38BDF8", "#A855F7", "#F59E0B", "#08090E"]
    },
    {
        "id": "06",
        "file": "icon_06_nordic_prism.png",
        "title": "Nordic Hex Prism",
        "concept": "Frosted Hexagon & Negative Space V",
        "desc": "Prisma heksagonal Nordik isometrik dengan potongan faset es arctic teal, slate, dan ukiran V negatif beraksen cahaya.",
        "colors": ["#38BDF8", "#6366F1", "#1E293B", "#0A0E17"]
    },
]

cards_html = ""
for item in icons_data:
    path = os.path.join(ICONS_DIR, item["file"])
    with open(path, "rb") as f:
        b64 = base64.b64encode(f.read()).decode("utf-8")
    
    palette_dots = "".join([f'<span style="background: {c};" title="{c}"></span>' for c in item["colors"]])
    
    cards_html += f"""
    <div class="card">
        <div class="icon-wrap">
            <img src="data:image/png;base64,{b64}" alt="{item['title']}" />
        </div>
        <div class="info">
            <div class="tag">CONCEPT {item['id']}</div>
            <h3>{item['title']}</h3>
            <div class="concept-subtitle">{item['concept']}</div>
            <p>{item['desc']}</p>
            <div class="palette">{palette_dots}</div>
        </div>
    </div>
    """

html_content = f"""<!DOCTYPE html>
<html lang="id">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>VoidTune • Programmatic Icon Concepts (NumPy & PIL)</title>
<style>
    * {{
        box-sizing: border-box;
        margin: 0;
        padding: 0;
    }}
    body {{
        background: #06070a;
        color: #e2e8f0;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        padding: 40px 24px;
        min-height: 100vh;
    }}
    .header {{
        text-align: center;
        max-width: 800px;
        margin: 0 auto 48px auto;
    }}
    .badge {{
        display: inline-block;
        padding: 6px 14px;
        background: rgba(56, 189, 248, 0.12);
        color: #38bdf8;
        border: 1px solid rgba(56, 189, 248, 0.3);
        border-radius: 999px;
        font-size: 12px;
        font-weight: 600;
        letter-spacing: 1px;
        margin-bottom: 16px;
    }}
    h1 {{
        font-size: 32px;
        font-weight: 800;
        letter-spacing: -0.5px;
        background: linear-gradient(135deg, #ffffff 0%, #cbd5e1 50%, #94a3b8 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 12px;
    }}
    p.lead {{
        color: #94a3b8;
        font-size: 15px;
        line-height: 1.6;
    }}
    .grid {{
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
        gap: 28px;
        max-width: 1280px;
        margin: 0 auto;
    }}
    .card {{
        background: #0d111a;
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 24px;
        padding: 24px;
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
    }}
    .card:hover {{
        transform: translateY(-4px);
        border-color: rgba(56, 189, 248, 0.35);
        box-shadow: 0 16px 36px -10px rgba(56, 189, 248, 0.15);
    }}
    .icon-wrap {{
        width: 180px;
        height: 180px;
        margin-bottom: 24px;
        filter: drop-shadow(0 12px 24px rgba(0, 0, 0, 0.6));
    }}
    .icon-wrap img {{
        width: 100%;
        height: 100%;
        border-radius: 40px;
        object-fit: cover;
    }}
    .tag {{
        font-size: 11px;
        font-weight: 700;
        letter-spacing: 1px;
        color: #38bdf8;
        margin-bottom: 6px;
    }}
    h3 {{
        font-size: 20px;
        font-weight: 700;
        color: #f8fafc;
        margin-bottom: 4px;
    }}
    .concept-subtitle {{
        font-size: 12px;
        font-weight: 500;
        color: #a855f7;
        margin-bottom: 12px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }}
    p {{
        font-size: 13px;
        line-height: 1.5;
        color: #94a3b8;
        margin-bottom: 18px;
    }}
    .palette {{
        display: flex;
        gap: 8px;
        justify-content: center;
    }}
    .palette span {{
        width: 18px;
        height: 18px;
        border-radius: 50%;
        border: 2px solid #0d111a;
        box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.15);
    }}
</style>
</head>
<body>
    <div class="header">
        <span class="badge">NUMPY & PILLOW PROCEDURAL ART</span>
        <h1>VoidTune Icon Pack Concepts</h1>
        <p class="lead">Eksplorasi visual baru untuk ikon aplikasi VoidTune v0.0.2 yang digenerate murni menggunakan matematika NumPy dan rendering anti-aliased PIL (tanpa AI generation).</p>
    </div>
    <div class="grid">
        {cards_html}
    </div>
</body>
</html>
"""

with open(os.path.join(ICONS_DIR, "icons_showcase.html"), "w", encoding="utf-8") as f:
    f.write(html_content)

print("Created icons_showcase.html successfully!")
