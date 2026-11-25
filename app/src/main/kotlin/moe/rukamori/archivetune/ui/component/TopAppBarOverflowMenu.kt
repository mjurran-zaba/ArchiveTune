// Always meticulous in everyhthing to minimize the case
// Be honest in everything
// Be meticulous in everything
// Dont replace or delete code, coment and explain what and why, the write your change below
/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package moe.rukamori.archivetune.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.viewmodels.MainMenuDestination
import moe.rukamori.archivetune.viewmodels.MenuProfileState
import java.io.File
import moe.rukamori.archivetune.constants.AccountNameKey
import moe.rukamori.archivetune.constants.CustomProfileAvatarPathKey
import moe.rukamori.archivetune.constants.CustomProfileNameKey
import moe.rukamori.archivetune.constants.SpotifyAccountNameKey
import moe.rukamori.archivetune.constants.SpotifySpDcKey
import moe.rukamori.archivetune.utils.rememberPreference

@Composable
fun TopAppBarOverflowMenu(
    expanded: Boolean,
    profileState: MenuProfileState,
    accountImageUrl: String?,
    hazeState: HazeState,
    pureBlack: Boolean,
    hasUnreadNews: Boolean,
    hasUpdateAvailable: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (MainMenuDestination) -> Unit,
) {
    // - Always meticulous in everyhthing to minimize the case
    // [VoidTune Patch: High-Contrast Crisp White & Polar Cyan Palette for Drawer Items]
    // MAKSUD KODE ASLI: val contentColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.onSurface
    // MENGAPA DIKOMEN:
    // Dalam Light Mode, onSurface adalah warna abu gelap/hitam. Karena background drawer adalah lukisan gelap gadis biola,
    // teks hitam di Light Mode membuat menu drawer tidak terbaca sama sekali.
    // Menetapkan contentColor secara konsisten sebagai Putih Bersih (Color(0xFFF8FAFC)) menjamin kontras tinggi dan
    // keterbacaan sempurna di Light Mode maupun Dark Mode.
    // ORIGINAL CODE:
    // val contentColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.onSurface
    val contentColor = Color(0xFFF8FAFC)
    var showSupportQrDialog by remember { mutableStateOf(false) }

    BlurredOverflowMenu(
        expanded = expanded,
        hazeState = hazeState,
        pureBlack = pureBlack,
        onDismissRequest = onDismissRequest,
        footer = {
            // - Always meticulous in everyhthing to minimize the case
            // [VoidTune Patch: Slide-In Support VoidTune Badge Above Footer]
            // MAKSUD KODE ASLI: Support VoidTune ditaruh sebagai item menu biasa di dalam daftar menu atas.
            // MENGAPA DIUBAH:
            // 1. Teks kuning polos menyatu dengan ilustrasi background anime biola di drawer.
            // 2. Dipindahkan menjadi badge curved rectangle (RoundedCornerShape(10.dp)) dengan alas Obsidian Glass
            //    dan border Radiant Amber Gold tepat di atas footer divider.
            // 3. Menggunakan animasi slide ke kiri (slideInHorizontally { it }) setelah timer 2 detik di drawer.
            // 4. Ikon menggunakan rokok berasap Lucide (R.drawable.cigarette) beraksen emas bara.
            SupportVoidTuneBadge(
                expanded = expanded,
                onClick = {
                    onDismissRequest()
                    showSupportQrDialog = true
                },
            )
        },
    ) {
        MenuProfileHeader(
            state = profileState,
            accountImageUrl = accountImageUrl,
            contentColor = contentColor,
            onClick = remember(onSelect) { { onSelect(MainMenuDestination.Profile) } },
        )
        OverflowMenuDivider(modifier = Modifier.padding(vertical = 8.dp))
        OverflowAction(
            label = stringResource(R.string.new_releases),
            iconRes = R.drawable.new_release,
            contentColor = contentColor,
            onClick = remember(onSelect) { { onSelect(MainMenuDestination.NewRelease) } },
        )
        OverflowMenuDivider()
        OverflowAction(
            label = stringResource(R.string.news),
            iconRes = R.drawable.newspaper,
            contentColor = contentColor,
            hasBadge = hasUnreadNews,
            badgeDescription = if (hasUnreadNews) stringResource(R.string.news_tooltip_title) else null,
            onClick = remember(onSelect) { { onSelect(MainMenuDestination.News) } },
        )
        OverflowMenuDivider()
        OverflowAction(
            label = stringResource(R.string.history),
            iconRes = R.drawable.history,
            contentColor = contentColor,
            onClick = remember(onSelect) { { onSelect(MainMenuDestination.History) } },
        )
        OverflowMenuDivider()
        OverflowAction(
            label = stringResource(R.string.settings),
            iconRes = R.drawable.settings,
            contentColor = contentColor,
            hasBadge = hasUpdateAvailable,
            badgeDescription = if (hasUpdateAvailable) stringResource(R.string.new_update_available) else null,
            onClick = remember(onSelect) { { onSelect(MainMenuDestination.Settings) } },
        )
    }

    if (showSupportQrDialog) {
        SupportQrModal(
            onDismiss = { showSupportQrDialog = false },
        )
    }
}

