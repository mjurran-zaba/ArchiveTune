import math
import os
import json
import numpy as np
from PIL import Image, ImageDraw, ImageFilter

SVG_DIR = "/home/user/VoidTune/IconPack/svg"
METADATA_FILE = "/home/user/VoidTune/IconPack/metadata.json"
APP_RES_DIR = "/home/user/VoidTune/app/src/main/res"

os.makedirs(SVG_DIR, exist_ok=True)

# ==============================================================================
# Helper to create squircle mask
# ==============================================================================
def create_squircle_mask(size, radius_ratio=0.44, power=4.5):
    y, x = np.ogrid[:size, :size]
    cx, cy = size / 2.0, size / 2.0
    r = size * radius_ratio
    dist = ((np.abs(x - cx) / r) ** power + (np.abs(y - cy) / r) ** power) ** (1.0 / power)
    edge_width = 3.0 / size
    mask = np.clip((1.0 - (dist - 1.0 + edge_width) / edge_width), 0.0, 1.0)
    return mask

# ==============================================================================
# 1. RENDER NEON MONOLITH V (100% ZERO PURPLE: Cyan + Teal + Amber Gold)
# ==============================================================================
def render_monolith_v_image(size=1024):
    bg = Image.new("RGBA", (size, size), (7, 14, 26, 255)) # #070E1A
    draw_bg = ImageDraw.Draw(bg)

    # Background audio frequency grid
    grid_color = (30, 41, 59, 140)
    scale = size / 1024.0
    for y_pos in range(int(120 * scale), int(900 * scale), int(40 * scale)):
        draw_bg.line([(100 * scale, y_pos), (924 * scale, y_pos)], fill=grid_color, width=max(1, int(2 * scale)))

    # Coords
    p_l_out = (220 * scale, 240 * scale)
    p_l_mid = (370 * scale, 240 * scale)
    p_center_notch = (512 * scale, 480 * scale)
    p_r_mid = (654 * scale, 240 * scale)
    p_r_out = (804 * scale, 240 * scale)
    p_bottom = (512 * scale, 810 * scale)
    p_spine = (512 * scale, 630 * scale)
    p_l_facet = (340 * scale, 520 * scale)
    p_r_facet = (684 * scale, 520 * scale)

    # Ambient Glow
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw_glow = ImageDraw.Draw(glow)
    draw_glow.polygon([p_l_out, p_l_mid, p_center_notch, p_r_mid, p_r_out, p_bottom], fill=(56, 189, 248, 120))
    glow = glow.filter(ImageFilter.GaussianBlur(radius=size * 0.05))
    bg.alpha_composite(glow)

    # Facets
    v_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw_v = ImageDraw.Draw(v_layer)

    # Left wing (Cyan & Teal)
    draw_v.polygon([p_l_out, p_l_mid, p_l_facet], fill=(56, 189, 248, 255))      # #38BDF8
    draw_v.polygon([p_l_out, p_l_facet, p_bottom], fill=(2, 132, 199, 255))      # #0284C7
    draw_v.polygon([p_l_mid, p_center_notch, p_spine, p_l_facet], fill=(14, 116, 144, 255)) # #0E7490
    draw_v.polygon([p_l_facet, p_spine, p_bottom], fill=(21, 94, 117, 255))      # #155E75

    # Right wing (Radiant Amber Gold)
    draw_v.polygon([p_r_mid, p_center_notch, p_spine, p_r_facet], fill=(245, 158, 11, 255)) # #F59E0B
    draw_v.polygon([p_r_facet, p_spine, p_bottom], fill=(217, 119, 6, 255))      # #D97706
    draw_v.polygon([p_r_out, p_r_mid, p_r_facet], fill=(251, 191, 36, 255))      # #FBBF24
    draw_v.polygon([p_r_out, p_r_facet, p_bottom], fill=(180, 83, 9, 255))       # #B45309

    # Laser Highlight lines
    draw_v.line([p_l_out, p_bottom], fill=(224, 242, 254, 220), width=max(1, int(3 * scale)))
    draw_v.line([p_spine, p_bottom], fill=(255, 255, 255, 255), width=max(1, int(4 * scale)))
    draw_v.line([p_center_notch, p_spine], fill=(254, 243, 199, 200), width=max(1, int(2 * scale)))
    draw_v.line([p_r_out, p_bottom], fill=(254, 243, 199, 220), width=max(1, int(3 * scale)))

    # Central glowing floating crystal
    beacon_cy = 380 * scale
    b_size = 18 * scale
    beacon_pts = [
        (512 * scale, beacon_cy - b_size),
        (512 * scale + b_size * 0.7, beacon_cy),
        (512 * scale, beacon_cy + b_size),
        (512 * scale - b_size * 0.7, beacon_cy)
    ]
    draw_v.polygon(beacon_pts, fill=(56, 189, 248, 255))
    draw_v.ellipse([512 * scale - 4*scale, beacon_cy - 4*scale, 512 * scale + 4*scale, beacon_cy + 4*scale], fill=(255, 255, 255, 255))

    bg.alpha_composite(v_layer)
    return bg

