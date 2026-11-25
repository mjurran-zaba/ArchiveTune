import math
import os
import json

SVG_DIR = "/home/user/VoidTune/IconPack/svg"
METADATA_FILE = "/home/user/VoidTune/IconPack/metadata.json"

# 1. void_singularity.svg (Zero Purple: Cyan -> Teal -> Amber Gold)
def gen_singularity():
    lines = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
             '  <!-- Integrated Background -->',
             '  <rect x="0" y="0" width="1024" height="1024" fill="#060C16" />',
             '  <circle cx="512" cy="512" r="480" fill="#0C1A2E" fill-opacity="0.4" />',
             '  <!-- 48 Radial Waveform Bars (Cyan -> Frost Teal -> Gold, ZERO PURPLE) -->',
             '  <g>']
    num_bars = 48
    inner_r, max_outer_r = 184, 420
    for i in range(num_bars):
        k = i / float(num_bars)
        angle_deg = i * (360.0 / num_bars)
        amp = 0.55 + 0.35 * math.sin(k * 4 * math.pi) + 0.15 * math.cos(k * 10 * math.pi)
        amp = max(0.25, min(1.0, amp))
        bar_h = amp * (max_outer_r - inner_r)
        
        t_col = (math.sin(k * 2 * math.pi) + 1.0) / 2.0
        if t_col < 0.5:
            p = t_col * 2
            r = int(56 * (1 - p) + 14 * p)
            g = int(189 * (1 - p) + 116 * p)
            b = int(248 * (1 - p) + 144 * p)
        else:
            p = (t_col - 0.5) * 2
            r = int(14 * (1 - p) + 245 * p)
            g = int(116 * (1 - p) + 158 * p)
            b = int(144 * (1 - p) + 11 * p)
        hex_col = f"#{r:02x}{g:02x}{b:02x}"
        lines.append(f'    <rect x="504" y="{512 - inner_r - bar_h:.1f}" width="16" height="{bar_h:.1f}" rx="6" ry="6" fill="{hex_col}" transform="rotate({angle_deg:.1f}, 512, 512)" />')

    lines.extend([
        '  </g>',
        '  <!-- Event Horizon -->',
        '  <circle cx="512" cy="512" r="190" fill="none" stroke="#81A1C1" stroke-width="4" stroke-opacity="0.4" />',
        '  <circle cx="512" cy="512" r="184" fill="#040810" stroke="#38BDF8" stroke-width="4" stroke-opacity="0.9" />',
        '  <!-- Center Diamond -->',
        '  <polygon points="512,460 522,502 564,512 522,522 512,564 502,522 460,512 502,502" fill="#E0F2FE" />',
        '  <circle cx="512" cy="512" r="8" fill="#FFFFFF" />',
        '</svg>'
    ])
    return "\n".join(lines)

# 2. void_monolith_v.svg (Zero Purple: Polar Cyan & Amber Gold)
def gen_monolith_v():
    lines = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
             '  <!-- Integrated Background -->',
             '  <rect x="0" y="0" width="1024" height="1024" fill="#070E1A" />']
    for y_pos in range(120, 900, 40):
        lines.append(f'  <line x1="100" y1="{y_pos}" x2="924" y2="{y_pos}" stroke="#1E293B" stroke-width="2" stroke-opacity="0.6" />')
    lines.extend([
        '  <!-- Facets: Cyan on left, Gold on right (Zero Purple) -->',
        '  <polygon points="220,240 370,240 340,520" fill="#38BDF8" />',
        '  <polygon points="220,240 340,520 512,810" fill="#0284C7" />',
        '  <polygon points="370,240 512,480 512,630 340,520" fill="#0E7490" />',
        '  <polygon points="340,520 512,630 512,810" fill="#155E75" />',
        '  <polygon points="654,240 512,480 512,630 684,520" fill="#F59E0B" />',
        '  <polygon points="684,520 512,630 512,810" fill="#D97706" />',
        '  <polygon points="804,240 654,240 684,520" fill="#FBBF24" />',
        '  <polygon points="804,240 684,520 512,810" fill="#B45309" />',
        '  <!-- Laser Highlights -->',
        '  <line x1="220" y1="240" x2="512" y2="810" stroke="#E0F2FE" stroke-width="3" stroke-opacity="0.8" />',
        '  <line x1="512" y1="630" x2="512" y2="810" stroke="#FFFFFF" stroke-width="4" stroke-opacity="0.95" />',
        '  <line x1="512" y1="480" x2="512" y2="630" stroke="#FEF3C7" stroke-width="2" stroke-opacity="0.75" />',
        '  <polygon points="512,362 525,380 512,398 499,380" fill="#38BDF8" />',
        '  <circle cx="512" cy="380" r="4" fill="#FFFFFF" />',
        '</svg>'
    ])
    return "\n".join(lines)

