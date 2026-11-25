/*
 * VoidTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.rukamori.archivetune.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * VoidTune Luxury Animated Splash Screen (v0.0.2).
 *
 * Implements the NOTEZ Master Design System ("Minimalist, Glamour & Luxury, yet Humble & Sahaja").
 * Rich procedural animation features:
 * - Pure AMOLED Black (#000000) backdrop
 * - Dynamic Canvas Soundwave Horizon Ripples expanding outward into the void
 * - Orbital Starlight Celestial Ring rotating around the emblem
 * - Floating Stardust Micro-particles drifting upward
 * - Central Monolith V & Accretion Waveform Emblem with smooth entrance & breathing scale
 * - Ethereal Lottie Light Burst Overlay
 * - Glowing Futuristic Typography ("VOIDTUNE • RESONANT SOUND HORIZON")
 * - Instant tap-to-skip and safety auto-dismiss
 */
@Composable
fun VoidTuneSplashScreen(
    visible: Boolean,
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(150)),
        exit = fadeOut(tween(400)),
        modifier = modifier,
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.voidtune_luxury_light_burst),
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            isPlaying = true,
            speed = 1.0f,
        )

        // Safety timeout: ensure transition completes even if device has animation restrictions
        LaunchedEffect(Unit) {
            delay(2400)
            onAnimationFinished()
        }

        LaunchedEffect(progress) {
            if (progress >= 1.0f) {
                onAnimationFinished()
            }
        }

        // Entrance animation states
        val emblemAlpha = remember { Animatable(0f) }
        val emblemScale = remember { Animatable(0.78f) }
        val textAlpha = remember { Animatable(0f) }
        val textSlideY = remember { Animatable(18f) }

        LaunchedEffect(Unit) {
            launch {
                emblemAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            }
            launch {
                emblemScale.animateTo(1f, tween(750, easing = FastOutSlowInEasing))
            }
            launch {
                delay(350)
                textAlpha.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
            }
            launch {
                delay(350)
                textSlideY.animateTo(0f, tween(550, easing = FastOutSlowInEasing))
            }
        }

        // Continuous luxury ambient animations
        val infiniteTransition = rememberInfiniteTransition(label = "VoidTuneSplashInfinite")

        val breathScale by infiniteTransition.animateFloat(
            initialValue = 0.985f,
            targetValue = 1.015f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "breathScale",
        )

        val soundRipple by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "soundRipple",
        )

        val orbitalAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(14000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "orbitalAngle",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onAnimationFinished()
                },
            contentAlignment = Alignment.Center,
        ) {
            // 1. Dynamic Canvas Ambient Layer: Soundwave Horizon Ripples & Orbit Ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f - 30.dp.toPx()

                // Radial soundwave ripple 1
                val baseRadius = 140.dp.toPx()
                val ripple1Radius = baseRadius + soundRipple * 90.dp.toPx()
                val ripple1Alpha = (1f - soundRipple) * 0.35f
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = ripple1Alpha),
                    radius = ripple1Radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx()),
                )

                // - Always meticulous in everyhthing to minimize the case
                // [VoidTune Patch: Purge Purple/Violet in Splash Particles - User Rule: NO ANYTHING PURPLE!]
                // MAKSUD KODE ASLI:
                // Radial soundwave ripple 2 menggunakan Color(0xFFA855F7), sweepGradient menggunakan 0xFFA855F7,
                // dan satellite dot 2 menggunakan Color(0xFFA855F7).
                // MENGAPA DIKOMEN:
                // User memberikan instruksi tegas: "NO ANYTHING PURPLE!"
                // Warna violet/purple 0xFFA855F7 diganti dengan Frost Teal (0xFF88C0D0), Arctic Blue (0xFF81A1C1),
                // dan Radiant Amber Gold (0xFFF59E0B) dari palet Nordic Aurora.
                // ORIGINAL CODE:
                // val phase2 = (soundRipple + 0.5f) % 1f
                // val ripple2Radius = baseRadius + phase2 * 90.dp.toPx()
                // val ripple2Alpha = (1f - phase2) * 0.25f
                // drawCircle(
                //     color = Color(0xFFA855F7).copy(alpha = ripple2Alpha),
                //     radius = ripple2Radius,
                //     center = Offset(cx, cy),
                //     style = Stroke(width = 1.2.dp.toPx()),
                // )
                // drawCircle(
                //     brush = Brush.sweepGradient(
                //         0f to Color(0xFF38BDF8).copy(alpha = 0.25f),
                //         0.5f to Color(0xFFA855F7).copy(alpha = 0.35f),
                //         1f to Color(0xFF38BDF8).copy(alpha = 0.25f),
                //         center = Offset(cx, cy),
                //     ),
                //     radius = orbitRadius,
                //     center = Offset(cx, cy),
                //     style = Stroke(width = 1.dp.toPx()),
                // )
                // val rad2 = ((orbitalAngle + 180f) * PI / 180.0).toFloat()
                // val dot2X = cx + orbitRadius * cos(rad2)
                // val dot2Y = cy + orbitRadius * sin(rad2)
                // drawCircle(
                //     color = Color(0xFFA855F7),
                //     radius = 2.5.dp.toPx(),
                //     center = Offset(dot2X, dot2Y),
                // )

                // Radial soundwave ripple 2 (phase offset: Frost Teal, Zero Purple)
                val phase2 = (soundRipple + 0.5f) % 1f
                val ripple2Radius = baseRadius + phase2 * 90.dp.toPx()
                val ripple2Alpha = (1f - phase2) * 0.25f
                drawCircle(
                    color = Color(0xFF88C0D0).copy(alpha = ripple2Alpha),
                    radius = ripple2Radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.2.dp.toPx()),
                )

                // Orbital Celestial Ring with 2 orbiting audio quantum dots (Nord Polar Cyan & Arctic Blue, Zero Purple)
                val orbitRadius = 155.dp.toPx()
                drawCircle(
                    brush = Brush.sweepGradient(
                        0f to Color(0xFF38BDF8).copy(alpha = 0.25f),
                        0.5f to Color(0xFF81A1C1).copy(alpha = 0.35f),
                        1f to Color(0xFF38BDF8).copy(alpha = 0.25f),
                        center = Offset(cx, cy),
                    ),
                    radius = orbitRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx()),
                )

                // Orbiting satellite dot 1 (Polar Cyan)
                val rad1 = (orbitalAngle * PI / 180.0).toFloat()
                val dot1X = cx + orbitRadius * cos(rad1)
                val dot1Y = cy + orbitRadius * sin(rad1)
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = 3.dp.toPx(),
                    center = Offset(dot1X, dot1Y),
                )
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                    radius = 6.dp.toPx(),
                    center = Offset(dot1X, dot1Y),
                )

                // Orbiting satellite dot 2 (Radiant Amber Gold, Zero Purple)
                val rad2 = ((orbitalAngle + 180f) * PI / 180.0).toFloat()
                val dot2X = cx + orbitRadius * cos(rad2)
                val dot2Y = cy + orbitRadius * sin(rad2)
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 2.5.dp.toPx(),
                    center = Offset(dot2X, dot2Y),
                )
            }

            // 2. Main Centered Content Column: Emblem + Lottie + Futuristic Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-30).dp),
            ) {
                // Emblem & Lottie Container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(310.dp)
                        .graphicsLayer {
                            alpha = emblemAlpha.value
                            scaleX = emblemScale.value * breathScale
                            scaleY = emblemScale.value * breathScale
                        },
                ) {
                    // Procedural High-Fidelity VoidTune Splash Artwork
                    Image(
                        painter = painterResource(R.drawable.voidtune_splash_seal),
                        contentDescription = "VoidTune",
                        modifier = Modifier.size(290.dp),
                        contentScale = ContentScale.Fit,
                    )

                    // Lottie Ethereal Light Burst Overlay
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(360.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Staggered Animated Typography
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        alpha = textAlpha.value
                        translationY = textSlideY.value
                    },
                ) {
                    Text(
                        text = "V O I D T U N E",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 6.sp,
                        fontFamily = FontFamily.SansSerif,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "R E S O N A N T   S O U N D   H O R I Z O N",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 3.sp,
                        fontFamily = FontFamily.SansSerif,
                    )
                }
            }
        }
    }
}
