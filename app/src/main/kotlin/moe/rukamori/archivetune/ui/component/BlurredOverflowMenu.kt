// Always meticulous in everyhthing to minimize the case
/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package moe.rukamori.archivetune.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import moe.rukamori.archivetune.R

@Composable
fun BlurredOverflowMenu(
    expanded: Boolean,
    hazeState: HazeState,
    pureBlack: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    // VoidTune UI Redesign: Slowly smooth physics-based Slide-In Navigation Drawer
    // Original ArchiveTune dropdown popup code is preserved in comments below.
    /*
    val surfaceColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.onSurface
    val shape = MaterialTheme.shapes.large
    val hazeInput = remember(hazeState) { HazeInput.Sources(hazeState) }
    val blurStyle = remember(surfaceColor) {
        HazeBlurStyle {
            backgroundColor(surfaceColor)
            blurRadius(24.dp)
            noiseFactor(0.12f)
        }
    }
    val menuTitle = stringResource(R.string.more_options)
    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = modifier
                .width(264.dp)
                .shadow(6.dp, shape)
                .clip(shape)
                .hazeBlur(input = hazeInput, style = blurStyle)
                .background(surfaceColor.copy(alpha = 0.72f))
                .border(1.dp, contentColor.copy(alpha = 0.08f), shape)
                .semantics { paneTitle = menuTitle }
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            content = content,
        )
    }
    */

    val transitionState = remember { MutableTransitionState(false) }
    LaunchedEffect(expanded) {
        transitionState.targetState = expanded
    }

    if (!transitionState.currentState && !transitionState.targetState) {
        return
    }

    val surfaceColor = if (pureBlack) Color(0xFF000000) else Color(0xFF0D1117)
    val contentColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.onSurface
    val shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    val unifrakturCookFont = remember { FontFamily(Font(R.font.unifrakturcook)) }

    BackHandler(enabled = expanded) {
        onDismissRequest()
    }

    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(280, easing = FastOutLinearInEasing)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.60f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismissRequest,
                    ),
            ) {
                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(420, easing = FastOutSlowInEasing),
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(320, easing = FastOutLinearInEasing),
                    ),
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    // - Always meticulous in everyhthing to minimize the case
                    // [VoidTune Patch: Violin Anime Girl Drawer Background, Zero Milky Glassmorphism Scrim, Enlarged Title, and Elevated Contrast]
                    // MAKSUD KODE ASLI:
                    // Menggunakan Column berlatar belakang solid surfaceColor alpha 0.94f dengan lapisan putih susu milky alpha 0.16f,
                    // dan menampilkan logo kotak 62dp di sebelah kiri judul VoidTune.
                    // MENGAPA DIKOMEN:
                    // 1. User meminta foto gadis pemain biola (drawer_bg.png) dipasang sebagai background drawer dengan kontras tinggi agar tidak pudar.
                    // 2. Lapisan putih susu glassmorphism (alpha 0.16f) membuat gambar buram dan kusam, sehingga diganti dengan Box + scrim gradasi gelap halus.
                    // 3. Logo di kiri header dihapus agar tampilan lebih lapang, judul 'VoidTune' diperbesar (32sp), dan digabung langsung dengan bar login di header.
                    // 4. Semua teks dan ikon dipatenkan memakai warna kontras tinggi (#F8FAFC & Polar Cyan) agar saat Light Mode tidak hilang/gelap.
                    /*
                    Column(
                        modifier = modifier
                            .fillMaxHeight()
                            .width(300.dp)
                            .clip(shape)
                            .background(surfaceColor.copy(alpha = 0.94f))
                            .background(Color.White.copy(alpha = 0.16f)) // VoidTune: 35% faded white milk glassmorphism
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.25f), // Specular hairline rim
                                shape = shape,
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}, // Absorb click inside drawer
                            )
                            .windowInsetsPadding(WindowInsets.systemBars)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 18.dp, horizontal = 8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = 1.2.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(14.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_voidtune_seal),
                                    contentDescription = "VoidTune Galactic Emblem",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                )
                            }
                            Column {
                                Text(
                                    text = "VoidTune",
                                    fontFamily = unifrakturCookFont,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 28.sp,
                                        letterSpacing = 1.2.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = MaterialTheme.colorScheme.primary, // Dynamically follows active theme color
                                )
                                Text(
                                    text = "MJURRAN ARC",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontStyle = FontStyle.Italic,
                                        letterSpacing = 1.8.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 1.dp),
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            thickness = 0.5.dp,
                            color = Color.White.copy(alpha = 0.14f),
                        )

                        content()
                    */
                    Box(
                        modifier = modifier
                            .fillMaxHeight()
                            .width(310.dp)
                            .clip(shape)
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.25f),
                                shape = shape,
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}, // Absorb click inside drawer
                            ),
                    ) {
                        // Background Drawer: High-Contrast Violin Anime Girl
                        Image(
                            painter = painterResource(R.drawable.drawer_bg),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )

                        // Subtle dark gradient scrim: preserves illustration vibrancy while ensuring crisp text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.48f),
                                            Color.Black.copy(alpha = 0.22f),
                                            Color.Black.copy(alpha = 0.65f),
                                        ),
                                    ),
                                ),
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.systemBars)
                                .padding(vertical = 18.dp, horizontal = 8.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                            ) {
                                // VoidTune Side Drawer Brand Header: Clean Gothic Typography (No Left Logo)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                ) {
                                    Text(
                                        text = "VoidTune",
                                        fontFamily = unifrakturCookFont,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontSize = 32.sp,
                                            letterSpacing = 1.4.sp,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Text(
                                        text = "MJURRAN ARC",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontStyle = FontStyle.Italic,
                                            letterSpacing = 2.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(top = 1.dp),
                                    )
                                }

                                content()
                            }

                            // Anchored Footer Section: Slide-In Support VoidTune Badge + Divider + Developer Info
                            footer?.invoke()

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                thickness = 0.5.dp,
                                color = Color.White.copy(alpha = 0.18f),
                            )

                            // VoidTune Drawer Footer (Crisp, High-Contrast White)
                            Text(
                                text = "Developer • Mjurran | INDONESIA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 0.8.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverflowMenuDivider(modifier: Modifier = Modifier) {
    // - Always meticulous in everyhthing to minimize the case
    // [VoidTune Patch: Crisp Translucent White Divider on Artwork Background]
    // ORIGINAL CODE:
    /*
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    )
    */
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 0.8.dp,
        color = Color.White.copy(alpha = 0.20f),
    )
}
