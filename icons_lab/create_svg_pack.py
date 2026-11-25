import json
import math
import os

OUTPUT_SVG_DIR = "/home/user/VoidTune/IconPack/svg"
METADATA_FILE = "/home/user/VoidTune/IconPack/metadata.json"

os.makedirs(OUTPUT_SVG_DIR, exist_ok=True)

# ------------------------------------------------------------------------------
# 1. void_singularity.svg
# ------------------------------------------------------------------------------
def generate_singularity_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <defs>')
    lines.append('    <radialGradient id="nebulaGlow" cx="50%" cy="50%" r="50%">')
    lines.append('      <stop offset="0%" stop-color="#2a1452" stop-opacity="0.85"/>')
    lines.append('      <stop offset="50%" stop-color="#160d2e" stop-opacity="0.4"/>')
    lines.append('      <stop offset="100%" stop-color="#080511" stop-opacity="0"/>')
    lines.append('    </radialGradient>')
    lines.append('  </defs>')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#080511" />')
    lines.append('  <circle cx="512" cy="512" r="480" fill="url(#nebulaGlow)" />')
    lines.append('  <!-- 48 Radial Waveform Bars -->')
    lines.append('  <g>')

    num_bars = 48
    inner_r = 184
    max_outer_r = 420
    for i in range(num_bars):
        k = i / float(num_bars)
        angle_deg = i * (360.0 / num_bars)
        amp = 0.55 + 0.35 * math.sin(k * 4 * math.pi) + 0.15 * math.cos(k * 10 * math.pi)
        amp = max(0.25, min(1.0, amp))
        bar_h = amp * (max_outer_r - inner_r)
        
        # Color interpolation: Cyan -> Violet -> Fuchsia -> Cyan
        t_col = (math.sin(k * 2 * math.pi) + 1.0) / 2.0
        if t_col < 0.5:
            r = int(6 * (1 - t_col*2) + 139 * (t_col*2))
            g = int(182 * (1 - t_col*2) + 92 * (t_col*2))
            b = int(212 * (1 - t_col*2) + 246 * (t_col*2))
        else:
            p = (t_col - 0.5) * 2
            r = int(139 * (1 - p) + 236 * p)
            g = int(92 * (1 - p) + 72 * p)
            b = int(246 * (1 - p) + 153 * p)
        hex_col = f"#{r:02x}{g:02x}{b:02x}"

        lines.append(f'    <rect x="504" y="{512 - inner_r - bar_h:.1f}" width="16" height="{bar_h:.1f}" rx="6" ry="6" fill="{hex_col}" transform="rotate({angle_deg:.1f}, 512, 512)" />')

    lines.append('  </g>')
    lines.append('  <!-- Central Event Horizon -->')
    lines.append('  <circle cx="512" cy="512" r="190" fill="none" stroke="#8B5CF6" stroke-width="4" stroke-opacity="0.4" />')
    lines.append('  <circle cx="512" cy="512" r="184" fill="#030208" stroke="#06B6D4" stroke-width="4" stroke-opacity="0.85" />')
    lines.append('  <!-- Void Center Starlight -->')
    lines.append('  <polygon points="512,460 522,502 564,512 522,522 512,564 502,522 460,512 502,502" fill="#E0F2FE" />')
    lines.append('  <circle cx="512" cy="512" r="8" fill="#FFFFFF" />')
    lines.append('</svg>')
    return "\n".join(lines)