# 3. void_sound_eclipse.svg (Zero Purple: Solar Amber & Polar Cyan)
def gen_sound_eclipse():
    lines = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
             '  <!-- Integrated Background -->',
             '  <rect x="0" y="0" width="1024" height="1024" fill="#080C14" />']
    for r in range(120, 450, 8):
        lines.append(f'  <circle cx="471" cy="491" r="{r}" fill="none" stroke="#1E293B" stroke-width="1.2" stroke-opacity="0.35" />')
    lines.extend([
        '  <!-- Solar Corona (Amber Gold & Arctic Cyan) -->',
        '  <path d="M 772,271 A 340,340 0 0,1 291,752" fill="none" stroke="#F59E0B" stroke-width="36" stroke-opacity="0.5" stroke-linecap="round" />',
        '  <path d="M 772,271 A 340,340 0 0,1 291,752" fill="none" stroke="#FBBF24" stroke-width="18" stroke-opacity="0.8" stroke-linecap="round" />',
        '  <path d="M 772,271 A 340,340 0 0,1 291,752" fill="none" stroke="#FEF3C7" stroke-width="6" stroke-opacity="0.95" stroke-linecap="round" />',
        '  <circle cx="471" cy="491" r="327" fill="#0A0E17" />',
        '  <circle cx="471" cy="491" r="41" fill="#131B2E" stroke="#38BDF8" stroke-width="2" stroke-opacity="0.6" />',
        '  <line x1="328" y1="164" x2="594" y2="860" stroke="#38BDF8" stroke-width="8" stroke-linecap="round" />',
        '  <line x1="594" y1="860" x2="860" y2="286" stroke="#F59E0B" stroke-width="8" stroke-linecap="round" />',
        '  <circle cx="594" cy="860" r="28" fill="#38BDF8" fill-opacity="0.25" />',
        '  <circle cx="594" cy="860" r="14" fill="#FFFFFF" />',
        '</svg>'
    ])
    return "\n".join(lines)

# 4. void_equalizer_v.svg (Zero Purple: Cyan -> Teal -> Amber Gold)
def gen_equalizer_v():
    lines = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
             '  <!-- Integrated Background -->',
             '  <rect x="0" y="0" width="1024" height="1024" fill="#070D18" />',
             '  <line x1="120" y1="512" x2="904" y2="512" stroke="#FFFFFF" stroke-width="2" stroke-opacity="0.2" />']
    num_bars, spacing, bar_w = 21, 35.0, 24.0
    start_x = 512.0 - (num_bars * spacing) / 2.0 + spacing / 2.0
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
            r = int(56 * (1 - k) + 14 * k)
            g = int(189 * (1 - k) + 116 * k)
            b = int(248 * (1 - k) + 144 * k)
        else:
            k = (norm_i - 0.5) * 2
            r = int(14 * (1 - k) + 245 * k)
            g = int(116 * (1 - k) + 158 * k)
            b = int(144 * (1 - k) + 11 * k)
        hex_col = f"#{r:02x}{g:02x}{b:02x}"
        lines.append(f'  <rect x="{bx - bar_w/2:.1f}" y="{top_y:.1f}" width="{bar_w:.1f}" height="{h:.1f}" rx="12" ry="12" fill="{hex_col}" />')
    lines.append('</svg>')
    return "\n".join(lines)

