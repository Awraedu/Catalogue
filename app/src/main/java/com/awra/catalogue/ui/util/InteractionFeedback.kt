package com.awra.catalogue.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View

object InteractionFeedback {

    private fun triggerVibration(context: Context, milliseconds: Long = 25L) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(milliseconds)
                }
            }
        } catch (_: Exception) {}
    }

    fun playClick(view: View) {
        try {
            view.isHapticFeedbackEnabled = true
            view.playSoundEffect(SoundEffectConstants.CLICK)
            view.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            triggerVibration(view.context, 20L)
        } catch (_: Exception) {}
    }

    fun playKey(view: View) {
        try {
            view.isHapticFeedbackEnabled = true
            view.playSoundEffect(SoundEffectConstants.CLICK)
            view.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            triggerVibration(view.context, 25L)
        } catch (_: Exception) {}
    }

    fun playConfirm(view: View) {
        try {
            view.isHapticFeedbackEnabled = true
            view.playSoundEffect(SoundEffectConstants.CLICK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(
                    HapticFeedbackConstants.CONFIRM,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            } else {
                view.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }
            triggerVibration(view.context, 35L)
        } catch (_: Exception) {}
    }

    fun playReject(view: View) {
        try {
            view.isHapticFeedbackEnabled = true
            view.playSoundEffect(SoundEffectConstants.CLICK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(
                    HapticFeedbackConstants.REJECT,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            } else {
                view.performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }
            triggerVibration(view.context, 45L)
        } catch (_: Exception) {}
    }
}
