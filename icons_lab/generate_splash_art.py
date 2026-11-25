import math
import numpy as np
from PIL import Image, ImageDraw, ImageFilter

OUTPUT_PATH = "/home/user/VoidTune/app/src/main/res/drawable/voidtune_splash_seal.png"
LAB_PREVIEW = "/home/user/VoidTune/icons_lab/voidtune_splash_art.png"

def render_splash_art(size=1024):
    """
    Renders the luxury master procedural splash emblem for VoidTune:
    - High-contrast transparent background
    - Accretion soundwave disk in neon cyan & cosmic violet
    - Central Faceted 3D 'V' Monolith with laser bevels
    - Starlight diamond beacon and glowing lens flares
    """
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    cx, cy = size / 2.0, size / 2.0

    # 1. Outer Accretion Soundwave Horizon (48 radial visualizer bars)
    bar_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bar_draw = ImageDraw.Draw(bar_layer)

    num_bars = 48
    inner_r = size * 0.22
    max_outer_r = size * 0.44

    for i in range(num_bars):
        k = i / float(num_bars)
        angle_rad = i * (2 * math.pi / num_bars)
        
        # Audio harmonics amplitude
        amp = 0.5 + 0.38 * math.sin(k * 4 * math.pi) + 0.12 * math.cos(k * 8 * math.pi)
        amp = max(0.2, min(1.0, amp))
        outer_r = inner_r + amp * (max_outer_r - inner_r)

        # Gradient color: Polar Cyan (#38BDF8) -> Cosmic Violet (#8B5CF6) -> Neon Pink (#EC4899)
        t_col = (math.sin(k * 2 * math.pi) + 1.0) / 2.0
        if t_col < 0.5:
            p = t_col * 2
            r = int(56 * (1 - p) + 139 * p)
            g = int(189 * (1 - p) + 92 * p)
            b = int(248 * (1 - p) + 246 * p)
        else:
            p = (t_col - 0.5) * 2
            r = int(139 * (1 - p) + 236 * p)
            g = int(92 * (1 - p) + 72 * p)
            b = int(246 * (1 - p) + 153 * p)

        # Bar endpoints
        x1 = cx + inner_r * math.cos(angle_rad)
        y1 = cy + inner_r * math.sin(angle_rad)
        x2 = cx + outer_r * math.cos(angle_rad)
        y2 = cy + outer_r * math.sin(angle_rad)

        bar_draw.line([(x1, y1), (x2, y2)], fill=(r, g, b, 230), width=int(size * 0.012))

    # Glow layer for accretion bars
    glow_bars = bar_layer.filter(ImageFilter.GaussianBlur(radius=size * 0.025))
    canvas.alpha_composite(glow_bars)
    canvas.alpha_composite(bar_layer)

    # 2. Central Event Horizon Rim
    horizon_draw = ImageDraw.Draw(canvas)
    horizon_draw.ellipse(
        [cx - inner_r - 4, cy - inner_r - 4, cx + inner_r + 4, cy + inner_r + 4],
        outline=(139, 92, 246, 120), width=int(size * 0.008)
    )
    horizon_draw.ellipse(
        [cx - inner_r, cy - inner_r, cx + inner_r, cy + inner_r],
        fill=(8, 10, 18, 255), outline=(56, 189, 248, 240), width=int(size * 0.006)
    )

    # 3. Faceted 3D "V" Monolith in the Center
    scale = size / 1024.0
    v_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw_v = ImageDraw.Draw(v_layer)

    # Monolith coordinates centered inside inner_r (radius ~225)
    # Target center around (512, 512)
    p_l_out = (cx - 145 * scale, cy - 130 * scale)
    p_l_mid = (cx - 65 * scale, cy - 130 * scale)
    p_notch = (cx, cy - 10 * scale)
    p_r_mid = (cx + 65 * scale, cy - 130 * scale)
    p_r_out = (cx + 145 * scale, cy - 130 * scale)
    p_bottom = (cx, cy + 155 * scale)
    p_spine = (cx, cy + 65 * scale)

    p_l_facet = (cx - 80 * scale, cy + 10 * scale)
    p_r_facet = (cx + 80 * scale, cy + 10 * scale)

    # Facet polygons
    draw_v.polygon([p_l_out, p_l_mid, p_l_facet], fill=(56, 189, 248, 255))      # Polar Cyan highlight
    draw_v.polygon([p_l_out, p_l_facet, p_bottom], fill=(2, 132, 199, 255))      # Deep Arctic Blue
    draw_v.polygon([p_l_mid, p_notch, p_spine, p_l_facet], fill=(79, 70, 229, 255)) # Electric Indigo
    draw_v.polygon([p_l_facet, p_spine, p_bottom], fill=(67, 56, 202, 255))      # Midnight Royal
    draw_v.polygon([p_r_mid, p_notch, p_spine, p_r_facet], fill=(139, 92, 246, 255)) # Cosmic Violet
    draw_v.polygon([p_r_facet, p_spine, p_bottom], fill=(109, 40, 217, 255))     # Deep Violet Shadow
    draw_v.polygon([p_r_out, p_r_mid, p_r_facet], fill=(168, 85, 247, 255))     # Orchid Neon
    draw_v.polygon([p_r_out, p_r_facet, p_bottom], fill=(126, 34, 206, 255))    # Dark Prismatic

    # Laser Bevel lines
    draw_v.line([p_l_out, p_bottom], fill=(224, 242, 254, 220), width=int(3 * scale))
    draw_v.line([p_spine, p_bottom], fill=(255, 255, 255, 255), width=int(4 * scale))
    draw_v.line([p_notch, p_spine], fill=(216, 180, 254, 200), width=int(2 * scale))
    draw_v.line([p_l_mid, p_l_facet], fill=(186, 230, 253, 180), width=int(2 * scale))
    draw_v.line([p_r_mid, p_r_facet], fill=(233, 213, 255, 180), width=int(2 * scale))

    # Floating Starlight Beacon above center notch
    beacon_cy = cy - 60 * scale
    b_size = 14 * scale
    beacon_pts = [
        (cx, beacon_cy - b_size),
        (cx + b_size * 0.7, beacon_cy),
        (cx, beacon_cy + b_size),
        (cx - b_size * 0.7, beacon_cy)
    ]
    draw_v.polygon(beacon_pts, fill=(56, 189, 248, 255))
    draw_v.ellipse([cx - 4*scale, beacon_cy - 4*scale, cx + 4*scale, beacon_cy + 4*scale], fill=(255, 255, 255, 255))

    canvas.alpha_composite(v_layer)

    return canvas

if __name__ == "__main__":
    print("Generating master VoidTune splash emblem...")
    hi_res = render_splash_art(1024)
    # Save 1024x1024 master
    hi_res.save(OUTPUT_PATH, format="PNG")
    hi_res.save(LAB_PREVIEW, format="PNG")
    print(f"Saved splash artwork to {OUTPUT_PATH} and {LAB_PREVIEW}!")