# 5. void_nordic_prism.svg (Already Zero Purple: Cyan & Indigo/Slate)
def gen_nordic_prism():
    lines = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
             '  <!-- Integrated Background -->',
             '  <rect x="0" y="0" width="1024" height="1024" fill="#0A0E17" />']
    cx, cy, scale = 512.0, 512.0, 389.0
    angles = [math.radians(a) for a in range(30, 390, 60)]
    pts = [(cx + scale * math.cos(a), cy + scale * math.sin(a)) for a in angles]
    lines.extend([
        f'  <polygon points="{pts[5][0]:.1f},{pts[5][1]:.1f} {pts[0][0]:.1f},{pts[0][1]:.1f} {cx},{cy}" fill="#1E293B" />',
        f'  <polygon points="{pts[0][0]:.1f},{pts[0][1]:.1f} {pts[1][0]:.1f},{pts[1][1]:.1f} {cx},{cy}" fill="#38BDF8" />',
        f'  <polygon points="{pts[1][0]:.1f},{pts[1][1]:.1f} {pts[2][0]:.1f},{pts[2][1]:.1f} {cx},{cy}" fill="#0E7490" />',
        f'  <polygon points="{pts[2][0]:.1f},{pts[2][1]:.1f} {pts[3][0]:.1f},{pts[3][1]:.1f} {cx},{cy}" fill="#0369A1" />',
        f'  <polygon points="{pts[3][0]:.1f},{pts[3][1]:.1f} {pts[4][0]:.1f},{pts[4][1]:.1f} {cx},{cy}" fill="#0284C7" />',
        f'  <polygon points="{pts[4][0]:.1f},{pts[4][1]:.1f} {pts[5][0]:.1f},{pts[5][1]:.1f} {cx},{cy}" fill="#475569" />',
    ])
    v_p0, v_p1, v_p2 = (cx - scale*0.5, cy - scale*0.45), (cx - scale*0.2, cy - scale*0.45), (cx, cy + scale*0.35)
    v_p3, v_p4, v_p5 = (cx + scale*0.2, cy - scale*0.45), (cx + scale*0.5, cy - scale*0.45), (cx, cy + scale*0.75)
    lines.extend([
        f'  <polygon points="{v_p0[0]:.1f},{v_p0[1]:.1f} {v_p1[0]:.1f},{v_p1[1]:.1f} {v_p2[0]:.1f},{v_p2[1]:.1f} {v_p3[0]:.1f},{v_p3[1]:.1f} {v_p4[0]:.1f},{v_p4[1]:.1f} {v_p5[0]:.1f},{v_p5[1]:.1f}" fill="#0A0E17" />',
        f'  <line x1="{v_p0[0]:.1f}" y1="{v_p0[1]:.1f}" x2="{v_p5[0]:.1f}" y2="{v_p5[1]:.1f}" stroke="#E0F2FE" stroke-width="8" stroke-linecap="round" />',
        f'  <line x1="{v_p5[0]:.1f}" y1="{v_p5[1]:.1f}" x2="{v_p4[0]:.1f}" y2="{v_p4[1]:.1f}" stroke="#E0F2FE" stroke-width="8" stroke-linecap="round" />',
        f'  <line x1="{v_p1[0]:.1f}" y1="{v_p1[1]:.1f}" x2="{v_p2[0]:.1f}" y2="{v_p2[1]:.1f}" stroke="#38BDF8" stroke-width="8" stroke-linecap="round" />',
        f'  <line x1="{v_p2[0]:.1f}" y1="{v_p2[1]:.1f}" x2="{v_p3[0]:.1f}" y2="{v_p3[1]:.1f}" stroke="#38BDF8" stroke-width="8" stroke-linecap="round" />',
        f'  <circle cx="{cx}" cy="{cy - scale * 0.1:.1f}" r="8" fill="#FFFFFF" />',
        '</svg>'
    ])
    return "\n".join(lines)

