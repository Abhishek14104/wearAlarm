package com.example.wearalarm.presentation

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wearalarm.R

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        var ringtone: Ringtone? = null
        var vibrator: Vibrator? = null
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val alarmId = intent?.getIntExtra("alarm_id", -1) ?: -1
        val alarmTime = intent?.getStringExtra("alarm_time") ?: "Unknown Time"

        if (intent?.action == "DISMISS_ALARM") {
            stopAlarm()
            NotificationManagerCompat.from(context).cancel(alarmId)
            return
        }

        playAlarmSoundAndVibrate(context)

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "DISMISS_ALARM"
            putExtra("alarm_id", alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, alarmId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.splash_icon)
            .setContentTitle("Alarm at $alarmTime")
            .setContentText("Tap to dismiss")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(null, true)
            .addAction(R.drawable.splash_icon, "Dismiss", dismissPendingIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(alarmId, notification)
    }

    private fun playAlarmSoundAndVibrate(context: Context) {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (ringtone == null) {
                ringtone = RingtoneManager.getRingtone(context, alarmUri)
            }
            ringtone?.let {
                if (!it.isPlaying) {
                    it.play()
                    Log.d("AlarmReceiver", "Ringtone started")
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error playing ringtone: ${e.message}")
        }

        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(VibratorManager::class.java)
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                val vibrationPattern = longArrayOf(0, 500, 500)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(vibrationPattern, 0)
                }
                Log.d("AlarmReceiver", "Vibration started")
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error starting vibration: ${e.message}")
        }
    }

    fun stopAlarm() {
        ringtone?.stop()
        vibrator?.cancel()
        ringtone = null
        vibrator = null
        Log.d("AlarmReceiver", "Alarm stopped")
    }
}