# ------------------------------------------------------------------------------
# 2. void_monolith_v.svg
# ------------------------------------------------------------------------------
def generate_monolith_v_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#070A12" />')
    lines.append('  <!-- Audio Grid Lines -->')
    for y_pos in range(120, 900, 40):
        lines.append(f'  <line x1="100" y1="{y_pos}" x2="924" y2="{y_pos}" stroke="#192134" stroke-width="2" stroke-opacity="0.6" />')
    
    lines.append('  <!-- 3D Monolith Facets -->')
    lines.append('  <polygon points="220,240 370,240 340,520" fill="#38BDF8" />')
    lines.append('  <polygon points="220,240 340,520 512,810" fill="#0284C7" />')
    lines.append('  <polygon points="370,240 512,480 512,630 340,520" fill="#4F46E5" />')
    lines.append('  <polygon points="340,520 512,630 512,810" fill="#4338CA" />')
    lines.append('  <polygon points="654,240 512,480 512,630 684,520" fill="#8B5CF6" />')
    lines.append('  <polygon points="684,520 512,630 512,810" fill="#6D28D9" />')
    lines.append('  <polygon points="804,240 654,240 684,520" fill="#A855F7" />')
    lines.append('  <polygon points="804,240 684,520 512,810" fill="#7E22CE" />')
    lines.append('  <!-- Laser Highlights -->')
    lines.append('  <line x1="220" y1="240" x2="512" y2="810" stroke="#E0F2FE" stroke-width="3" stroke-opacity="0.8" />')
    lines.append('  <line x1="512" y1="630" x2="512" y2="810" stroke="#FFFFFF" stroke-width="4" stroke-opacity="0.95" />')
    lines.append('  <line x1="512" y1="480" x2="512" y2="630" stroke="#D8B4FE" stroke-width="2" stroke-opacity="0.75" />')
    lines.append('  <line x1="370" y1="240" x2="340" y2="520" stroke="#BAE6FD" stroke-width="2" stroke-opacity="0.65" />')
    lines.append('  <line x1="654" y1="240" x2="684" y2="520" stroke="#E9D5FF" stroke-width="2" stroke-opacity="0.65" />')
    lines.append('  <!-- Floating Beacon -->')
    lines.append('  <polygon points="512,362 525,380 512,398 499,380" fill="#38BDF8" />')
    lines.append('  <circle cx="512" cy="380" r="4" fill="#FFFFFF" />')
    lines.append('</svg>')
    return "\n".join(lines)

# ------------------------------------------------------------------------------
# 3. void_sound_eclipse.svg
# ------------------------------------------------------------------------------
def generate_sound_eclipse_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#0C0D11" />')
    lines.append('  <!-- Vinyl Microgrooves -->')
    for r in range(120, 450, 8):
        lines.append(f'  <circle cx="471" cy="491" r="{r}" fill="none" stroke="#262A35" stroke-width="1.2" stroke-opacity="0.35" />')
    
    lines.append('  <!-- Solar Corona Arc (Amber/Coral/Violet) -->')
    # Center (532, 512), radius ~340, arc from -45 to 135 deg
    # In SVG path, arc from (772, 271) to (291, 752)
    lines.append('  <path d="M 772,271 A 340,340 0 0,1 291,752" fill="none" stroke="#F59E0B" stroke-width="36" stroke-opacity="0.5" stroke-linecap="round" />')
    lines.append('  <path d="M 772,271 A 340,340 0 0,1 291,752" fill="none" stroke="#F43F5E" stroke-width="18" stroke-opacity="0.8" stroke-linecap="round" />')
    lines.append('  <path d="M 772,271 A 340,340 0 0,1 291,752" fill="none" stroke="#FEF3C7" stroke-width="6" stroke-opacity="0.95" stroke-linecap="round" />')

    lines.append('  <!-- Occluding Void Disc -->')
    lines.append('  <circle cx="471" cy="491" r="327" fill="#0A0B0F" />')
    lines.append('  <circle cx="471" cy="491" r="41" fill="#161820" stroke="#38BDF8" stroke-width="2" stroke-opacity="0.6" />')

    lines.append('  <!-- Minimalist Stylus Needle Beam V -->')
    lines.append('  <line x1="328" y1="164" x2="594" y2="860" stroke="#38BDF8" stroke-width="8" stroke-linecap="round" />')
    lines.append('  <line x1="594" y1="860" x2="860" y2="286" stroke="#F59E0B" stroke-width="8" stroke-linecap="round" />')
    lines.append('  <circle cx="594" cy="860" r="28" fill="#38BDF8" fill-opacity="0.25" />')
    lines.append('  <circle cx="594" cy="860" r="14" fill="#FFFFFF" />')
    lines.append('</svg>')
    return "\n".join(lines)