# ==============================================================================
# 2. GENERATE 4 NEW ICON SVGs
# ==============================================================================

# ICON A: void_cyber_cassette.svg (Cyberpunk Obsidian Cassette)
def gen_cyber_cassette_svg():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#060A14" />',
        '  <!-- Subtle Grid -->',
        '  <g stroke="#1E293B" stroke-width="1.5" stroke-opacity="0.4">',
    ]
    for y in range(160, 900, 80):
        lines.append(f'    <line x1="120" y1="{y}" x2="904" y2="{y}" />')
    lines.extend([
        '  </g>',
        '  <!-- Cassette Body Outer Shell -->',
        '  <rect x="180" y="240" width="664" height="544" rx="44" ry="44" fill="#0F172A" stroke="#38BDF8" stroke-width="6" />',
        '  <rect x="220" y="280" width="584" height="464" rx="28" ry="28" fill="#090E17" stroke="#1E293B" stroke-width="3" />',
        '  <!-- Cassette Label Area -->',
        '  <path d="M 260,330 L 764,330 L 764,570 L 260,570 Z" fill="#0284C7" fill-opacity="0.25" stroke="#0E7490" stroke-width="2" />',
        '  <!-- Spool Window -->',
        '  <rect x="330" y="380" width="364" height="150" rx="24" ry="24" fill="#060A14" stroke="#F59E0B" stroke-width="4" />',
        '  <!-- Left Tape Reel -->',
        '  <circle cx="410" cy="455" r="52" fill="#0F172A" stroke="#38BDF8" stroke-width="5" />',
        '  <circle cx="410" cy="455" r="26" fill="#38BDF8" />',
        '  <circle cx="410" cy="455" r="12" fill="#FFFFFF" />',
        '  <!-- Right Tape Reel -->',
        '  <circle cx="614" cy="455" r="52" fill="#0F172A" stroke="#F59E0B" stroke-width="5" />',
        '  <circle cx="614" cy="455" r="26" fill="#F59E0B" />',
        '  <circle cx="614" cy="455" r="12" fill="#FFFFFF" />',
        '  <!-- Connecting Tape Loop Bridge -->',
        '  <line x1="410" y1="507" x2="614" y2="507" stroke="#FBBF24" stroke-width="6" stroke-linecap="round" />',
        '  <!-- Bottom Trapezoid Head Mount -->',
        '  <polygon points="320,744 380,630 644,630 704,744" fill="#1E293B" stroke="#38BDF8" stroke-width="3" />',
        '  <circle cx="420" cy="690" r="14" fill="#38BDF8" />',
        '  <circle cx="604" cy="690" r="14" fill="#F59E0B" />',
        '  <rect x="472" y="660" width="80" height="54" rx="8" ry="8" fill="#060A14" stroke="#FBBF24" stroke-width="3" />',
        '</svg>'
    ])
    return "\n".join(lines)

