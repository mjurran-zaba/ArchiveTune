import json
import shutil
import os

FORK_DIR = "/tmp/IconPackFork"
LOCAL_DIR = "/home/user/VoidTune/IconPack"

our_entries = [
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

# Copy SVGs to fork
for entry in our_entries:
    src = os.path.join(LOCAL_DIR, "svg", entry["Source"])
    dst = os.path.join(FORK_DIR, "svg", entry["Source"])
    shutil.copyfile(src, dst)
    print(f"Copied {entry['Source']} to {dst}")

# Read existing fork metadata
fork_meta_file = os.path.join(FORK_DIR, "metadata.json")
with open(fork_meta_file, "r") as f:
    existing = json.load(f)

# Combine: our entries at the very top, followed by existing
combined = our_entries + [item for item in existing if item["Id"] not in [x["Id"] for x in our_entries]]

with open(fork_meta_file, "w", encoding="utf-8") as f:
    json.dump(combined, f, indent=2)

with open(os.path.join(LOCAL_DIR, "metadata.json"), "w", encoding="utf-8") as f:
    json.dump(combined, f, indent=2)

# Copy all SVGs from fork to local IconPack/svg as well
for f_name in os.listdir(os.path.join(FORK_DIR, "svg")):
    src = os.path.join(FORK_DIR, "svg", f_name)
    dst = os.path.join(LOCAL_DIR, "svg", f_name)
    if not os.path.exists(dst):
        shutil.copyfile(src, dst)

print(f"Successfully integrated! Total icons now: {len(combined)}")