# ------------------------------------------------------------------------------
# 4. void_equalizer_v.svg
# ------------------------------------------------------------------------------
def generate_equalizer_v_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#08090E" />')
    lines.append('  <!-- Horizon Center Line -->')
    lines.append('  <line x1="120" y1="512" x2="904" y2="512" stroke="#FFFFFF" stroke-width="2" stroke-opacity="0.2" />')

    num_bars = 21
    spacing = 35.0
    bar_w = 24.0
    start_x = 512.0 - (num_bars * spacing) / 2.0 + spacing / 2.0

    lines.append('  <!-- 21 Equalizer Pill Bars -->')
    for i in range(num_bars):
        bx = start_x + i * spacing
        t = (i - 10) / 10.0
        base_h = 1024 * (0.15 + 0.55 * (abs(t) ** 1.35))
        jitter = 40.0 * math.sin(i * 1.5)
        h = base_h + jitter
        top_y = 512.0 - h / 2.0

        norm_i = i / float(num_bars - 1)
        if norm_i < 0.5:
            k = norm_i * 2
            r = int(56 * (1 - k) + 168 * k)
            g = int(189 * (1 - k) + 85 * k)
            b = int(248 * (1 - k) + 247 * k)
        else:
            k = (norm_i - 0.5) * 2
            r = int(168 * (1 - k) + 245 * k)
            g = int(85 * (1 - k) + 158 * k)
            b = int(247 * (1 - k) + 11 * k)
        hex_col = f"#{r:02x}{g:02x}{b:02x}"

        lines.append(f'  <rect x="{bx - bar_w/2:.1f}" y="{top_y:.1f}" width="{bar_w:.1f}" height="{h:.1f}" rx="12" ry="12" fill="{hex_col}" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ------------------------------------------------------------------------------
# 5. void_nordic_prism.svg
# ------------------------------------------------------------------------------
def generate_nordic_prism_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#0A0E17" />')

    cx, cy = 512.0, 512.0
    scale = 389.0
    angles = [math.radians(a) for a in range(30, 390, 60)]
    pts = [(cx + scale * math.cos(a), cy + scale * math.sin(a)) for a in angles]

    lines.append('  <!-- Hexagon Facets -->')
    lines.append(f'  <polygon points="{pts[5][0]:.1f},{pts[5][1]:.1f} {pts[0][0]:.1f},{pts[0][1]:.1f} {cx},{cy}" fill="#1E293B" />')
    lines.append(f'  <polygon points="{pts[0][0]:.1f},{pts[0][1]:.1f} {pts[1][0]:.1f},{pts[1][1]:.1f} {cx},{cy}" fill="#38BDF8" />')
    lines.append(f'  <polygon points="{pts[1][0]:.1f},{pts[1][1]:.1f} {pts[2][0]:.1f},{pts[2][1]:.1f} {cx},{cy}" fill="#0E7490" />')
    lines.append(f'  <polygon points="{pts[2][0]:.1f},{pts[2][1]:.1f} {pts[3][0]:.1f},{pts[3][1]:.1f} {cx},{cy}" fill="#6366F1" />')
    lines.append(f'  <polygon points="{pts[3][0]:.1f},{pts[3][1]:.1f} {pts[4][0]:.1f},{pts[4][1]:.1f} {cx},{cy}" fill="#818CF8" />')
    lines.append(f'  <polygon points="{pts[4][0]:.1f},{pts[4][1]:.1f} {pts[5][0]:.1f},{pts[5][1]:.1f} {cx},{cy}" fill="#475569" />')

    # Negative Space Sound "V" carving
    v_p0 = (cx - scale * 0.5, cy - scale * 0.45)
    v_p1 = (cx - scale * 0.2, cy - scale * 0.45)
    v_p2 = (cx, cy + scale * 0.35)
    v_p3 = (cx + scale * 0.2, cy - scale * 0.45)
    v_p4 = (cx + scale * 0.5, cy - scale * 0.45)
    v_p5 = (cx, cy + scale * 0.75)

    lines.append(f'  <polygon points="{v_p0[0]:.1f},{v_p0[1]:.1f} {v_p1[0]:.1f},{v_p1[1]:.1f} {v_p2[0]:.1f},{v_p2[1]:.1f} {v_p3[0]:.1f},{v_p3[1]:.1f} {v_p4[0]:.1f},{v_p4[1]:.1f} {v_p5[0]:.1f},{v_p5[1]:.1f}" fill="#0A0E17" />')
    lines.append(f'  <line x1="{v_p0[0]:.1f}" y1="{v_p0[1]:.1f}" x2="{v_p5[0]:.1f}" y2="{v_p5[1]:.1f}" stroke="#E0F2FE" stroke-width="8" stroke-linecap="round" />')
    lines.append(f'  <line x1="{v_p5[0]:.1f}" y1="{v_p5[1]:.1f}" x2="{v_p4[0]:.1f}" y2="{v_p4[1]:.1f}" stroke="#E0F2FE" stroke-width="8" stroke-linecap="round" />')
    lines.append(f'  <line x1="{v_p1[0]:.1f}" y1="{v_p1[1]:.1f}" x2="{v_p2[0]:.1f}" y2="{v_p2[1]:.1f}" stroke="#38BDF8" stroke-width="8" stroke-linecap="round" />')
    lines.append(f'  <line x1="{v_p2[0]:.1f}" y1="{v_p2[1]:.1f}" x2="{v_p3[0]:.1f}" y2="{v_p3[1]:.1f}" stroke="#38BDF8" stroke-width="8" stroke-linecap="round" />')
    lines.append(f'  <circle cx="{cx}" cy="{cy - scale * 0.1:.1f}" r="8" fill="#FFFFFF" />')
    lines.append('</svg>')
    return "\n".join(lines)

# ------------------------------------------------------------------------------
# MAIN
# ------------------------------------------------------------------------------
svg_definitions = [
    ("void_singularity.svg", generate_singularity_svg()),
    ("void_monolith_v.svg", generate_monolith_v_svg()),
    ("void_sound_eclipse.svg", generate_sound_eclipse_svg()),
    ("void_equalizer_v.svg", generate_equalizer_v_svg()),
    ("void_nordic_prism.svg", generate_nordic_prism_svg()),
]

for filename, content in svg_definitions:
    path = os.path.join(OUTPUT_SVG_DIR, filename)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generated SVG: {path} ({len(content)} chars)")

# Metadata definitions
metadata_entries = [
    {
        "Id": "void_singularity",
        "Name": "Sound Singularity",
        "Author": "mjurran-zaba",
        "Source": "void_singularity.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF080511"
    },
    {
        "Id": "void_monolith_v",
        "Name": "Neon Monolith V",
        "Author": "mjurran-zaba",
        "Source": "void_monolith_v.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF070A12"
    },
    {
        "Id": "void_sound_eclipse",
        "Name": "Sound Eclipse",
        "Author": "mjurran-zaba",
        "Source": "void_sound_eclipse.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF0C0D11"
    },
    {
        "Id": "void_equalizer_v",
        "Name": "Equalizer V-Pulse",
        "Author": "mjurran-zaba",
        "Source": "void_equalizer_v.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF08090E"
    },
    {
        "Id": "void_nordic_prism",
        "Name": "Nordic Hex Prism",
        "Author": "mjurran-zaba",
        "Source": "void_nordic_prism.svg",
        "Link": "https://github.com/mjurran-zaba/VoidTune",
        "GitHubAuthorUrl": "https://github.com/mjurran-zaba",
        "BackgroundMode": "integrated",
        "BackgroundColor": "#FF0A0E17"
    }
]

with open(METADATA_FILE, "w", encoding="utf-8") as f:
    json.dump(metadata_entries, f, indent=2)

print(f"Written metadata.json with {len(metadata_entries)} VoidTune icons!")
