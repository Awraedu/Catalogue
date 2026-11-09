package com.awra.catalogue.ui.theme

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YourAppTheme(
    darkMode: String = "LIGHT",
    selectedTheme: String = "VIBRANT_ORANGE",
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDark = when (darkMode) {
        "DARK" -> true
        else -> false
    }
    
    // Using Material 3 Expressive Color Schemes
    val colorScheme = when {
        selectedTheme != "DEFAULT" && CustomColorSchemes.Schemes.containsKey(selectedTheme) -> {
            CustomColorSchemes.getScheme(selectedTheme, isDark)!!
        }
        // dynamicColor is disabled when a custom theme is selected, or we can logic it out
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> darkColorScheme() // Fallback dark mode
        else -> expressiveLightColorScheme() // Expressive light mode
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Remove any background color or contrast scrims from status bar & navigation bar
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isStatusBarContrastEnforced = false
                window.isNavigationBarContrastEnforced = false
            }
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
