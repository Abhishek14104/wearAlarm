package com.example.wearalarm.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        var ringtone: Ringtone? = null
        var vibrator: Vibrator? = null
    }

    override fun onReceive(context: Context, intent: Intent?) {
        // ✅ Start the full-screen pop-up (AlarmActivity)
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(alarmIntent)

        // ✅ Start Vibration
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0) // Infinite loop
            )
        } else {
            vibrator?.vibrate(longArrayOf(0, 500, 500), 0)
        }

        // ✅ Play Alarm Sound
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, alarmUri)

        ringtone?.let {
            if (!it.isPlaying) {
                it.play()
            }
        }
    }

    // ✅ Call this when dismissing the alarm
    fun stopAlarm() {
        ringtone?.stop()
        vibrator?.cancel()
        ringtone = null
        vibrator = null
    }
}
