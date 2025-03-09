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
        when (intent?.action) {
            "DISMISS_ALARM" -> {
                stopAlarm()
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.cancel(1)
                return
            }
        }

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "DISMISS_ALARM"
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "alarm_channel"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.splash_icon)
            .setContentTitle("Alarm Ringing!")
            .setContentText("Tap to dismiss")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .addAction(R.drawable.splash_icon, "Dismiss", dismissPendingIntent) // ⬅️ Dismiss button added

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            notificationBuilder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }

        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        NotificationManagerCompat.from(context).notify(1, notification)

        //has to use this as the other one was depricated in JAVA
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator?.hasVibrator() == true) {
            Log.d("AlarmReceiver", "Device supports vibration")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0)
                vibrator?.vibrate(effect)
                Log.d("AlarmReceiver", "Vibration started with effect: $effect")
            } else {
                vibrator?.vibrate(longArrayOf(0, 500, 500), 0)
                Log.d("AlarmReceiver", "Vibration started (legacy method)")
            }
        } else {
            Log.d("AlarmReceiver", "Vibration not supported on this device (probably an emulator)")
        }

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone?.let {
            if (!it.isPlaying) {
                it.play()
                Log.d("AlarmReceiver", "Ringtone started")
            }
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
