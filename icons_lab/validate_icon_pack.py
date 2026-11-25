import hashlib
import json
import os
import xml.etree.ElementTree as ET

METADATA_FILE = "/home/user/VoidTune/IconPack/metadata.json"
SVG_DIR = "/home/user/VoidTune/IconPack/svg"

with open(METADATA_FILE, "r") as f:
    catalog = json.load(f)

print(f"Loaded {len(catalog)} entries from metadata.json")

seen_ids = set()
seen_hashes = set()

for i, entry in enumerate(catalog):
    assert "Id" in entry and entry["Id"], f"Entry {i} missing Id"
    assert "Name" in entry and entry["Name"], f"Entry {i} missing Name"
    assert "Author" in entry and entry["Author"], f"Entry {i} missing Author"
    assert "Source" in entry and entry["Source"], f"Entry {i} missing Source"
    
    icon_id = entry["Id"]
    assert icon_id != "default", "Cannot use reserved id 'default'"
    assert icon_id not in seen_ids, f"Duplicate ID {icon_id}"
    seen_ids.add(icon_id)
    
    sha256_prefix = hashlib.sha256(icon_id.encode('utf-8')).hexdigest()[:12]
    assert sha256_prefix not in seen_hashes, f"Hash collision for {icon_id}"
    seen_hashes.add(sha256_prefix)
    
    svg_path = os.path.join(SVG_DIR, entry["Source"])
    assert os.path.isfile(svg_path), f"Source file {svg_path} not found"
    
    tree = ET.parse(svg_path)
    root = tree.getroot()
    assert root.tag.endswith("svg"), f"Root must be svg, got {root.tag}"
    viewBox = root.get("viewBox", "").strip().split()
    assert len(viewBox) == 4, f"viewBox must have 4 elements: {viewBox}"
    w, h = float(viewBox[2]), float(viewBox[3])
    assert w > 0 and h > 0, "Width and height must be > 0"
    
    # Check for paths and rects
    paths = list(root.iter("{http://www.w3.org/2000/svg}path")) + list(root.iter("path"))
    rects = list(root.iter("{http://www.w3.org/2000/svg}rect")) + list(root.iter("rect"))
    assert len(paths) + len(rects) > 0, f"No path or rect in {svg_path}"
    
    # Check background rect
    has_full_rect = False
    for r in rects:
        rx = float(r.get("x", "0"))
        ry = float(r.get("y", "0"))
        rw = float(r.get("width", "0"))
        rh = float(r.get("height", "0"))
        if rx <= 10.24 and ry <= 10.24 and rx + rw >= 1024 - 10.24 and ry + rh >= 1024 - 10.24:
            has_full_rect = True
            break
    assert has_full_rect, f"Expected full background rect in {svg_path}"
    
    print(f"✓ Validated [{entry['Id']}] -> {entry['Name']} (hash: {sha256_prefix}, paths: {len(paths)}, rects: {len(rects)})")

print("\nALL ICONS FULLY VALIDATED AGAINST GRADLE TASK CRITERIA!")
