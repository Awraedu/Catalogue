package com.awra.catalogue.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.awra.catalogue.R
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onExitProgress: (Float) -> Unit = {},
    onAnimationFinished: () -> Unit
) {
    val currentOnFinished by rememberUpdatedState(onAnimationFinished)
    val currentOnExitProgress by rememberUpdatedState(onExitProgress)

    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.welcome))
    val composition by compositionResult

    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.0f,
        isPlaying = true
    )

    var isLaunched by remember { mutableStateOf(false) }
    var startFadeOut by remember { mutableStateOf(false) }
    var hasFinishedCallbackFired by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLaunched = true
        // Fallback timer (5s) in case Lottie fails to report completion
        delay(5000L)
        if (!startFadeOut) {
            startFadeOut = true
        }
    }

    // Trigger exit transition after Lottie completes its single iteration
    LaunchedEffect(composition, lottieState.isAtEnd, lottieState.progress, compositionResult.isFailure) {
        if (compositionResult.isFailure) {
            startFadeOut = true
        } else if (composition != null && (lottieState.isAtEnd || lottieState.progress >= 0.999f)) {
            delay(150L) // Small resting pause on the finished frame
            if (!startFadeOut) {
                startFadeOut = true
            }
        }
    }

    val enterAnim by animateFloatAsState(
        targetValue = if (isLaunched) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "splash_enter"
    )

    // Smooth exit crossfade
    val exitAnim by animateFloatAsState(
        targetValue = if (startFadeOut) 1f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        finishedListener = {
            if (startFadeOut && !hasFinishedCallbackFired) {
                hasFinishedCallbackFired = true
                currentOnFinished()
            }
        },
        label = "splash_exit"
    )

    SideEffect {
        currentOnExitProgress(exitAnim)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Intercept touches while splash is visible
            .pointerInput(Unit) {}
            .graphicsLayer {
                // Crossfade out the splash screen seamlessly into the home screen
                alpha = (1f - exitAnim).coerceIn(0f, 1f)
            },
        contentAlignment = Alignment.Center
    ) {
        // Exact ambient mesh background used by the home screen
        AmbientMeshBackground(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                val scale = if (startFadeOut) {
                    1f + (0.05f * exitAnim) // Subtle, premium expansion on exit
                } else {
                    0.92f + (0.08f * enterAnim)
                }
                scaleX = scale
                scaleY = scale
                alpha = if (startFadeOut) (1f - exitAnim) else enterAnim
            }
        ) {
            LottieAnimation(
                composition = composition,
                progress = { lottieState.progress },
                modifier = Modifier.size(240.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Catalogue",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}
