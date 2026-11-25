// Always meticulous in everyhthing to minimize the case
// Be honest in everything
// Be meticulous in everything
// Dont replace or delete code, coment and explain what and why, the write your change below
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.rukamori.archivetune.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * VoidTune Luxury Animated Splash Screen (v0.0.3).
 *
 * Implements the NOTEZ Master Design System ("Minimalist, Glamour & Luxury, yet Humble & Sahaja").
 * Rich procedural animation features:
 * - Pure AMOLED Black (#000000) backdrop
 * - Dynamic Canvas Soundwave Horizon Ripples expanding outward into the void
 * - Orbital Starlight Celestial Ring rotating around the emblem (Zero Purple)
 * - Central Monolith V & Accretion Waveform Emblem with smooth entrance & breathing scale
 * - Signature Gothic UnifrakturCook 'VoidTune' title sliding in smoothly to the left
 * - Glowing Futuristic Typography ("RESONANT SOUND HORIZON")
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
        /*
        // PREVIOUS LOTTIE OVERLAY IMPLEMENTATION:
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.voidtune_luxury_light_burst),
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            isPlaying = true,
            speed = 1.0f,
        )
        */
        // [VoidTune Patch: Authentic Gothic UnifrakturCook Typography, Slide-to-Left Entrance]
        // REASON: Replace heavy Lottie light burst with signature UnifrakturCook VoidTune title sliding smoothly to the left under the emblem.
        val unifrakturCookFont = remember { FontFamily(Font(R.font.unifrakturcook)) }

        // Safety timeout: ensure transition completes even if device has animation restrictions
        LaunchedEffect(Unit) {
            delay(2200)
            onAnimationFinished()
        }

        // Entrance animation states
        val emblemAlpha = remember { Animatable(0f) }
        val emblemScale = remember { Animatable(0.78f) }
        val titleAlpha = remember { Animatable(0f) }
        val titleSlideX = remember { Animatable(48f) } // Slide from right to left (+48dp -> 0dp)
        val textAlpha = remember { Animatable(0f) }
        val textSlideY = remember { Animatable(14f) }

        LaunchedEffect(Unit) {
            launch {
                emblemAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            }
            launch {
                emblemScale.animateTo(1f, tween(750, easing = FastOutSlowInEasing))
            }
            launch {
                delay(200)
                titleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            }
            launch {
                delay(200)
                titleSlideX.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
            }
            launch {
                delay(400)
                textAlpha.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
            }
            launch {
                delay(400)
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
                val cy = size.height / 2f - 40.dp.toPx()

                // Radial soundwave ripple 1
                val baseRadius = 130.dp.toPx()
                val ripple1Radius = baseRadius + soundRipple * 85.dp.toPx()
                val ripple1Alpha = (1f - soundRipple) * 0.35f
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = ripple1Alpha),
                    radius = ripple1Radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx()),
                )

                // Radial soundwave ripple 2 (phase offset: Frost Teal, Zero Purple)
                val phase2 = (soundRipple + 0.5f) % 1f
                val ripple2Radius = baseRadius + phase2 * 85.dp.toPx()
                val ripple2Alpha = (1f - phase2) * 0.25f
                drawCircle(
                    color = Color(0xFF88C0D0).copy(alpha = ripple2Alpha),
                    radius = ripple2Radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.2.dp.toPx()),
                )

                // Orbital Celestial Ring with 2 orbiting audio quantum dots (Nord Polar Cyan & Arctic Blue, Zero Purple)
                val orbitRadius = 145.dp.toPx()
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

            // 2. Main Centered Content Column: Monolith V Emblem + UnifrakturCook Title + Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-40).dp),
            ) {
                // Emblem Container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(220.dp)
                        .graphicsLayer {
                            alpha = emblemAlpha.value
                            scaleX = emblemScale.value * breathScale
                            scaleY = emblemScale.value * breathScale
                        },
                ) {
                    // Procedural Pure Transparent Monolith V
                    Image(
                        painter = painterResource(R.drawable.voidtune_splash_seal),
                        contentDescription = "VoidTune",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit,
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Signature Gothic VoidTune Title (UnifrakturCook, slide to left)
                Text(
                    text = "VoidTune",
                    fontFamily = unifrakturCookFont,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.graphicsLayer {
                        alpha = titleAlpha.value
                        translationX = titleSlideX.value
                    },
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 4. Clean Resonant Sound Horizon Subtitle
                Text(
                    text = "R E S O N A N T   S O U N D   H O R I Z O N",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.graphicsLayer {
                        alpha = textAlpha.value
                        translationY = textSlideY.value
                    },
                )
            }
        }
    }
}
