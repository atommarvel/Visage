package com.radiantmood.visage

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

class ChimeBehavior(val context: Context) {

    private val vibrator by lazy { getSystemService(context, Vibrator::class.java) }
    private val am by lazy { getSystemService(context, AlarmManager::class.java) }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onAlarm()
        }
    }

    init {
        setupNextAlarm()
        context.registerReceiver(receiver, IntentFilter("com.radiantmood.HOURLY_CHIME"))
        vibrate()
    }

    private fun onAlarm() {
        chime()
        setupNextAlarm()
    }

    private fun chime() {
        vibrate()
    }

    private fun setupNextAlarm() {
        val nextChime = findNextMinChime()
        val ambientStateIntent = Intent("com.radiantmood.HOURLY_CHIME")
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

    private fun findNextMinChime(): Calendar = Calendar.getInstance().apply {
        timeInMillis += 1000 * 60
    }

    private fun vibrate() {
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(500L, 100L, 200L, 100L, 200L), intArrayOf(255, 0, 255, 0, 255), -1))
    }

    companion object {
        private const val HOUR_IN_MILLIS = 1000 * 60 * 60
        private const val HALF_HOUR_IN_MILLIS = HOUR_IN_MILLIS / 2
    }
}