import os
import math
import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageFont

OUTPUT_DIR = "/home/user/VoidTune/icons_lab"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def create_squircle_mask(size, radius_ratio=0.44, power=4.5):
    """Generates an ultra-smooth anti-aliased squircle mask using superellipse formula."""
    y, x = np.ogrid[:size, :size]
    cx, cy = size / 2.0, size / 2.0
    r = size * radius_ratio
    dist = ((np.abs(x - cx) / r) ** power + (np.abs(y - cy) / r) ** power) ** (1.0 / power)
    # Anti-aliased edge over ~3 pixels
    edge_width = 3.0 / size
    mask = np.clip((1.0 - (dist - 1.0 + edge_width) / edge_width), 0.0, 1.0)
    return mask

def blend_additive(base_np, glow_np):
    """Additive blend of two float32 [0..1] RGB images."""
    return np.clip(base_np + glow_np, 0.0, 1.0)

# ==============================================================================
# ICON 1: THE SOUND SINGULARITY (Accretion Waveform & Gravitational Void)
# ==============================================================================
def render_icon_singularity(size=1024):
    img = np.zeros((size, size, 4), dtype=np.float32)
    y, x = np.ogrid[:size, :size]
    cx, cy = size / 2.0, size / 2.0
    dx = x - cx
    dy = y - cy
    dist = np.sqrt(dx*dx + dy*dy)
    angle = np.arctan2(dy, dx) # [-pi, pi]

    # 1. Background radial gradient: Deep void nebula
    bg_t = np.clip(dist / (size * 0.5), 0.0, 1.0)
    # Center color: #160d2e (22, 13, 46), Outer color: #080511 (8, 5, 17)
    c_center = np.array([0.086, 0.051, 0.180])
    c_outer = np.array([0.031, 0.020, 0.067])
    bg_rgb = (1 - bg_t[:, :, None]) * c_center + bg_t[:, :, None] * c_outer

    # 2. Gravitational Accretion Waveform Bars
    # 48 radial sound bars
    num_bars = 48
    bar_width_rad = (2 * math.pi / num_bars) * 0.42
    norm_angle = (angle + math.pi) % (2 * math.pi)
    bar_idx = np.floor(norm_angle / (2 * math.pi / num_bars)).astype(int)
    bar_center_angle = (bar_idx + 0.5) * (2 * math.pi / num_bars)
    angle_diff = np.abs(norm_angle - bar_center_angle)

    # Soundwave amplitude per bar using combination of harmonics
    # Bar amplitudes shaped to form a dynamic musical horizon
    k = bar_idx / num_bars
    amp = 0.55 + 0.35 * np.sin(k * 4 * math.pi) + 0.15 * np.cos(k * 10 * math.pi)
    amp = np.clip(amp, 0.25, 1.0)

    inner_r = size * 0.18
    max_outer_r = size * 0.41
    bar_outer_r = inner_r + amp * (max_outer_r - inner_r)

    in_bar_angle = angle_diff < bar_width_rad
    in_bar_dist = (dist >= inner_r) & (dist <= bar_outer_r)
    bar_mask = (in_bar_angle & in_bar_dist).astype(np.float32)

    # Gradient along radius: inner cyan (#06B6D4), middle violet (#8B5CF6), outer pink/fuchsia (#EC4899)
    radial_pos = np.clip((dist - inner_r) / (max_outer_r - inner_r + 1e-5), 0.0, 1.0)
    
    col_cyan = np.array([0.024, 0.714, 0.831])
    col_violet = np.array([0.545, 0.361, 0.965])
    col_pink = np.array([0.925, 0.282, 0.600])

    bar_color = np.where(
        radial_pos[:, :, None] < 0.5,
        (1 - radial_pos[:, :, None]*2) * col_cyan + (radial_pos[:, :, None]*2) * col_violet,
        (1 - (radial_pos[:, :, None]-0.5)*2) * col_violet + ((radial_pos[:, :, None]-0.5)*2) * col_pink
    )

    # Combine bars onto background
    comp_rgb = bg_rgb * (1.0 - bar_mask[:, :, None]) + bar_color * bar_mask[:, :, None]

    # Convert to PIL for smooth glow and central singularity circle
    base_img = Image.fromarray((comp_rgb * 255).astype(np.uint8), mode="RGB")
    
    # Create glow overlay
    glow_bar = Image.fromarray((bar_mask * 255).astype(np.uint8), mode="L")
    glow_colored = Image.fromarray((bar_color * 255).astype(np.uint8), mode="RGB")
    glow_layer = Image.composite(glow_colored, Image.new("RGB", (size, size), (0, 0, 0)), glow_bar)
    glow_layer = glow_layer.filter(ImageFilter.GaussianBlur(radius=size * 0.025))

    # Screen blend base + glow
    base_np = np.array(base_img, dtype=np.float32) / 255.0
    glow_np = np.array(glow_layer, dtype=np.float32) / 255.0
    blended_np = 1.0 - (1.0 - base_np) * (1.0 - glow_np * 0.8)

    # Draw Central Event Horizon (Pitch-black sphere with glowing cyan/violet ring)
    result_img = Image.fromarray((blended_np * 255).astype(np.uint8), mode="RGB")
    draw = ImageDraw.Draw(result_img, "RGBA")

    # Outer horizon glow ring
    for r_offset, alpha in [(12, 40), (8, 80), (4, 160), (2, 230)]:
        r = inner_r + r_offset
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(6, 182, 212, alpha), width=3)
    
    # Event horizon black circle
    draw.ellipse([cx - inner_r, cy - inner_r, cx + inner_r, cy + inner_r], fill=(3, 2, 8, 255))

    # Minimalist central Void Starlight (4-point star in center)
    star_r = size * 0.05
    star_pts = [
        (cx, cy - star_r),
        (cx + star_r * 0.2, cy - star_r * 0.2),
        (cx + star_r, cy),
        (cx + star_r * 0.2, cy + star_r * 0.2),
        (cx, cy + star_r),
        (cx - star_r * 0.2, cy + star_r * 0.2),
        (cx - star_r, cy),
        (cx - star_r * 0.2, cy - star_r * 0.2),
    ]
    draw.polygon(star_pts, fill=(224, 242, 254, 240))
    # Star center dot
    dot_r = size * 0.012
    draw.ellipse([cx - dot_r, cy - dot_r, cx + dot_r, cy + dot_r], fill=(255, 255, 255, 255))

    # Apply squircle mask
    mask = create_squircle_mask(size)
    final_rgba = np.dstack([np.array(result_img), (mask * 255).astype(np.uint8)])
    return Image.fromarray(final_rgba, mode="RGBA")

