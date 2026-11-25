import os
import math
import numpy as np
from PIL import Image, ImageDraw, ImageFilter

OUTPUT_DIR = "/home/user/VoidTune/icons_lab"

def create_squircle_mask(size, radius_ratio=0.44, power=4.5):
    y, x = np.ogrid[:size, :size]
    cx, cy = size / 2.0, size / 2.0
    r = size * radius_ratio
    dist = ((np.abs(x - cx) / r) ** power + (np.abs(y - cy) / r) ** power) ** (1.0 / power)
    edge_width = 3.0 / size
    return np.clip((1.0 - (dist - 1.0 + edge_width) / edge_width), 0.0, 1.0)

# ==============================================================================
# ICON 5: EQUALIZER V-PULSE (Digital Sound Spectrum in Negative Space)
# ==============================================================================
def render_icon_equalizer_v(size=1024):
    bg = Image.new("RGBA", (size, size), (8, 9, 14, 255))
    draw = ImageDraw.Draw(bg)

    cx, cy = size / 2.0, size / 2.0
    
    # 21 vertical equalizer bars centered
    num_bars = 21
    bar_w = size * 0.024
    spacing = size * 0.034
    total_w = num_bars * spacing
    start_x = cx - total_w / 2.0 + spacing / 2.0

    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)

    for i in range(num_bars):
        bx = start_x + i * spacing
        # normalized distance from center [-1 .. 1]
        t = (i - (num_bars - 1) / 2.0) / ((num_bars - 1) / 2.0)
        
        # V-shape envelope: center is lowest, edges are highest
        # Height follows a steep V-curve
        base_h = size * (0.15 + 0.55 * (abs(t) ** 1.35))
        # Add dynamic music jitter
        jitter = 0.04 * math.sin(i * 1.5) * size
        h = base_h + jitter

        top_y = cy - h / 2.0
        bot_y = cy + h / 2.0

        # Color: Polar Cyan at left, Violet in center, Sunset Amber at right
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

        # Rounded rectangle bar
        # Glow
        glow_draw.rounded_rectangle([bx - bar_w/2, top_y, bx + bar_w/2, bot_y], radius=bar_w/2, fill=(r, g, b, 140))
        # Crisp bar
        draw.rounded_rectangle([bx - bar_w/2, top_y, bx + bar_w/2, bot_y], radius=bar_w/2, fill=(r, g, b, 255))

    glow = glow.filter(ImageFilter.GaussianBlur(radius=size * 0.035))
    # Composite glow under crisp bars
    base_comp = Image.alpha_composite(bg, glow)
    # Re-draw crisp on top
    final_canvas = Image.new("RGBA", (size, size), (8, 9, 14, 255))
    final_canvas.alpha_composite(glow)
    final_canvas.alpha_composite(bg)

    # Ambient soundwave horizon line at center
    f_draw = ImageDraw.Draw(final_canvas)
    f_draw.line([(size*0.12, cy), (size*0.88, cy)], fill=(255, 255, 255, 40), width=2)

    # Squircle frame
    mask = create_squircle_mask(size)
    final_rgba = np.dstack([np.array(final_canvas.convert("RGB")), (mask * 255).astype(np.uint8)])
    return Image.fromarray(final_rgba, mode="RGBA")

# ==============================================================================
# ICON 6: NORDIC VOID PRISM (Frosted Hexagonal Crystal)
# ==============================================================================
def render_icon_nordic_prism(size=1024):
    bg = Image.new("RGBA", (size, size), (10, 14, 23, 255)) # Dark slate
    draw = ImageDraw.Draw(bg)

    cx, cy = size / 2.0, size / 2.0
    scale = size * 0.38

    # Hexagonal Prism vertices
    angles = [math.radians(a) for a in range(30, 390, 60)]
    pts = [(cx + scale * math.cos(a), cy + scale * math.sin(a)) for a in angles]

    # Inner inverted triangular void forming V
    v_top_l = pts[5] # Top left
    v_top_r = pts[0] # Top right
    v_bot   = pts[2] # Bottom center (apex)
    center  = (cx, cy)

    # Facet 1: Top Upper
    draw.polygon([pts[5], pts[0], center], fill=(30, 41, 59, 255)) # Slate dark
    # Facet 2: Top Right
    draw.polygon([pts[0], pts[1], center], fill=(56, 189, 248, 255)) # Polar Cyan
    # Facet 3: Bottom Right
    draw.polygon([pts[1], pts[2], center], fill=(14, 116, 144, 255)) # Deep Cyan
    # Facet 4: Bottom Left
    draw.polygon([pts[2], pts[3], center], fill=(99, 102, 241, 255)) # Indigo
    # Facet 5: Top Left
    draw.polygon([pts[3], pts[4], center], fill=(129, 140, 248, 255)) # Light Indigo
    # Facet 6: Top Upper Left
    draw.polygon([pts[4], pts[5], center], fill=(71, 85, 105, 255)) # Slate mid

    # Negative Space Sound "V" carving through the center
    v_path = [
        (cx - scale*0.5, cy - scale*0.45),
        (cx - scale*0.2, cy - scale*0.45),
        (cx, cy + scale*0.35),
        (cx + scale*0.2, cy - scale*0.45),
        (cx + scale*0.5, cy - scale*0.45),
        (cx, cy + scale*0.75),
    ]
    draw.polygon(v_path, fill=(10, 14, 23, 255))

    # Glowing edge accents
    draw.line([v_path[0], v_path[5], v_path[4]], fill=(224, 242, 254, 220), width=int(size*0.008))
    draw.line([v_path[1], v_path[2], v_path[3]], fill=(56, 189, 248, 240), width=int(size*0.008))

    # Central glowing resonance dot
    dot_cy = cy - scale*0.1
    draw.ellipse([cx - 8, dot_cy - 8, cx + 8, dot_cy + 8], fill=(255, 255, 255, 255))

    # Squircle frame
    mask = create_squircle_mask(size)
    final_rgba = np.dstack([np.array(bg.convert("RGB")), (mask * 255).astype(np.uint8)])
    return Image.fromarray(final_rgba, mode="RGBA")

if __name__ == "__main__":
    print("Rendering Icon 5 & 6...")
    i5 = render_icon_equalizer_v(1024).resize((512, 512), Image.Resampling.LANCZOS)
    i5.save(os.path.join(OUTPUT_DIR, "icon_05_equalizer_v.png"))

    i6 = render_icon_nordic_prism(1024).resize((512, 512), Image.Resampling.LANCZOS)
    i6.save(os.path.join(OUTPUT_DIR, "icon_06_nordic_prism.png"))
    print("Saved Icon 5 & 6 successfully!")