# ICON B: void_valkyrie_wings.svg (Valkyrie Sound Wings)
def gen_valkyrie_wings_svg():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#070C16" />',
        '  <!-- Energy Halo Rings -->',
        '  <circle cx="512" cy="512" r="380" fill="none" stroke="#1E293B" stroke-width="2" stroke-opacity="0.4" />',
        '  <circle cx="512" cy="512" r="280" fill="none" stroke="#0E7490" stroke-width="2" stroke-dasharray="12,12" stroke-opacity="0.5" />',
        '  <!-- Left Wing Feathers (Cyan / Teal) -->',
        '  <polygon points="512,580 180,300 240,460" fill="#0284C7" />',
        '  <polygon points="512,620 220,440 290,560" fill="#0E7490" />',
        '  <polygon points="512,660 270,540 340,640" fill="#155E75" />',
        '  <polygon points="512,540 160,240 320,380" fill="#38BDF8" />',
        '  <!-- Right Wing Feathers (Amber Gold) -->',
        '  <polygon points="512,580 844,300 784,460" fill="#D97706" />',
        '  <polygon points="512,620 804,440 734,560" fill="#B45309" />',
        '  <polygon points="512,660 754,540 684,640" fill="#92400E" />',
        '  <polygon points="512,540 864,240 704,380" fill="#F59E0B" />',
        '  <!-- Central Spear Blade / Apex Core -->',
        '  <polygon points="512,200 472,680 512,820 552,680" fill="#0F172A" stroke="#FFFFFF" stroke-width="4" />',
        '  <line x1="512" y1="200" x2="512" y2="820" stroke="#38BDF8" stroke-width="4" />',
        '  <polygon points="512,280 492,340 512,400 532,340" fill="#FBBF24" />',
        '</svg>'
    ]
    return "\n".join(lines)

# ICON C: void_aurora_pulse.svg (Nordic Aurora Wave)
def gen_aurora_pulse_svg():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#050B14" />',
        '  <!-- Starlight dots -->',
        '  <circle cx="200" cy="220" r="3" fill="#FFFFFF" fill-opacity="0.8" />',
        '  <circle cx="820" cy="180" r="2.5" fill="#FFFFFF" fill-opacity="0.7" />',
        '  <circle cx="340" cy="160" r="4" fill="#FBBF24" fill-opacity="0.9" />',
        '  <circle cx="700" cy="260" r="3" fill="#38BDF8" fill-opacity="0.8" />',
        '  <circle cx="160" cy="400" r="2" fill="#FFFFFF" fill-opacity="0.5" />',
        '  <circle cx="860" cy="420" r="2.5" fill="#FFFFFF" fill-opacity="0.6" />',
        '  <!-- Aurora Wave Bands (Cyan to Emerald Green to Amber) -->',
        '  <path d="M 120,640 Q 320,280 512,520 T 904,400" fill="none" stroke="#10B981" stroke-width="38" stroke-opacity="0.4" stroke-linecap="round" />',
        '  <path d="M 120,640 Q 320,280 512,520 T 904,400" fill="none" stroke="#38BDF8" stroke-width="20" stroke-opacity="0.75" stroke-linecap="round" />',
        '  <path d="M 120,640 Q 320,280 512,520 T 904,400" fill="none" stroke="#E0F2FE" stroke-width="6" stroke-opacity="0.95" stroke-linecap="round" />',
        '  <!-- Lower Counter-Wave -->',
        '  <path d="M 120,740 Q 360,540 540,680 T 904,600" fill="none" stroke="#F59E0B" stroke-width="28" stroke-opacity="0.35" stroke-linecap="round" />',
        '  <path d="M 120,740 Q 360,540 540,680 T 904,600" fill="none" stroke="#FBBF24" stroke-width="12" stroke-opacity="0.7" stroke-linecap="round" />',
        '  <!-- Central Nordic Mountain Silhouette -->',
        '  <polygon points="120,840 380,560 512,680 660,510 904,840" fill="#090E17" stroke="#1E293B" stroke-width="4" />',
        '  <!-- Mountain Ridge Light Peak -->',
        '  <polyline points="380,560 512,680 660,510" fill="none" stroke="#38BDF8" stroke-width="5" stroke-linecap="round" />',
        '  <circle cx="660" cy="510" r="8" fill="#FBBF24" />',
        '</svg>'
    ]
    return "\n".join(lines)