# ==============================================================================
# ICON 2: NEON MONOLITH V (Geometric Cyber Facets)
# ==============================================================================
def render_icon_monolith_v(size=1024):
    # Dark stealth background with isometric cyber grid
    bg = Image.new("RGBA", (size, size), (7, 10, 18, 255))
    draw_bg = ImageDraw.Draw(bg)

    # Draw subtle background sound frequency lines
    grid_color = (25, 33, 52, 90)
    for y_pos in range(int(size * 0.12), int(size * 0.88), int(size * 0.04)):
        draw_bg.line([(size * 0.1, y_pos), (size * 0.9, y_pos)], fill=grid_color, width=2)

    cx, cy = size / 2.0, size / 2.0
    scale = size / 1024.0

    # Monolith V Facets geometry (Precision 3D angled polygons)
    # Top vertices:
    # Left outer top: (210, 240)
    # Left inner top: (370, 240)
    # Center top notch: (512, 480)
    # Right inner top: (654, 240)
    # Right outer top: (814, 240)
    # Bottom apex: (512, 810)
    # Spine center: (512, 630)

    p_l_out = (220 * scale, 240 * scale)
    p_l_mid = (370 * scale, 240 * scale)
    p_center_notch = (512 * scale, 480 * scale)
    p_r_mid = (654 * scale, 240 * scale)
    p_r_out = (804 * scale, 240 * scale)
    p_bottom = (512 * scale, 810 * scale)
    p_spine = (512 * scale, 630 * scale)

    # Intermediate facet split points for luxury 3D gemstone refraction:
    p_l_facet = (340 * scale, 520 * scale)
    p_r_facet = (684 * scale, 520 * scale)

    # Draw Monolith Ambient Underglow (Gaussian blur layer)
    glow_canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow_canvas)
    glow_poly = [p_l_out, p_l_mid, p_center_notch, p_r_mid, p_r_out, p_bottom]
    glow_draw.polygon(glow_poly, fill=(56, 189, 248, 140))
    glow_canvas = glow_canvas.filter(ImageFilter.GaussianBlur(radius=size * 0.06))
    bg.alpha_composite(glow_canvas)

    # Foreground Facets Drawing
    v_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw_v = ImageDraw.Draw(v_layer)

    # Facet 1: Left Outer Wing (Cyan -> Blue gradient simulated with polygons or rich tones)
    # Gradient simulation:
    draw_v.polygon([p_l_out, p_l_mid, p_l_facet], fill=(56, 189, 248, 255))      # Polar Cyan highlight
    draw_v.polygon([p_l_out, p_l_facet, p_bottom], fill=(2, 132, 199, 255))      # Deep Arctic Blue

    # Facet 2: Left Inner Bevel (Deep Indigo -> Violet)
    draw_v.polygon([p_l_mid, p_center_notch, p_spine, p_l_facet], fill=(79, 70, 229, 255)) # Electric Indigo
    draw_v.polygon([p_l_facet, p_spine, p_bottom], fill=(67, 56, 202, 255))      # Midnight Royal

    # Facet 3: Right Inner Bevel (Cosmic Violet -> Purple)
    draw_v.polygon([p_r_mid, p_center_notch, p_spine, p_r_facet], fill=(139, 92, 246, 255)) # Cosmic Violet
    draw_v.polygon([p_r_facet, p_spine, p_bottom], fill=(109, 40, 217, 255))     # Deep Violet Shadow

    # Facet 4: Right Outer Wing (Electric Ultraviolet -> Deep Magenta)
    draw_v.polygon([p_r_out, p_r_mid, p_r_facet], fill=(168, 85, 247, 255))     # Orchid Neon
    draw_v.polygon([p_r_out, p_r_facet, p_bottom], fill=(126, 34, 206, 255))    # Dark Prismatic

    # Crisp Bevel Accent Lines (Laser highlight)
    draw_v.line([p_l_out, p_bottom], fill=(224, 242, 254, 200), width=int(3 * scale))
    draw_v.line([p_spine, p_bottom], fill=(255, 255, 255, 240), width=int(4 * scale))
    draw_v.line([p_center_notch, p_spine], fill=(216, 180, 254, 180), width=int(2 * scale))
    draw_v.line([p_l_mid, p_l_facet], fill=(186, 230, 253, 160), width=int(2 * scale))
    draw_v.line([p_r_mid, p_r_facet], fill=(233, 213, 255, 160), width=int(2 * scale))

    # Top floating audio beacon (a small glowing diamond at the center notch)
    beacon_cy = 380 * scale
    b_size = 18 * scale
    beacon_pts = [
        (cx, beacon_cy - b_size),
        (cx + b_size * 0.7, beacon_cy),
        (cx, beacon_cy + b_size),
        (cx - b_size * 0.7, beacon_cy)
    ]
    draw_v.polygon(beacon_pts, fill=(56, 189, 248, 255))
    draw_v.ellipse([cx - 4*scale, beacon_cy - 4*scale, cx + 4*scale, beacon_cy + 4*scale], fill=(255, 255, 255, 255))

    bg.alpha_composite(v_layer)

    # Squircle frame
    mask = create_squircle_mask(size)
    final_rgba = np.dstack([np.array(bg.convert("RGB")), (mask * 255).astype(np.uint8)])
    return Image.fromarray(final_rgba, mode="RGBA")

