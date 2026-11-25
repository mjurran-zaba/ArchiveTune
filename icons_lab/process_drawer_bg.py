from PIL import Image, ImageEnhance

input_path = "/home/user/uploads/images (1)_1.jpeg"
output_path = "/home/user/VoidTune/app/src/main/res/drawable/drawer_bg.png"

img = Image.open(input_path).convert("RGBA")

# Increase contrast slightly (1.18x) so the black dress, amber violin, and hair glow pop vividly
contrast_enhancer = ImageEnhance.Contrast(img)
enhanced_img = contrast_enhancer.enhance(1.18)

# Enhance sharpness slightly (1.12x)
sharpness_enhancer = ImageEnhance.Sharpness(enhanced_img)
final_img = sharpness_enhancer.enhance(1.12)

# Save as PNG
final_img.save(output_path, format="PNG", optimize=True)
print(f"Processed and saved drawer background to {output_path} (size: {final_img.size})")
