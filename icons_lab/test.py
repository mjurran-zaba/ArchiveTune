import numpy as np
from PIL import Image, ImageDraw, ImageFilter

size = 512
y, x = np.ogrid[:size, :size]
cx, cy = size / 2, size / 2
r = size * 0.44
dist = ((np.abs(x - cx) / r) ** 4.5 + (np.abs(y - cy) / r) ** 4.5) ** (1 / 4.5)
mask = np.clip((1.0 - (dist - 0.98) / 0.04) * 255, 0, 255).astype(np.uint8)
img = Image.fromarray(mask)
print('Squircle mask shape:', mask.shape, 'mean:', mask.mean())
