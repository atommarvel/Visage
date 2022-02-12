package com.example.android.wearable.alpha

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import java.util.Calendar

/**
 * Inspired by this SO (and Calarm): https://stackoverflow.com/questions/34397315/android-wear-watch-face-vibrate-with-screen-off
 *
 */
class ChimeBehavior(private val context: Context) {

    private val vibrator by lazy { getSystemService(context, Vibrator::class.java) }
    private val am by lazy { getSystemService(context, AlarmManager::class.java) }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onAlarm()
        }
    }

    init {
        context.registerReceiver(receiver, IntentFilter("com.radiantmood.CHIME"))
        setupNextAlarm()
    }

    fun onDestroy() {
        context.unregisterReceiver(receiver)
    }

    private fun onAlarm() {
        vibrate()
        setupNextAlarm()
    }

    private fun setupNextAlarm() {
        val nextChime = findNextChime()
        val ambientStateIntent = Intent("com.radiantmood.CHIME")
        val pIntentFlags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pIntent = PendingIntent.getBroadcast(
            context,
            1234,
            ambientStateIntent,
            pIntentFlags
        )
        val info = AlarmManager.AlarmClockInfo(nextChime.timeInMillis, pIntent)
        am?.setAlarmClock(info, pIntent)
    }

    private fun findNextChime(): Calendar = Calendar.getInstance().apply {
        // Don't chime from 9pm to 10am
        val hour = get(Calendar.HOUR_OF_DAY)
        @Suppress("ConvertTwoComparisonsToRangeCheck") // performance
        if (hour >= 21) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        if (hour >= 21 || hour < 10) {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        } else {
            val min = get(Calendar.MINUTE)
            if (min >= 30) {
                // go forward an hour
                timeInMillis += HOUR_IN_MILLIS
                // set min to 0
                set(Calendar.MINUTE, 0)
            } else {
                set(Calendar.MINUTE, 30)
            }
        }
    }

    private fun vibrate() {
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(500L, 100L, 200L, 100L, 200L), intArrayOf(255, 0, 255, 0, 255), -1))
    }

    companion object {
        private const val HOUR_IN_MILLIS = 1000 * 60 * 60
    }
}