# ICON D: void_quantum_core.svg (Quantum Resonance Core)
def gen_quantum_core_svg():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#060A12" />',
        '  <!-- Orbital Gyro Rings -->',
        '  <ellipse cx="512" cy="512" rx="360" ry="140" fill="none" stroke="#1E293B" stroke-width="3" stroke-opacity="0.5" transform="rotate(-30 512 512)" />',
        '  <ellipse cx="512" cy="512" rx="360" ry="140" fill="none" stroke="#0284C7" stroke-width="5" stroke-opacity="0.6" stroke-dasharray="24,18" transform="rotate(30 512 512)" />',
        '  <ellipse cx="512" cy="512" rx="360" ry="140" fill="none" stroke="#F59E0B" stroke-width="4" stroke-opacity="0.7" stroke-dasharray="40,24" transform="rotate(90 512 512)" />',
        '  <!-- Isometric Quantum Cube Core -->',
        '  <!-- Top Face (Polar Cyan) -->',
        '  <polygon points="512,300 660,380 512,460 364,380" fill="#38BDF8" stroke="#E0F2FE" stroke-width="3" />',
        '  <!-- Left Face (Deep Arctic Teal) -->',
        '  <polygon points="364,380 512,460 512,640 364,560" fill="#0369A1" stroke="#0284C7" stroke-width="3" />',
        '  <!-- Right Face (Amber Gold) -->',
        '  <polygon points="512,460 660,380 660,560 512,640" fill="#D97706" stroke="#F59E0B" stroke-width="3" />',
        '  <!-- Inner Glowing Reactor Orb -->',
        '  <circle cx="512" cy="460" r="46" fill="#FBBF24" stroke="#FFFFFF" stroke-width="5" />',
        '  <circle cx="512" cy="460" r="22" fill="#FFFFFF" />',
        '  <!-- Resonance Rays -->',
        '  <line x1="512" y1="180" x2="512" y2="280" stroke="#38BDF8" stroke-width="6" stroke-linecap="round" />',
        '  <line x1="512" y1="660" x2="512" y2="760" stroke="#F59E0B" stroke-width="6" stroke-linecap="round" />',
        '  <line x1="260" y1="460" x2="340" y2="460" stroke="#38BDF8" stroke-width="6" stroke-linecap="round" />',
        '  <line x1="684" y1="460" x2="764" y2="460" stroke="#F59E0B" stroke-width="6" stroke-linecap="round" />',
        '</svg>'
    ]
    return "\n".join(lines)

# Write SVGs
new_svgs = {
    "void_cyber_cassette.svg": gen_cyber_cassette_svg(),
    "void_valkyrie_wings.svg": gen_valkyrie_wings_svg(),
    "void_aurora_pulse.svg": gen_aurora_pulse_svg(),
    "void_quantum_core.svg": gen_quantum_core_svg(),
}

