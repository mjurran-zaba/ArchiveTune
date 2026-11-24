<table border="0" style="width: 100%; border: none;">
  <tr>
    <td width="260" align="center" valign="top" style="border: none; padding-right: 24px;">
      <img src="app/src/main/res/drawable/ic_voidtune_seal.png" width="220" height="220" alt="VoidTune Emblem" style="border-radius: 28px;" />
      <br /><br />
      <h3><b>VoidTune</b></h3>
      <p><i>The Resonant Void Arc</i></p>
      <a href="https://github.com/mjurran-zaba/ArchiveTune/releases"><img src="https://img.shields.io/badge/Release-v0.0.1-purple?style=for-the-badge&logo=android" alt="Download Release" /></a>
    </td>
    <td valign="top" style="border: none;">
      <h2>🌌 VoidTune (Mjurran Arc)</h2>
      <p>
        <strong>VoidTune</strong> is an ultra-optimized, high-performance Android music player fork engineered for instant 1–2 second audio starts, zero UI stutter, and seamless Spotify playlist synchronization, wrapped in an AMOLED pure-black aesthetic with gothic typography and responsive glassmorphism interfaces.
      </p>

      <hr />

      <h3>🏛️ Upstream Lineage & Source Ownership</h3>
      <p>
        VoidTune is built upon and proudly acknowledges its upstream foundation: 
        <strong><a href="https://github.com/rukamori/ArchiveTune">ArchiveTune</a></strong>, originally created and maintained by <strong><a href="https://github.com/rukamori">Rukamori</a></strong> (<a href="https://github.com/rukamori/ArchiveTune">https://github.com/rukamori/ArchiveTune</a>). 
        All original intellectual properties, foundations, and trademarks of ArchiveTune remain under the <strong>GNU General Public License v3.0 (GPL-3.0)</strong>.
      </p>

      <hr />

      <h3>⚡ Why Detached Fork & Reason for Enhancements</h3>
      <p>
        This standalone fork was created and actively refined by <strong>Mjurran</strong> to resolve critical usability bottlenecks encountered in daily listening:
      </p>
      <ul>
        <li><strong>Network Bottlenecks & Starvation:</strong> The upstream HTTP connection pool suffered from connection starvation, causing extreme slowdowns during simultaneous background library sync and user browsing.</li>
        <li><strong>Perpetual Playback Buffering:</strong> When logged into an account, BotGuard token checks and GVS HTTP 403 range rejections frequently resulted in endless buffering loops. VoidTune enforces a guest-patented playback stream engine that bypasses throttling while preserving full account synchronization for quick plays, suggestions, and playlists.</li>
        <li><strong>Streamlined Spotify Integration:</strong> Re-routed navigation drawer login directly to effortless Spotify Connect integration, delivering instantaneous playback without account-locking or authorization headaches.</li>
        <li><strong>Artistic Rebranding & UI Polish:</strong> Revamped with a vibrant galactic emblem, 45% faded grey charcoal glassmorphism modals, Gothic Unifraktur typography, and authentic QRIS support.</li>
      </ul>

      <hr />

      <h3>📜 Non-Destructive Code Modification Policy</h3>
      <p>
        In strict adherence to development transparency and respectful open-source ethics:
      </p>
      <blockquote>
        <strong>No original upstream code is ever erased, deleted, or silently overwritten.</strong><br />
        Every single modification preserves the original upstream source code inside comments, accompanied by a precise explanation of what the original code did, why it was commented out, and the corresponding improved VoidTune implementation written directly beneath it.
      </blockquote>
    </td>
  </tr>
</table>
