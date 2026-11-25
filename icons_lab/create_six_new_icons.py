import math
import os
import json

OUTPUT_SVG_DIR = "/home/user/VoidTune/IconPack/svg"
METADATA_FILE = "/home/user/VoidTune/IconPack/metadata.json"

# ==============================================================================
# 6. void_neko_beat.svg (Kawaii Neko Beat)
# Cute anime cat face with glowing cyan/pink headphones & audio wave ears
# ==============================================================================
def generate_neko_beat_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#0D0A1A" />')
    
    # Ambient audio pulse circles
    lines.append('  <circle cx="512" cy="512" r="380" fill="none" stroke="#EC4899" stroke-width="2" stroke-opacity="0.25" />')
    lines.append('  <circle cx="512" cy="512" r="320" fill="none" stroke="#38BDF8" stroke-width="2" stroke-opacity="0.3" />')

    # Cat Head base
    lines.append('  <!-- Cat Head Outline & Ears -->')
    # Left Ear
    lines.append('  <polygon points="320,440 240,240 440,340" fill="#1E1638" stroke="#EC4899" stroke-width="8" stroke-linejoin="round" />')
    lines.append('  <polygon points="320,410 265,275 405,345" fill="#EC4899" fill-opacity="0.4" />')
    # Right Ear
    lines.append('  <polygon points="704,440 784,240 584,340" fill="#1E1638" stroke="#EC4899" stroke-width="8" stroke-linejoin="round" />')
    lines.append('  <polygon points="704,410 759,275 619,345" fill="#EC4899" fill-opacity="0.4" />')

    # Head Circle
    lines.append('  <circle cx="512" cy="540" r="210" fill="#1A1333" stroke="#A855F7" stroke-width="6" />')

    # Cyber Headphones Band
    lines.append('  <path d="M 280,510 A 240,240 0 0,1 744,510" fill="none" stroke="#38BDF8" stroke-width="20" stroke-linecap="round" />')
    lines.append('  <!-- Headphone Earcups -->')
    lines.append('  <rect x="230" y="440" width="70" height="140" rx="35" ry="35" fill="#0E7490" stroke="#38BDF8" stroke-width="8" />')
    lines.append('  <circle cx="265" cy="510" r="20" fill="#EC4899" />')
    lines.append('  <rect x="724" y="440" width="70" height="140" rx="35" ry="35" fill="#0E7490" stroke="#38BDF8" stroke-width="8" />')
    lines.append('  <circle cx="759" cy="510" r="20" fill="#EC4899" />')

    # Kawaii Eyes (Winking / Joy anime curve)
    lines.append('  <!-- Left Eye: Joy arc -->')
    lines.append('  <path d="M 400,530 Q 435,490 470,530" fill="none" stroke="#38BDF8" stroke-width="10" stroke-linecap="round" />')
    # Right Eye: Sparkle Star eye
    lines.append('  <polygon points="590,490 598,515 625,520 600,532 602,558 582,540 558,550 570,528 555,508 580,512" fill="#EC4899" />')
    lines.append('  <circle cx="590" cy="525" r="5" fill="#FFFFFF" />')

    # Kawaii Nose & Mouth (:3)
    lines.append('  <polygon points="506,562 518,562 512,572" fill="#EC4899" />')
    lines.append('  <path d="M 482,580 Q 497,600 512,582 Q 527,600 542,580" fill="none" stroke="#FFFFFF" stroke-width="6" stroke-linecap="round" />')

    # Pink Cheeks
    lines.append('  <ellipse cx="380" cy="570" rx="28" ry="14" fill="#EC4899" fill-opacity="0.6" />')
    lines.append('  <ellipse cx="644" cy="570" rx="28" ry="14" fill="#EC4899" fill-opacity="0.6" />')

    # Sound equalizer bars on forehead
    for idx, h in enumerate([24, 45, 65, 45, 24]):
        bx = 472 + idx * 20
        lines.append(f'  <rect x="{bx}" y="{430 - h/2}" width="8" height="{h}" rx="4" fill="#38BDF8" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ==============================================================================
# 7. void_sakura_melody.svg (Sakura Melody)
# Anime cherry blossom with musical note petals on soft twilight
# ==============================================================================
def generate_sakura_melody_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#140A1E" />')

    # Soft ambient glow circle
    lines.append('  <circle cx="512" cy="512" r="360" fill="#2E123D" fill-opacity="0.6" />')

    # 5 Sakura Petals rotated around center (512, 490)
    cx, cy = 512, 490
    petal_r = 180
    lines.append('  <g id="sakura_flower">')
    for i in range(5):
        deg = i * 72
        rad = math.radians(deg)
        px = cx + petal_r * math.cos(rad)
        py = cy + petal_r * math.sin(rad)
        # Petal path
        lines.append(f'    <g transform="rotate({deg}, {cx}, {cy})">')
        lines.append(f'      <path d="M {cx},{cy} C {cx-75},{cy-110} {cx-60},{cy-240} {cx},{cy-260} C {cx+60},{cy-240} {cx+75},{cy-110} {cx},{cy} Z" fill="#F472B6" fill-opacity="0.85" stroke="#FDF2F8" stroke-width="4" />')
        lines.append(f'      <path d="M {cx},{cy} C {cx-35},{cy-90} {cx-30},{cy-180} {cx},{cy-200} C {cx+30},{cy-180} {cx+35},{cy-90} {cx},{cy} Z" fill="#EC4899" fill-opacity="0.5" />')
        lines.append('    </g>')
    lines.append('  </g>')

    # Stamen / Flower Center
    lines.append('  <circle cx="512" cy="490" r="38" fill="#FDE047" />')
    lines.append('  <circle cx="512" cy="490" r="18" fill="#F59E0B" />')

    # Modern Floating Musical Note (Eighth note in gold & white)
    lines.append('  <!-- Eighth Musical Note -->')
    lines.append('  <path d="M 525,480 L 525,320 C 560,320 620,335 640,380 L 640,345 C 600,310 550,295 505,295 L 505,480 Z" fill="#FDF2F8" />')
    lines.append('  <circle cx="480" cy="495" r="45" fill="#38BDF8" stroke="#FDF2F8" stroke-width="6" />')

    # Drifting mini petals
    lines.append('  <ellipse cx="320" cy="740" rx="32" ry="18" fill="#F472B6" transform="rotate(-30, 320, 740)" />')
    lines.append('  <ellipse cx="720" cy="710" rx="26" ry="15" fill="#F472B6" transform="rotate(25, 720, 710)" />')
    lines.append('  <ellipse cx="620" cy="810" rx="22" ry="12" fill="#F472B6" transform="rotate(45, 620, 810)" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ==============================================================================
# 8. void_crescent_vinyl.svg (Anime Crescent Moon Vinyl)
# Luminous crescent moon hugging a dark vinyl disc with stardust
# ==============================================================================
def generate_crescent_vinyl_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#080B18" />')

    # Vinyl Record base
    cx, cy = 480, 512
    lines.append('  <!-- Vinyl Disc -->')
    lines.append(f'  <circle cx="{cx}" cy="{cy}" r="320" fill="#0F1424" stroke="#1E293B" stroke-width="4" />')
    for r in range(120, 310, 22):
        lines.append(f'  <circle cx="{cx}" cy="{cy}" r="{r}" fill="none" stroke="#25324D" stroke-width="1.5" stroke-opacity="0.45" />')
    lines.append(f'  <circle cx="{cx}" cy="{cy}" r="90" fill="#1E293B" />')
    lines.append(f'  <circle cx="{cx}" cy="{cy}" r="32" fill="#080B18" stroke="#38BDF8" stroke-width="3" />')

    # Golden Anime Crescent Moon embracing the vinyl from right
    lines.append('  <!-- Crescent Moon -->')
    lines.append('  <path d="M 520,180 A 340,340 0 0,1 780,740 A 310,310 0 1,0 520,180 Z" fill="#F59E0B" stroke="#FDE68A" stroke-width="6" />')
    lines.append('  <path d="M 560,220 A 300,300 0 0,1 740,700 A 280,280 0 1,0 560,220 Z" fill="#FBBF24" fill-opacity="0.5" />')

    # Stardust particles
    stars = [(280, 220), (320, 280), (220, 480), (260, 720), (380, 810), (740, 240), (810, 420), (720, 800)]
    for sx, sy in stars:
        lines.append(f'  <circle cx="{sx}" cy="{sy}" r="5" fill="#E0F2FE" />')
        lines.append(f'  <circle cx="{sx}" cy="{sy}" r="12" fill="#38BDF8" fill-opacity="0.25" />')

    # 4-point sparkle star
    lines.append('  <polygon points="760,320 766,338 784,344 766,350 760,368 754,350 736,344 754,338" fill="#FFFFFF" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ==============================================================================
# 9. void_retro_synthwave.svg (Retro Synthwave Sunset)
# 80s neon slotted sunset with magenta audio grid & cyber horizon
# ==============================================================================
def generate_retro_synthwave_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#0C071C" />')

    # Perspective Audio Cyber Grid
    lines.append('  <!-- Cyber Horizon Grid -->')
    lines.append('  <rect x="0" y="580" width="1024" height="444" fill="#120626" />')
    for y_pos in [620, 670, 730, 800, 880, 970]:
        lines.append(f'  <line x1="0" y1="{y_pos}" x2="1024" y2="{y_pos}" stroke="#EC4899" stroke-width="2" stroke-opacity="0.6" />')
    for x_p in range(120, 920, 100):
        lines.append(f'  <line x1="{x_p}" y1="580" x2="{512 + (x_p - 512)*1.8}" y2="1024" stroke="#8B5CF6" stroke-width="2" stroke-opacity="0.5" />')

    # Glowing 80s Slotted Sun (radius 240, centered at 512, 540)
    lines.append('  <!-- Slotted Sunset Sun -->')
    lines.append('  <defs>')
    lines.append('    <linearGradient id="sunGrad" x1="0%" y1="0%" x2="0%" y2="100%">')
    lines.append('      <stop offset="0%" stop-color="#FDE047" />')
    lines.append('      <stop offset="50%" stop-color="#F43F5E" />')
    lines.append('      <stop offset="100%" stop-color="#A855F7" />')
    lines.append('    </linearGradient>')
    lines.append('  </defs>')
    lines.append('  <circle cx="512" cy="520" r="230" fill="url(#sunGrad)" />')

    # Horizontal Slots cutting through bottom half of sun
    slot_y_list = [480, 520, 565, 615, 670, 725]
    for idx, sy in enumerate(slot_y_list):
        sh = 8 + idx * 4
        lines.append(f'  <rect x="250" y="{sy}" width="524" height="{sh}" fill="#0C071C" />')

    # Neon Horizon Laser Line
    lines.append('  <line x1="80" y1="580" x2="944" y2="580" stroke="#38BDF8" stroke-width="6" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ==============================================================================
# 10. void_sonic_shuriken.svg (Cyber Sonic Shuriken)
# 4-blade crystal ninja sound shuriken with Polar Cyan & Arctic glow
# ==============================================================================
def generate_sonic_shuriken_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#070E1A" />')

    # Concentric target sonar rings
    lines.append('  <circle cx="512" cy="512" r="360" fill="none" stroke="#1E293B" stroke-width="2" />')
    lines.append('  <circle cx="512" cy="512" r="280" fill="none" stroke="#0E7490" stroke-width="2" stroke-opacity="0.4" />')

    # 4 Crystal Blades (North, East, South, West)
    cx, cy = 512, 512
    lines.append('  <!-- 4 Shuriken Sonic Blades -->')
    for rot in [0, 90, 180, 270]:
        lines.append(f'  <g transform="rotate({rot}, {cx}, {cy})">')
        # Left facet of blade (Cyan)
        lines.append(f'    <polygon points="{cx},{cy} {cx},{cy-340} {cx-60},{cy-140}" fill="#38BDF8" />')
        # Right facet of blade (Deep Arctic Blue)
        lines.append(f'    <polygon points="{cx},{cy} {cx},{cy-340} {cx+60},{cy-140}" fill="#0284C7" />')
        # Inner beveled edge
        lines.append(f'    <line x1="{cx}" y1="{cy}" x2="{cx}" y2="{cy-340}" stroke="#FFFFFF" stroke-width="3" />')
        lines.append('  </g>')

    # Center Sonic Core
    lines.append('  <circle cx="512" cy="512" r="70" fill="#0C1527" stroke="#38BDF8" stroke-width="8" />')
    lines.append('  <circle cx="512" cy="512" r="40" fill="#0284C7" />')
    lines.append('  <circle cx="512" cy="512" r="18" fill="#FFFFFF" />')

    # Quantum Energy Dots
    for rot in [45, 135, 225, 315]:
        rad = math.radians(rot)
        dx = cx + 220 * math.cos(rad)
        dy = cy + 220 * math.sin(rad)
        lines.append(f'  <circle cx="{dx}" cy="{dy}" r="8" fill="#38BDF8" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ==============================================================================
# 11. void_sound_butterfly.svg (Sound Butterfly)
# Ethereal anime butterfly with wings formed from equalizer frequency curves
# ==============================================================================
def generate_sound_butterfly_svg():
    lines = []
    lines.append('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">')
    lines.append('  <!-- Integrated Background -->')
    lines.append('  <rect x="0" y="0" width="1024" height="1024" fill="#0B0918" />')

    # Center Butterfly Body
    cx, cy = 512, 530
    lines.append('  <!-- Butterfly Antennae & Body -->')
    lines.append('  <path d="M 505,420 Q 480,340 440,310" fill="none" stroke="#38BDF8" stroke-width="5" stroke-linecap="round" />')
    lines.append('  <circle cx="440" cy="310" r="10" fill="#EC4899" />')
    lines.append('  <path d="M 519,420 Q 544,340 584,310" fill="none" stroke="#38BDF8" stroke-width="5" stroke-linecap="round" />')
    lines.append('  <circle cx="584" cy="310" r="10" fill="#EC4899" />')

    lines.append('  <ellipse cx="512" cy="530" rx="16" ry="110" fill="#E2E8F0" stroke="#A855F7" stroke-width="4" />')

    # Wings: Left & Right Upper Wings formed of equalizing harmonic arches
    lines.append('  <!-- Upper Wings (Left & Right) -->')
    # Left Upper Wing
    lines.append('  <path d="M 495,470 C 360,280 180,320 180,480 C 180,610 380,620 495,540 Z" fill="#8B5CF6" fill-opacity="0.45" stroke="#A855F7" stroke-width="6" />')
    lines.append('  <path d="M 495,475 C 380,330 240,360 240,480 C 240,570 380,580 495,535 Z" fill="#EC4899" fill-opacity="0.35" stroke="#F472B6" stroke-width="4" />')
    # Right Upper Wing
    lines.append('  <path d="M 529,470 C 664,280 844,320 844,480 C 844,610 644,620 529,540 Z" fill="#8B5CF6" fill-opacity="0.45" stroke="#A855F7" stroke-width="6" />')
    lines.append('  <path d="M 529,475 C 644,330 784,360 784,480 C 784,570 644,580 529,535 Z" fill="#EC4899" fill-opacity="0.35" stroke="#F472B6" stroke-width="4" />')

    # Lower Wings (Left & Right)
    lines.append('  <!-- Lower Wings (Left & Right) -->')
    lines.append('  <path d="M 495,540 C 380,620 250,670 290,780 C 330,860 460,740 498,600 Z" fill="#38BDF8" fill-opacity="0.45" stroke="#38BDF8" stroke-width="6" />')
    lines.append('  <path d="M 529,540 C 644,620 774,670 734,780 C 694,860 564,740 526,600 Z" fill="#38BDF8" fill-opacity="0.45" stroke="#38BDF8" stroke-width="6" />')

    # Wing Equalizer Veins
    for r_bar in [320, 380, 440]:
        lines.append(f'  <circle cx="{r_bar}" cy="480" r="14" fill="#38BDF8" fill-opacity="0.7" />')
        lines.append(f'  <circle cx="{1024 - r_bar}" cy="480" r="14" fill="#38BDF8" fill-opacity="0.7" />')

    lines.append('</svg>')
    return "\n".join(lines)

# ==============================================================================
# MAIN
# ==============================================================================
new_svgs = [
    ("void_neko_beat.svg", generate_neko_beat_svg()),
    ("void_sakura_melody.svg", generate_sakura_melody_svg()),
    ("void_crescent_vinyl.svg", generate_crescent_vinyl_svg()),
    ("void_retro_synthwave.svg", generate_retro_synthwave_svg()),
    ("void_sonic_shuriken.svg", generate_sonic_shuriken_svg()),
    ("void_sound_butterfly.svg", generate_sound_butterfly_svg()),
]

for filename, content in new_svgs:
    path = os.path.join(OUTPUT_SVG_DIR, filename)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generated new SVG: {path} ({len(content)} chars)")

print("All 6 new SVGs written successfully!")