# 6. void_neko_beat.svg (Zero Purple: Polar Cyan & Amber Gold)
def gen_neko_beat():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Integrated Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#080F1D" />',
        '  <circle cx="512" cy="512" r="380" fill="none" stroke="#F59E0B" stroke-width="2" stroke-opacity="0.25" />',
        '  <circle cx="512" cy="512" r="320" fill="none" stroke="#38BDF8" stroke-width="2" stroke-opacity="0.3" />',
        '  <!-- Ears (Cyan & Gold) -->',
        '  <polygon points="320,440 240,240 440,340" fill="#0F172A" stroke="#38BDF8" stroke-width="8" stroke-linejoin="round" />',
        '  <polygon points="320,410 265,275 405,345" fill="#38BDF8" fill-opacity="0.4" />',
        '  <polygon points="704,440 784,240 584,340" fill="#0F172A" stroke="#38BDF8" stroke-width="8" stroke-linejoin="round" />',
        '  <polygon points="704,410 759,275 619,345" fill="#38BDF8" fill-opacity="0.4" />',
        '  <circle cx="512" cy="540" r="210" fill="#131F37" stroke="#38BDF8" stroke-width="6" />',
        '  <!-- Headphones -->',
        '  <path d="M 280,510 A 240,240 0 0,1 744,510" fill="none" stroke="#F59E0B" stroke-width="20" stroke-linecap="round" />',
        '  <rect x="230" y="440" width="70" height="140" rx="35" ry="35" fill="#0E7490" stroke="#38BDF8" stroke-width="8" />',
        '  <circle cx="265" cy="510" r="20" fill="#F59E0B" />',
        '  <rect x="724" y="440" width="70" height="140" rx="35" ry="35" fill="#0E7490" stroke="#38BDF8" stroke-width="8" />',
        '  <circle cx="759" cy="510" r="20" fill="#F59E0B" />',
        '  <!-- Eyes -->',
        '  <path d="M 400,530 Q 435,490 470,530" fill="none" stroke="#38BDF8" stroke-width="10" stroke-linecap="round" />',
        '  <polygon points="590,490 598,515 625,520 600,532 602,558 582,540 558,550 570,528 555,508 580,512" fill="#F59E0B" />',
        '  <circle cx="590" cy="525" r="5" fill="#FFFFFF" />',
        '  <polygon points="506,562 518,562 512,572" fill="#F59E0B" />',
        '  <path d="M 482,580 Q 497,600 512,582 Q 527,600 542,580" fill="none" stroke="#FFFFFF" stroke-width="6" stroke-linecap="round" />',
        '  <ellipse cx="380" cy="570" rx="28" ry="14" fill="#38BDF8" fill-opacity="0.4" />',
        '  <ellipse cx="644" cy="570" rx="28" ry="14" fill="#38BDF8" fill-opacity="0.4" />',
    ]
    for idx, h in enumerate([24, 45, 65, 45, 24]):
        bx = 472 + idx * 20
        lines.append(f'  <rect x="{bx}" y="{430 - h/2}" width="8" height="{h}" rx="4" fill="#38BDF8" />')
    lines.append('</svg>')
    return "\n".join(lines)

# 7. void_sakura_melody.svg (Zero Purple: Frost White, Emerald Mint & Polar Cyan)
def gen_sakura_melody():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Integrated Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#0A1424" />',
        '  <circle cx="512" cy="512" r="360" fill="#122542" fill-opacity="0.6" />',
        '  <g id="frost_flower">'
    ]
    cx, cy, petal_r = 512, 490, 180
    for i in range(5):
        deg = i * 72
        lines.extend([
            f'    <g transform="rotate({deg}, {cx}, {cy})">',
            f'      <path d="M {cx},{cy} C {cx-75},{cy-110} {cx-60},{cy-240} {cx},{cy-260} C {cx+60},{cy-240} {cx+75},{cy-110} {cx},{cy} Z" fill="#38BDF8" fill-opacity="0.85" stroke="#E0F2FE" stroke-width="4" />',
            f'      <path d="M {cx},{cy} C {cx-35},{cy-90} {cx-30},{cy-180} {cx},{cy-200} C {cx+30},{cy-180} {cx+35},{cy-90} {cx},{cy} Z" fill="#0284C7" fill-opacity="0.5" />',
            '    </g>'
        ])
    lines.extend([
        '  </g>',
        '  <circle cx="512" cy="490" r="38" fill="#FDE047" />',
        '  <circle cx="512" cy="490" r="18" fill="#F59E0B" />',
        '  <path d="M 525,480 L 525,320 C 560,320 620,335 640,380 L 640,345 C 600,310 550,295 505,295 L 505,480 Z" fill="#FDF2F8" />',
        '  <circle cx="480" cy="495" r="45" fill="#38BDF8" stroke="#FDF2F8" stroke-width="6" />',
        '  <ellipse cx="320" cy="740" rx="32" ry="18" fill="#38BDF8" transform="rotate(-30, 320, 740)" />',
        '  <ellipse cx="720" cy="710" rx="26" ry="15" fill="#38BDF8" transform="rotate(25, 720, 710)" />',
        '  <ellipse cx="620" cy="810" rx="22" ry="12" fill="#38BDF8" transform="rotate(45, 620, 810)" />',
        '</svg>'
    ])
    return "\n".join(lines)

