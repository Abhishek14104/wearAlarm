//package com.example.wearalarm.presentation
//
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.media.Ringtone
//import android.media.RingtoneManager
//import android.os.*
//import androidx.core.app.NotificationCompat
//import com.example.wearalarm.R
//
//class AlarmService : Service() {
//
//    private var ringtone: Ringtone? = null
//    private lateinit var vibrator: Vibrator
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // ✅ Start foreground notification (keeps service alive)
//        startForegroundService()
//
//        // ✅ Initialize vibrator
//        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//
//        // ✅ Start vibration (Check API level)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 1000, 500, 1000), 0))
//        } else {
//            vibrator.vibrate(longArrayOf(0, 1000, 500, 1000), 0)
//        }
//
//        // ✅ Play ringtone
//        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        ringtone = RingtoneManager.getRingtone(this, alarmUri)
//        ringtone?.play()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        ringtone?.stop()
//        vibrator.cancel()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    private fun startForegroundService() {
//        val notificationIntent = Intent(this, AlarmActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(this, "alarm_channel")
//            .setContentTitle("Alarm Ringing")
//            .setContentText("Tap to dismiss")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setFullScreenIntent(pendingIntent, true) // ✅ Ensure pop-up shows
//            .build()
//
//        startForeground(1, notification)
//    }
//
//    companion object {
//        fun stopAlarm(context: Context) {
//            val intent = Intent(context, AlarmService::class.java)
//            context.stopService(intent)
//        }
//    }
//}
