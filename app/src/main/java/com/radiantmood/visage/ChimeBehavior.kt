package com.radiantmood.visage

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import java.util.Calendar

class ChimeBehavior(val context: Context) {
    private val chimeTarget: Calendar = Calendar.getInstance().apply {
        val min = get(Calendar.MINUTE)
        if (min > 30) {
            // go forward an hour
            timeInMillis += HOUR_IN_MILLIS
            // set min to 0
            set(Calendar.MINUTE, 0)
        } else {
            set(Calendar.MINUTE, 30)
        }
    }

    private val vibrator by lazy { getSystemService(context, Vibrator::class.java) }

    fun chimeIfNeeded(current: Calendar) {
        if (current.after(chimeTarget)) {
            chimeTarget.timeInMillis += HALF_HOUR_IN_MILLIS
            vibrate()
        }
    }

    fun vibrate() {
        Log.d("araiff", "vibrating")
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
    }

    companion object {
        private const val HOUR_IN_MILLIS = 1000 * 60 * 60
        private const val HALF_HOUR_IN_MILLIS = HOUR_IN_MILLIS / 2
    }
}