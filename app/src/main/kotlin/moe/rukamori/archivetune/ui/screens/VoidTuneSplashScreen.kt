/*
 * VoidTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import moe.rukamori.archivetune.R

/**
 * VoidTune Luxury Animated Splash Screen.
 *
 * Implements the NOTEZ Master Design System ("Minimalist, Glamour & Luxury, yet Humble & Sahaja").
 * Features:
 * - Pure AMOLED Black (#000000) backdrop
 * - Feathered VoidTune Arcane Cosmic Rune Seal
 * - Lottie Ethereal Light Burst ("Cahaya tipis yang meledak") overlay
 * - Graceful tap-to-skip and automatic safety timeout
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
            LottieCompositionSpec.RawRes(R.raw.voidtune_luxury_light_burst)
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            isPlaying = true,
            speed = 1.0f,
        )

        // Safety timeout: ensure transition completes even if device has animation restrictions
        LaunchedEffect(Unit) {
            delay(2200)
            onAnimationFinished()
        }

        LaunchedEffect(progress) {
            if (progress >= 1.0f) {
                onAnimationFinished()
            }
        }

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
            // Feathered VoidTune Cosmic Seal with central cyan musical note & typography
            Image(
                painter = painterResource(R.drawable.voidtune_splash_seal),
                contentDescription = "VoidTune",
                modifier = Modifier.size(320.dp),
                contentScale = ContentScale.Fit,
            )

            // Lottie Luxury Light Burst Overlay (delicate starlight flare, shockwave ring, radiant micro-rays)
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(380.dp),
            )
        }
    }
}
