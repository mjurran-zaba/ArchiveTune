# ARCHIVETUNE — System Architecture & Complete Flow Blueprint

Dokumentasi arsitektur komprehensif untuk proyek **ArchiveTune** (Fork resmi dari `rukamori/ArchiveTune` yang telah di-detach oleh `mjurran-zaba`).

---

## 1. Ringkasan Eksekutif & Identitas Proyek

- **Nama Proyek:** ArchiveTune
- **Kategori:** High-Performance YouTube Music Client & Local Audio Player
- **Platform:** Android (API 26 s/d 35+ / Android 8.0 – Android 15), dengan target mobile, Android Auto (automotive), dan Android TV.
- **Bahasa & Framework:**
  - **100% Kotlin** murni dengan paradigma fungsional & Coroutines/StateFlow.
  - **Jetpack Compose** untuk 100% antarmuka pengguna (Declarative UI).
  - **Dagger Hilt** untuk Dependency Injection (`@HiltAndroidApp`).
  - **AndroidX Media3 (ExoPlayer)** sebagai inti playback audio.
  - **Room Database** (`InternalDatabase`) untuk persistensi lokal (riwayat, lagu disukai, playlist, cache).
  - **Coil 3** untuk pemuatan gambar dan artwork dengan smart cache trimmer.
  - **C++ Native Layer** (`bitperfect`) untuk direct DAC playback.
  - **QuickJS / JavaScript Runtime** untuk ekstensi modul `.arch`.

---

## 2. Peta Modul Gradle (Multi-Module Architecture)

ArchiveTune dibangun dengan arsitektur multi-modul yang sangat modular dan terisolasi:

```text
ArchiveTune (Root)
│
├── :app                 -> UI Jetpack Compose, ViewModels, MediaService, Navigasi, Dependency Injection
├── :core                -> Innertube API Client (YouTube Music endpoint parser & models)
│
├── :lyrics              -> Sub-ekosistem penyedia lirik tersinkronisasi:
│   ├── :lyrics:kugou
│   ├── :lyrics:lrclib
│   ├── :lyrics:betterlyrics
│   ├── :lyrics:paxsenix (Apple Music & Spotify reverse-engineered lyrics)
│   ├── :lyrics:simpmusic
│   ├── :lyrics:unison
│   ├── :lyrics:youlyplus
│   ├── :lyrics:lrcred
│   ├── :lyrics:binimum
│   └── :lyrics:megalobiz
│
├── :modules             -> Engine modul runtime JS untuk plugin eksternal (.arch)
├── :canvas              -> Integrasi Spotify Canvas (video loop vertikal di latar pemutar)
├── :shazamkit           -> Mesin pengenalan musik (Music Recognition) via mikrofon / Quick Tile
├── :spotifycore         -> Resolver playlist & lagu disukai dari akun Spotify
├── :lastfm              -> Scrobbling riwayat putar ke Last.fm
└── :morideobfuscator    -> Utilitas proteksi string & dekoding token
```

---

## 3. Alur Kerja Inti (Core Data & Playback Flow)

### 3.1 Alur Pencarian & Resolusi Stream (Audio Resolution Pipeline)
Tantangan terbesar aplikasi pemutar YouTube Music pihak ketiga adalah blokir bot (HTTP 403 Forbidden) dan throttling dari Google Video Server (GVS). ArchiveTune menyelesaikan ini dengan arsitektur multi-layer:

```text
[User Tap Song] 
      │
      ▼
[ResolveAudioStreamUseCase]
      │
      ├──> Cek 1: Apakah lagu sudah diunduh offline? (ExoDownloadService) -> Putar lokal.
      │
      ├──> Cek 2: Apakah ada modul Lossless aktif? (ModuleMediaSource / .arch plugin)
      │            └─ Jika ya, panggil provider.js via QuickJS runtime.
      │
      └──> Cek 3: Resolusi YouTube Music Default:
                   │
                   ▼
            [YoutubeiStreamRepository] (Embedded youtubei.js runtime)
                   │
                   ▼
            [BotGuardTokenGenerator] (Menghasilkan PoToken / Proof-of-Origin)
                   │
                   ▼
            [GvsPoTokenVerifier] (Validasi token ke Google Video Server)
                   │
                   ▼
            [ChunkedDataSource] (Mengambil chunk audio Opus/AAC 160kbps via HTTP Range)
                   │
                   ▼
            [MusicService / ExoPlayer Audio Pipeline]
```