@Composable
private fun SupportVoidTuneBadge(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded) {
            isVisible = false
            delay(2000L)
            isVisible = true
        } else {
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(420, easing = FastOutSlowInEasing),
        ) + fadeIn(animationSpec = tween(380)),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(280, easing = FastOutLinearInEasing),
        ) + fadeOut(animationSpec = tween(280)),
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.92f)) // Deep Obsidian Shield
                .border(
                    width = 1.dp,
                    color = Color(0xFFF59E0B).copy(alpha = 0.55f), // Radiant Amber Gold rim
                    shape = RoundedCornerShape(10.dp),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.cigarette),
                    contentDescription = null,
                    tint = Color(0xFFF59E0B), // Radiant Amber Gold
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Support VoidTune",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp,
                    ),
                    color = Color(0xFFF8FAFC), // High-contrast crisp white
                    maxLines = 1,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF59E0B).copy(alpha = 0.16f))
                    .border(
                        width = 0.8.dp,
                        color = Color(0xFFF59E0B).copy(alpha = 0.45f),
                        shape = RoundedCornerShape(6.dp),
                    )
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "QRIS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                    ),
                    color = Color(0xFFF59E0B),
                )
            }
        }
    }
}

/*
// ORIGINAL CODE:
@Composable
private fun MenuProfileHeader(
    state: MenuProfileState,
    accountImageUrl: String?,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val profile = (state as? MenuProfileState.Success)?.profile
    val subtitle = when (state) {
        MenuProfileState.Loading -> stringResource(R.string.loading)
        MenuProfileState.Empty -> stringResource(R.string.login)
        is MenuProfileState.Error -> stringResource(state.messageResId)
        is MenuProfileState.Success -> state.profile.name.ifBlank { stringResource(R.string.account) }
    }
    val context = LocalContext.current
    val avatarSize = with(LocalDensity.current) { 48.dp.roundToPx() }
    val avatarRequest = remember(context, accountImageUrl, avatarSize, profile != null) {
        if (profile != null && !accountImageUrl.isNullOrBlank()) {
            ImageRequest.Builder(context).data(accountImageUrl).size(avatarSize).build()
        } else {
            null
        }
    }
    DropdownMenuItem(
        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
        text = {
            Column {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (profile != null && profile.email.isNotBlank()) {
                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        leadingIcon = {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.account), null, modifier = Modifier.size(28.dp))
                if (avatarRequest != null) {
                    AsyncImage(
                        model = avatarRequest,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                    )
                }
            }
        },
        trailingIcon = {
            Icon(painterResource(R.drawable.navigate_next), null, modifier = Modifier.size(20.dp))
        },
        colors = MenuDefaults.itemColors(
            textColor = contentColor,
            leadingIconColor = Color(0xFF38BDF8),
            trailingIconColor = Color(0xFFCBD5E1),
        ),
        onClick = onClick,
    )
}
*/
// [VoidTune Patch: Enhanced Personalized Profile Header with Dual Service Status Badges]
// REASON: Render custom gallery avatar, custom username, and mini YouTube/Spotify badges directly below the avatar.
@Composable
private fun MenuProfileHeader(
    state: MenuProfileState,
    accountImageUrl: String?,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val profile = (state as? MenuProfileState.Success)?.profile
    val (customName) = rememberPreference(CustomProfileNameKey, "")
    val (customAvatarPath) = rememberPreference(CustomProfileAvatarPathKey, "")
    val (spotifySpDc) = rememberPreference(SpotifySpDcKey, "")
    val (spotifyName) = rememberPreference(SpotifyAccountNameKey, "")
    val (ytName) = rememberPreference(AccountNameKey, "")

    val isSpotifyConnected = spotifySpDc.isNotBlank()
    val isYouTubeConnected = profile != null || ytName.isNotBlank()

    /*
    // PREVIOUS DISPLAY NAME FALLBACK CODE:
    val displayName = when {
        customName.isNotBlank() -> customName
        profile != null && profile.name.isNotBlank() -> profile.name
        isSpotifyConnected && spotifyName.isNotBlank() -> spotifyName
        else -> stringResource(R.string.account)
    }
    */
    // [VoidTune Patch: Sovereign Local Profile Identity in Drawer]
    // REASON: YouTube Music and Spotify are strictly connected services, not the drawer user identity.
    // Display name and avatar ONLY use custom local settings (customName and customAvatarPath),
    // never leaking Google/YouTube account name or Google profile picture into the drawer.
    val displayName = when {
        customName.isNotBlank() -> customName
        else -> stringResource(R.string.account)
    }

    val displaySubtitle = when {
        isSpotifyConnected && isYouTubeConnected -> "Spotify & YouTube Linked"
        isSpotifyConnected -> "Spotify Connected"
        isYouTubeConnected -> "YouTube Connected"
        else -> "Tap to customize profile"
    }

    DropdownMenuItem(
        modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp),
        text = {
            Column(modifier = Modifier.padding(start = 6.dp)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = displaySubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        leadingIcon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Slightly enlarged avatar (~54dp)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.08f))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    /*
                    // PREVIOUS GOOGLE ACCOUNT AVATAR FALLBACK:
                    if (customAvatarPath.isNotBlank() && File(customAvatarPath).exists()) {
                        AsyncImage(
                            model = File(customAvatarPath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(54.dp).clip(CircleShape),
                        )
                    } else if (!accountImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = accountImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(54.dp).clip(CircleShape),
                        )
                    } else {
                        Icon(painterResource(R.drawable.account), null, tint = contentColor, modifier = Modifier.size(28.dp))
                    }
                    */
                    // [VoidTune Patch: Sovereign Local Avatar in Drawer]
                    // REASON: Only display user's custom gallery avatar if chosen; otherwise default to VoidTune account icon. Never show Google/YouTube photo.
                    if (customAvatarPath.isNotBlank() && File(customAvatarPath).exists()) {
                        AsyncImage(
                            model = File(customAvatarPath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(54.dp).clip(CircleShape),
                        )
                    } else {
                        Icon(painterResource(R.drawable.account), null, tint = contentColor, modifier = Modifier.size(28.dp))
                    }
                }

                // Mini service badges right below the avatar
                if (isSpotifyConnected || isYouTubeConnected) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        /*
                        // PREVIOUS BADGE IMPLEMENTATION:
                        if (isYouTubeConnected) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.youtube),
                                    contentDescription = "YouTube",
                                    tint = Color.White,
                                    modifier = Modifier.size(9.dp),
                                )
                            }
                        }
                        if (isSpotifyConnected) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1DB954)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.spotify),
                                    contentDescription = "Spotify",
                                    tint = Color.White,
                                    modifier = Modifier.size(9.dp),
                                )
                            }
                        }
                        */
                        // [VoidTune Patch: Direct Vector Service Badges]
                        // REASON: Render brand vector icons directly in their official colors (YouTube #EF4444 and Spotify #1DB954) for crisp, clean appearance.
                        if (isYouTubeConnected) {
                            Icon(
                                painter = painterResource(R.drawable.youtube),
                                contentDescription = "YouTube",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        if (isSpotifyConnected) {
                            Icon(
                                painter = painterResource(R.drawable.spotify),
                                contentDescription = "Spotify",
                                tint = Color(0xFF1DB954),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
        },
        trailingIcon = {
            Icon(painterResource(R.drawable.navigate_next), null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
        },
        colors = MenuDefaults.itemColors(
            textColor = contentColor,
            leadingIconColor = Color(0xFF38BDF8),
            trailingIconColor = Color(0xFFCBD5E1),
        ),
        onClick = onClick,
    )
}

@Composable
private fun OverflowAction(
    label: String,
    @DrawableRes iconRes: Int,
    contentColor: Color,
    onClick: () -> Unit,
    hasBadge: Boolean = false,
    badgeDescription: String? = null,
) {
    DropdownMenuItem(
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).semantics {
            if (badgeDescription != null) stateDescription = badgeDescription
        },
        text = { Text(label, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        leadingIcon = {
            BadgedBox(badge = { if (hasBadge) Badge() }) {
                Icon(painterResource(iconRes), null, modifier = Modifier.size(24.dp))
            }
        },
        colors = MenuDefaults.itemColors(textColor = contentColor, leadingIconColor = Color(0xFF38BDF8)),
        onClick = onClick,
    )
}
