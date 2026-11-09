package com.awra.catalogue.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.*
import com.awra.catalogue.R
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onAnimationFinished)

    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.welcome))
    val composition by compositionResult

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.25f,
        isPlaying = true
    )

    // Tint strokes/fills white without overriding layer OPACITY (which breaks internal Lottie masks/fades)
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = android.graphics.Color.WHITE,
            keyPath = arrayOf("**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = android.graphics.Color.WHITE,
            keyPath = arrayOf("**")
        )
    )

    var isLaunched by remember { mutableStateOf(false) }
    var startFadeOut by remember { mutableStateOf(false) }
    var hasFinishedCallbackFired by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLaunched = true
        // Safety fallback timer in case Lottie resource fails or takes too long
        delay(2400L)
        if (!startFadeOut) {
            startFadeOut = true
        }
    }

    // Trigger exit as soon as Lottie reaches 95% or fails to load
    LaunchedEffect(composition != null, progress >= 0.95f, compositionResult.isFailure) {
        if ((composition != null && progress >= 0.95f) || compositionResult.isFailure) {
            if (!startFadeOut) {
                startFadeOut = true
            }
        }
    }

    val enterAnim by animateFloatAsState(
        targetValue = if (isLaunched) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "splash_enter"
    )

    val slideOutAnim by animateFloatAsState(
        targetValue = if (startFadeOut) 1f else 0f,
        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
        finishedListener = {
            if (startFadeOut && !hasFinishedCallbackFired) {
                hasFinishedCallbackFired = true
                currentOnFinished()
            }
        },
        label = "splash_exit"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Consume touches while splash is visible so taps don't leak through to Home screen
            .pointerInput(Unit) {}
            .graphicsLayer {
                translationY = -slideOutAnim * size.height * 0.35f // Smoothly lift up while fading out
                alpha = (1f - slideOutAnim).coerceIn(0f, 1f)
            },
        contentAlignment = Alignment.Center
    ) {
        AmbientMeshBackground(
            modifier = Modifier.fillMaxSize(),
            animationProgress = 1f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                val currentEnter = if (startFadeOut) 1f else enterAnim
                alpha = currentEnter
                val scale = 0.92f + (0.08f * currentEnter)
                scaleX = scale
                scaleY = scale
            }
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(240.dp),
                dynamicProperties = dynamicProperties
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Catalogue",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}