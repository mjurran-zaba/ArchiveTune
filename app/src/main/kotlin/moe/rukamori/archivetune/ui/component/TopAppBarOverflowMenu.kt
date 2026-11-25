// Always meticulous in everyhthing to minimize the case
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