### 3.2 Alur Pemrosesan Audio (DSP & Audiophile Features)
Di dalam `MusicService.kt`:
1. **Bit-Perfect DAC Output:**
   - Memotong (*bypass*) resampler audio 48kHz bawaan OS Android melalui modul C++ JNI (`app/src/main/cpp/bitperfect`). Lagu FLAC atau Hi-Res dipancarkan langsung dengan bit-rate & sample-rate asli ke USB DAC eksternal.
2. **EBU R128 Loudness Normalization:**
   - Menghitung nilai gain lagu secara otomatis untuk mencegah lonjakan volume mendadak antar lagu yang berbeda master.
3. **Crossfade Handler:**
   - Transisi pemotongan dan peleburan volume mulus (0–12 detik) saat berpindah trek.
4. **Equalizer & Effects:**
   - Equalizer parametrik kustom, BassBoost, Virtualizer panggung suara, dan LoudnessEnhancer.

---

## 4. Alur Mesin Lirik (Synced Lyrics Architecture)

Lirik di ArchiveTune dirancang memberikan pengalaman layaknya **Apple Music Time-Synced Lyrics**:

1. **Prioritas Penyedia Lirik (`PrepareLyricsUseCase`):**
   - Saat lagu berganti, sistem menanyakan penyedia lirik secara paralel berdasarkan prioritas:
     `Paxsenix (Apple Music / Spotify)` $\to$ `LrcLib` $\to$ `KuGou` $\to$ `BetterLyrics` $\to$ `SimpMusic` $\to$ `Embedded ID3 tag` $\to$ `YouTube CC Subtitles`.
2. **Parsing & Sinkronisasi:**
   - Format lirik `.lrc` dengan cap waktu milidetik (`[mm:ss.xx]`) diparsing ke dalam `LyricsEntry`.
3. **Penyajian di Compose UI (`LyricsScreen.kt`):**
   - Menggunakan animasi interpolasi posisi putar real-time.
   - Baris aktif mendapatkan pendaran cahaya (*glow effect*), pembesaran tipografi, dan teks bergerak secara mulus mengikuti vokal.
   - Dilengkapi modul romanisasi aksara (Kanji/Kana $\to$ Romaji, Hanzi $\to$ Pinyin) dan terjemahan instan.

---

## 5. Sistem Ekstensi Modul (`.arch` Package Format)

Fitur mutakhir ArchiveTune adalah **Plugin Modul Eksternal**:
- Format berkas: File ZIP berekstensi `.arch` berisi:
  ```text
  my-provider.arch
  ├── manifest.json   (Metadata, id, author, capabilities)
  ├── provider.js     (Logika JavaScript untuk fetch data stream/lirik)
  └── icon.png        (Ikon opsional)
  ```
- **Fungsi Modul:**
  - Menyediakan sumber audio alternatif (misal: FLAC lossless dari server pribadi/Tidal/Qobuz).
  - Menambah penyedia lirik kustom.
  - Menghubungkan kanvas video dari sumber eksternal.
- Inti aplikasi tetap menjaga antarmuka dan riwayat lagu, sementara pengambilan bitstream dialihkan ke kode JavaScript yang dieksekusi di dalam sandbox `ModuleRuntime`.

---

## 6. Fitur Sosial & Sinkronisasi

1. **Music Together (`TogetherServer` & `TogetherClient`):**
   - Fitur mendengarkan musik bersama teman secara real-time via koneksi WebSocket/P2P.
   - `TogetherClock` mengoreksi latensi jaringan agar pemutaran lagu di perangkat host dan tamu sinkron pada milidetik yang sama.
2. **Discord Rich Presence (`GatewayClient`):**
   - Menghubungkan akun Discord secara native tanpa perantara PC, menampilkan album art, nama lagu, dan durasi di profil status Discord.
3. **Music Recognition (ShazamKit):**
   - Merekam audio sekitar via mikrofon, mengekstrak sidik jari audio, mencari kecocokan judul lagu, dan langsung memasukkannya ke antrean putar.
4. **Spotify Import (`spotifycore`):**
   - Mengambil daftar putar dan lagu favorit dari Spotify, lalu secara cerdas mencocokkannya ke audio stream YouTube Music dengan metadata yang tepat.

---

## 7. Nilai Desain & Kaitan dengan `NOTEZ`

ArchiveTune adalah inspirasi utama dari komponen **Floating Glass Mini Player** di proyek `NOTEZ`:
- Kapsul melayang setinggi 56dp dengan pendaran kaca (*glassmorphism*).
- Cincin progress bar melingkar pada thumbnail album.
- Gestur usap (*swipe horizontal*) untuk mengganti trek tanpa membuka layar penuh.

---

*Disusun & diverifikasi oleh AI Agent untuk Knowledge Base Obsidian / Google Drive `PROJECTS/ArchiveTune/`.*