for fname, content in new_svgs.items():
    fpath = os.path.join(SVG_DIR, fname)
    with open(fpath, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generated new SVG: {fpath}")

# ==============================================================================
# 3. UPDATE IconPack metadata.json
# Neon Monolith V is set to index 0 (Top / Premier Icon), plus 4 new entries!
# ==============================================================================
with open(METADATA_FILE, "r", encoding="utf-8") as f:
    existing_meta = json.load(f)

# Keep other entries except monolith_v and newly created
clean_entries = [e for e in existing_meta if e["Id"] not in [
    "void_monolith_v", "void_cyber_cassette", "void_valkyrie_wings", "void_aurora_pulse", "void_quantum_core"
]]

monolith_entry = {
    "Id": "void_monolith_v",
    "Name": "Neon Monolith V",
    "Author": "mjurran-zaba",
    "Source": "void_monolith_v.svg",
    "Link": "https://github.com/mjurran-zaba/VoidTune",
    "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
    "BackgroundMode": "integrated",
    "BackgroundColor": "#FF070E1A"
}

added_entries = [
    {
        "Id": "void_cyber_cassette",
        "Name": "Cyber Obsidian Cassette",
        "Author": "mjurran-zaba",
        "Source": "void_cyber_cassette.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF060A14"
    },
    {
        "Id": "void_valkyrie_wings",
        "Name": "Valkyrie Sound Wings",
        "Author": "mjurran-zaba",
        "Source": "void_valkyrie_wings.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF070C16"
    },
    {
        "Id": "void_aurora_pulse",
        "Name": "Nordic Aurora Wave",
        "Author": "mjurran-zaba",
        "Source": "void_aurora_pulse.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF050B14"
    },
    {
        "Id": "void_quantum_core",
        "Name": "Quantum Resonance Core",
        "Author": "mjurran-zaba",
        "Source": "void_quantum_core.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF060A12"
    }
]

# Neon Monolith V is at position 0!
full_catalog = [monolith_entry] + clean_entries + added_entries

with open(METADATA_FILE, "w", encoding="utf-8") as f:
    json.dump(full_catalog, f, indent=2)

print(f"Updated {METADATA_FILE} with {len(full_catalog)} total icons! Neon Monolith V is default #1.")

# ==============================================================================
# 4. EXPORT NEON MONOLITH V TO APP MIPMAP LAUNCHER ICONS & DRAWABLES
# ==============================================================================
base_monolith = render_monolith_v_image(1024)

# Create launcher icon versions:
# - ic_launcher.png (squircle)
# - ic_launcher_round.png (circle)
# - ic_launcher_foreground.png (transparent foreground)
# - ic_launcher_background.png (solid background)

# Squircle
squircle_mask = create_squircle_mask(1024)
squircle_img = Image.fromarray(
    np.dstack([np.array(base_monolith.convert("RGB")), (squircle_mask * 255).astype(np.uint8)])
)

# Circle
circle_mask = Image.new("L", (1024, 1024), 0)
draw_c = ImageDraw.Draw(circle_mask)
draw_c.ellipse([20, 20, 1004, 1004], fill=255)
circle_img = Image.fromarray(
    np.dstack([np.array(base_monolith.convert("RGB")), np.array(circle_mask)])
)

# Foreground (Centered scaled down ~70% to fit within adaptive icon safe zone)
fg_canvas = Image.new("RGBA", (1024, 1024), (0, 0, 0, 0))
monolith_scaled = base_monolith.resize((716, 716), Image.Resampling.LANCZOS)
fg_canvas.paste(monolith_scaled, (154, 154), monolith_scaled)

# Background
bg_canvas = Image.new("RGBA", (1024, 1024), (7, 14, 26, 255))

# Export across all mipmap densities
densities = {
    "mipmap-mdpi": (48, 108),
    "mipmap-hdpi": (72, 162),
    "mipmap-xhdpi": (96, 216),
    "mipmap-xxhdpi": (144, 324),
    "mipmap-xxxhdpi": (192, 432),
}

for folder, (launcher_sz, adapt_sz) in densities.items():
    dir_path = os.path.join(APP_RES_DIR, folder)
    os.makedirs(dir_path, exist_ok=True)
    
    squircle_img.resize((launcher_sz, launcher_sz), Image.Resampling.LANCZOS).save(os.path.join(dir_path, "ic_launcher.png"), format="PNG")
    circle_img.resize((launcher_sz, launcher_sz), Image.Resampling.LANCZOS).save(os.path.join(dir_path, "ic_launcher_round.png"), format="PNG")
    fg_canvas.resize((adapt_sz, adapt_sz), Image.Resampling.LANCZOS).save(os.path.join(dir_path, "ic_launcher_foreground.png"), format="PNG")
    bg_canvas.resize((adapt_sz, adapt_sz), Image.Resampling.LANCZOS).save(os.path.join(dir_path, "ic_launcher_background.png"), format="PNG")
    fg_canvas.resize((adapt_sz, adapt_sz), Image.Resampling.LANCZOS).save(os.path.join(dir_path, "ic_launcher_monochrome.png"), format="PNG")
    print(f"Exported Neon Monolith V launcher icons to {folder}")

# Update branding drawables
drawable_dir = os.path.join(APP_RES_DIR, "drawable")
base_monolith.resize((512, 512), Image.Resampling.LANCZOS).save(os.path.join(drawable_dir, "voidtune_emblem.png"), format="PNG")
base_monolith.resize((512, 512), Image.Resampling.LANCZOS).save(os.path.join(drawable_dir, "voidtune_splash_seal.png"), format="PNG")
print("Updated voidtune_emblem.png and voidtune_splash_seal.png with Neon Monolith V!")