# ==============================================================================
# ICON 3: SOUND ECLIPSE (Vinyl Corona & Minimalist Ray)
# ==============================================================================
def render_icon_sound_eclipse(size=1024):
    # Deep matte charcoal background #0C0D11
    bg = Image.new("RGBA", (size, size), (12, 13, 17, 255))
    draw = ImageDraw.Draw(bg)

    cx, cy = size * 0.46, size * 0.48
    vinyl_r = size * 0.32

    # Draw Vinyl Micro-grooves in background
    for r in range(int(size * 0.12), int(size * 0.44), 6):
        alpha = int(25 + 15 * math.sin(r * 0.2))
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(38, 42, 53, alpha), width=1)

    # Draw Glowing Solar Eclipse Sound Corona
    corona_canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    c_draw = ImageDraw.Draw(corona_canvas)

    # Multiple blurred warm color crescents: Amber -> Sunset Orange -> Magenta
    corona_cx, corona_cy = size * 0.52, size * 0.50
    for offset, w, col in [
        (18, 48, (245, 158, 11, 70)),   # Amber #F59E0B
        (12, 32, (244, 63, 94, 110)),   # Rose/Coral #F43F5E
        (6, 18, (168, 85, 247, 180)),   # Cosmic Violet #A855F7
        (0, 8, (254, 243, 199, 255)),   # Radiant Gold White
    ]:
        c_draw.arc(
            [corona_cx - vinyl_r - offset, corona_cy - vinyl_r - offset,
             corona_cx + vinyl_r + offset, corona_cy + vinyl_r + offset],
            start=-45, end=135, fill=col, width=w
        )
    
    corona_canvas = corona_canvas.filter(ImageFilter.GaussianBlur(radius=size * 0.035))
    bg.alpha_composite(corona_canvas)

    # Pure Black Occluding Disc (The "Void Disc")
    draw.ellipse([cx - vinyl_r, cy - vinyl_r, cx + vinyl_r, cy + vinyl_r], fill=(10, 11, 15, 255))

    # Inner spindle hole of the disc
    spindle_r = size * 0.04
    draw.ellipse([cx - spindle_r, cy - spindle_r, cx + spindle_r, cy + spindle_r], fill=(22, 24, 32, 255), outline=(56, 189, 248, 120), width=2)

    # Diagonal Minimalist Sonic Needle Beam forming abstract "V"
    # Ray 1: Tangent cutting along right side
    p_ray_start = (size * 0.32, size * 0.16)
    p_apex = (size * 0.58, size * 0.84)
    p_ray_right = (size * 0.84, size * 0.28)

    # Draw sharp sleek laser lines
    draw.line([p_ray_start, p_apex], fill=(56, 189, 248, 220), width=int(size * 0.008))
    draw.line([p_apex, p_ray_right], fill=(245, 158, 11, 220), width=int(size * 0.008))

    # Turntable stylus glowing point at the apex
    apex_r = size * 0.016
    draw.ellipse([p_apex[0] - apex_r*2, p_apex[1] - apex_r*2, p_apex[0] + apex_r*2, p_apex[1] + apex_r*2], fill=(56, 189, 248, 60))
    draw.ellipse([p_apex[0] - apex_r, p_apex[1] - apex_r, p_apex[0] + apex_r, p_apex[1] + apex_r], fill=(255, 255, 255, 255))

    # Squircle frame
    mask = create_squircle_mask(size)
    final_rgba = np.dstack([np.array(bg.convert("RGB")), (mask * 255).astype(np.uint8)])
    return Image.fromarray(final_rgba, mode="RGBA")