# 8. void_crescent_vinyl.svg (Already Zero Purple: Gold & Dark Navy)
def gen_crescent_vinyl():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Integrated Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#080B18" />'
    ]
    cx, cy = 480, 512
    lines.append(f'  <circle cx="{cx}" cy="{cy}" r="320" fill="#0F1424" stroke="#1E293B" stroke-width="4" />')
    for r in range(120, 310, 22):
        lines.append(f'  <circle cx="{cx}" cy="{cy}" r="{r}" fill="none" stroke="#25324D" stroke-width="1.5" stroke-opacity="0.45" />')
    lines.extend([
        f'  <circle cx="{cx}" cy="{cy}" r="90" fill="#1E293B" />',
        f'  <circle cx="{cx}" cy="{cy}" r="32" fill="#080B18" stroke="#38BDF8" stroke-width="3" />',
        '  <path d="M 520,180 A 340,340 0 0,1 780,740 A 310,310 0 1,0 520,180 Z" fill="#F59E0B" stroke="#FDE68A" stroke-width="6" />',
        '  <path d="M 560,220 A 300,300 0 0,1 740,700 A 280,280 0 1,0 560,220 Z" fill="#FBBF24" fill-opacity="0.5" />'
    ])
    stars = [(280, 220), (320, 280), (220, 480), (260, 720), (380, 810), (740, 240), (810, 420), (720, 800)]
    for sx, sy in stars:
        lines.append(f'  <circle cx="{sx}" cy="{sy}" r="5" fill="#E0F2FE" />')
        lines.append(f'  <circle cx="{sx}" cy="{sy}" r="12" fill="#38BDF8" fill-opacity="0.25" />')
    lines.extend([
        '  <polygon points="760,320 766,338 784,344 766,350 760,368 754,350 736,344 754,338" fill="#FFFFFF" />',
        '</svg>'
    ])
    return "\n".join(lines)

# 9. void_retro_synthwave.svg (Zero Purple: Cyan Grid & Amber/Orange Sun)
def gen_retro_synthwave():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Integrated Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#070E1A" />',
        '  <rect x="0" y="580" width="1024" height="444" fill="#0C172B" />'
    ]
    for y_pos in [620, 670, 730, 800, 880, 970]:
        lines.append(f'  <line x1="0" y1="{y_pos}" x2="1024" y2="{y_pos}" stroke="#38BDF8" stroke-width="2" stroke-opacity="0.6" />')
    for x_p in range(120, 920, 100):
        lines.append(f'  <line x1="{x_p}" y1="580" x2="{512 + (x_p - 512)*1.8}" y2="1024" stroke="#0E7490" stroke-width="2" stroke-opacity="0.5" />')
    lines.extend([
        '  <defs>',
        '    <linearGradient id="sunGrad" x1="0%" y1="0%" x2="0%" y2="100%">',
        '      <stop offset="0%" stop-color="#FDE047" />',
        '      <stop offset="50%" stop-color="#F59E0B" />',
        '      <stop offset="100%" stop-color="#D97706" />',
        '    </linearGradient>',
        '  </defs>',
        '  <circle cx="512" cy="520" r="230" fill="url(#sunGrad)" />'
    ])
    for idx, sy in enumerate([480, 520, 565, 615, 670, 725]):
        sh = 8 + idx * 4
        lines.append(f'  <rect x="250" y="{sy}" width="524" height="{sh}" fill="#070E1A" />')
    lines.append('  <line x1="80" y1="580" x2="944" y2="580" stroke="#38BDF8" stroke-width="6" />')
    lines.append('</svg>')
    return "\n".join(lines)

