package com.example.wearalarm.presentation

import android.app.*
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.wearalarm.R

class AlarmService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "alarm_service_channel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alarm Service", NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Running")
            .setContentText("Your alarm is active")
            .setSmallIcon(R.drawable.splash_icon)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}