# ==============================================================================
# ICON 4: CYBER HARMONIC RESONANCE (Lissajous Laser Wave)
# ==============================================================================
def render_icon_cyber_resonance(size=1024):
    # Deep midnight space background
    bg = Image.new("RGBA", (size, size), (5, 6, 12, 255))
    draw = ImageDraw.Draw(bg)

    cx, cy = size / 2.0, size * 0.48
    scale = size * 0.36

    # Parametric Lissajous Harmonograph curve forming an ethereal "V" / infinity resonance
    t = np.linspace(0, 2 * np.pi, 2000)
    # Math harmonic formula:
    # x = sin(t) * (1.1 - 0.25 * cos(2t))
    # y = -0.85 * cos(t) + 0.45 * sin(2t) - 0.15 * cos(3t)
    xs = cx + scale * (np.sin(t) * (1.05 - 0.25 * np.cos(2 * t)))
    ys = cy + scale * (-0.8 * np.cos(t) + 0.45 * np.abs(np.sin(t)) - 0.1)

    points = list(zip(xs, ys))

    # Glow layer
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)

    # Multi-pass glow with gradient colors
    for i in range(len(points) - 1):
        ratio = i / float(len(points))
        # Color transition: Neon Cyan (#06B6D4) -> Electric Violet (#8B5CF6) -> Sunset Rose (#F43F5E)
        if ratio < 0.5:
            k = ratio * 2
            r = int(6 * (1 - k) + 139 * k)
            g = int(182 * (1 - k) + 92 * k)
            b = int(212 * (1 - k) + 246 * k)
        else:
            k = (ratio - 0.5) * 2
            r = int(139 * (1 - k) + 244 * k)
            g = int(92 * (1 - k) + 63 * k)
            b = int(246 * (1 - k) + 94 * k)
        
        glow_draw.line([points[i], points[i+1]], fill=(r, g, b, 120), width=int(size * 0.03))

    glow = glow.filter(ImageFilter.GaussianBlur(radius=size * 0.025))
    bg.alpha_composite(glow)

    # Sharp foreground laser line
    sharp_draw = ImageDraw.Draw(bg)
    for i in range(len(points) - 1):
        ratio = i / float(len(points))
        if ratio < 0.5:
            k = ratio * 2
            r = int(180 * (1 - k) + 220 * k)
            g = int(240 * (1 - k) + 180 * k)
            b = int(255)
        else:
            k = (ratio - 0.5) * 2
            r = int(220 * (1 - k) + 255 * k)
            g = int(180 * (1 - k) + 160 * k)
            b = int(255 * (1 - k) + 200 * k)

        sharp_draw.line([points[i], points[i+1]], fill=(r, g, b, 240), width=int(size * 0.009))

    # Add floating sound quantum particles around the node
    np.random.seed(42)
    for _ in range(32):
        px = cx + (np.random.rand() - 0.5) * size * 0.72
        py = cy + (np.random.rand() - 0.5) * size * 0.72
        pr = size * (0.003 + 0.005 * np.random.rand())
        alpha = int(80 + 150 * np.random.rand())
        sharp_draw.ellipse([px - pr, py - pr, px + pr, py + pr], fill=(147, 197, 253, alpha))

    # Center sound singularity pulse
    pulse_cy = cy + scale * 0.35
    pulse_r = size * 0.015
    sharp_draw.ellipse([cx - pulse_r*2, pulse_cy - pulse_r*2, cx + pulse_r*2, pulse_cy + pulse_r*2], fill=(56, 189, 248, 80))
    sharp_draw.ellipse([cx - pulse_r, pulse_cy - pulse_r, cx + pulse_r, pulse_cy + pulse_r], fill=(255, 255, 255, 255))

    # Squircle frame
    mask = create_squircle_mask(size)
    final_rgba = np.dstack([np.array(bg.convert("RGB")), (mask * 255).astype(np.uint8)])
    return Image.fromarray(final_rgba, mode="RGBA")

# ==============================================================================
# MAIN EXECUTION
# ==============================================================================
if __name__ == "__main__":
    print("Generating VoidTune experimental icons via NumPy & PIL...")

    icons = [
        ("icon_01_singularity.png", render_icon_singularity),
        ("icon_02_monolith_v.png", render_icon_monolith_v),
        ("icon_03_sound_eclipse.png", render_icon_sound_eclipse),
        ("icon_04_cyber_resonance.png", render_icon_cyber_resonance),
    ]

    for filename, render_func in icons:
        print(f"Rendering {filename} (1024x1024 supersampled -> 512x512)...")
        hi_res = render_func(size=1024)
        # Downsample with high-quality Lanczos for buttery smooth antialiasing
        final_img = hi_res.resize((512, 512), Image.Resampling.LANCZOS)
        out_path = os.path.join(OUTPUT_DIR, filename)
        final_img.save(out_path, format="PNG")
        print(f"Saved: {out_path}")

    print("All 4 icons rendered successfully!")