# 10. void_sonic_shuriken.svg (Already Zero Purple: Cyan & Blue)
def gen_sonic_shuriken():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Integrated Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#070E1A" />',
        '  <circle cx="512" cy="512" r="360" fill="none" stroke="#1E293B" stroke-width="2" />',
        '  <circle cx="512" cy="512" r="280" fill="none" stroke="#0E7490" stroke-width="2" stroke-opacity="0.4" />'
    ]
    cx, cy = 512, 512
    for rot in [0, 90, 180, 270]:
        lines.extend([
            f'  <g transform="rotate({rot}, {cx}, {cy})">',
            f'    <polygon points="{cx},{cy} {cx},{cy-340} {cx-60},{cy-140}" fill="#38BDF8" />',
            f'    <polygon points="{cx},{cy} {cx},{cy-340} {cx+60},{cy-140}" fill="#0284C7" />',
            f'    <line x1="{cx}" y1="{cy}" x2="{cx}" y2="{cy-340}" stroke="#FFFFFF" stroke-width="3" />',
            '  </g>'
        ])
    lines.extend([
        '  <circle cx="512" cy="512" r="70" fill="#0C1527" stroke="#38BDF8" stroke-width="8" />',
        '  <circle cx="512" cy="512" r="40" fill="#0284C7" />',
        '  <circle cx="512" cy="512" r="18" fill="#FFFFFF" />'
    ])
    for rot in [45, 135, 225, 315]:
        rad = math.radians(rot)
        dx = cx + 220 * math.cos(rad)
        dy = cy + 220 * math.sin(rad)
        lines.append(f'  <circle cx="{dx}" cy="{dy}" r="8" fill="#38BDF8" />')
    lines.append('</svg>')
    return "\n".join(lines)

# 11. void_sound_butterfly.svg (Zero Purple: Cyan, Arctic Teal & Amber Gold)
def gen_sound_butterfly():
    lines = [
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">',
        '  <!-- Integrated Background -->',
        '  <rect x="0" y="0" width="1024" height="1024" fill="#080E1B" />',
        '  <path d="M 505,420 Q 480,340 440,310" fill="none" stroke="#38BDF8" stroke-width="5" stroke-linecap="round" />',
        '  <circle cx="440" cy="310" r="10" fill="#F59E0B" />',
        '  <path d="M 519,420 Q 544,340 584,310" fill="none" stroke="#38BDF8" stroke-width="5" stroke-linecap="round" />',
        '  <circle cx="584" cy="310" r="10" fill="#F59E0B" />',
        '  <ellipse cx="512" cy="530" rx="16" ry="110" fill="#E2E8F0" stroke="#38BDF8" stroke-width="4" />',
        '  <!-- Upper Wings (Cyan & Frost Teal) -->',
        '  <path d="M 495,470 C 360,280 180,320 180,480 C 180,610 380,620 495,540 Z" fill="#0284C7" fill-opacity="0.45" stroke="#38BDF8" stroke-width="6" />',
        '  <path d="M 495,475 C 380,330 240,360 240,480 C 240,570 380,580 495,535 Z" fill="#0E7490" fill-opacity="0.35" stroke="#88C0D0" stroke-width="4" />',
        '  <path d="M 529,470 C 664,280 844,320 844,480 C 844,610 644,620 529,540 Z" fill="#0284C7" fill-opacity="0.45" stroke="#38BDF8" stroke-width="6" />',
        '  <path d="M 529,475 C 644,330 784,360 784,480 C 784,570 644,580 529,535 Z" fill="#0E7490" fill-opacity="0.35" stroke="#88C0D0" stroke-width="4" />',
        '  <!-- Lower Wings (Amber Gold) -->',
        '  <path d="M 495,540 C 380,620 250,670 290,780 C 330,860 460,740 498,600 Z" fill="#F59E0B" fill-opacity="0.45" stroke="#F59E0B" stroke-width="6" />',
        '  <path d="M 529,540 C 644,620 774,670 734,780 C 694,860 564,740 526,600 Z" fill="#F59E0B" fill-opacity="0.45" stroke="#F59E0B" stroke-width="6" />',
        '</svg>'
    ]
    return "\n".join(lines)

all_svgs = [
    ("void_singularity.svg", gen_singularity()),
    ("void_monolith_v.svg", gen_monolith_v()),
    ("void_sound_eclipse.svg", gen_sound_eclipse()),
    ("void_equalizer_v.svg", gen_equalizer_v()),
    ("void_nordic_prism.svg", gen_nordic_prism()),
    ("void_neko_beat.svg", gen_neko_beat()),
    ("void_sakura_melody.svg", gen_sakura_melody()),
    ("void_crescent_vinyl.svg", gen_crescent_vinyl()),
    ("void_retro_synthwave.svg", gen_retro_synthwave()),
    ("void_sonic_shuriken.svg", gen_sonic_shuriken()),
    ("void_sound_butterfly.svg", gen_sound_butterfly()),
]

for filename, content in all_svgs:
    path = os.path.join(SVG_DIR, filename)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generated ZERO-PURPLE SVG: {filename}")

print("All 11 SVGs regenerated with ZERO PURPLE!")
