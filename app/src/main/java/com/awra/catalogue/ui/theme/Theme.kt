package com.awra.catalogue.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YourAppTheme(
    darkMode: String = "SYSTEM",
    selectedTheme: String = "DEFAULT",
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDark = when (darkMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
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
            // Set status bar to transparent to allow the app background to flow through
            // with enableEdgeToEdge in MainActivity.
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
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
