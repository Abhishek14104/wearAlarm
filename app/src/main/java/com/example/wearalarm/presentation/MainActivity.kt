package com.example.wearalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.wearalarm.presentation.AlarmReceiver
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wearalarm.ui.theme.WearAlarmTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Request notification permission (Wear OS 4+)
        requestNotificationPermission()

        // ✅ Ensure notification settings are registered
        ensureNotificationSettings()

        setContent {
            WearAlarmTheme {
                WearAlarmApp()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001
                )
            }
        }
    }

    private fun ensureNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as android.app.NotificationManager

            val channel = android.app.NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows full-screen alarm notifications"
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun WearAlarmApp() {
    val context = LocalContext.current

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<Triple<Int, Int, String>?>(null) }
    var isAlarmSet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showTimePicker) {
            TimePickerScreen { hour, minute, amPm ->
                selectedTime = Triple(hour, minute, amPm)
                showTimePicker = false
                isAlarmSet = true
                setAlarm(context, hour, minute, amPm)
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isAlarmSet) {
                    Button(
                        onClick = {
                            cancelAlarm(context)
                            isAlarmSet = false
                            selectedTime = null
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Alarm",
                            tint = Color.Black
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Set New Alarm",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 28.dp)
                        )

                        Button(
                            onClick = { showTimePicker = true },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Set Alarm",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isAlarmSet && selectedTime != null) {
                val (hour, minute, amPm) = selectedTime!!
                Text(
                    text = "Selected Time: %02d:%02d %s".format(hour, minute, amPm),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun TimePickerScreen(onTimeSelected: (Int, Int, String) -> Unit) {
//    val state = rememberPickerState(24, initiallySelected = 12) // Hours
//    val minuteState = rememberPickerState(60, initiallySelected = 0) // Minutes
//    val amPmState = rememberPickerState(2, initiallySelected = 0) // AM/PM
    val hours = (1..12).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }
    val amPm = listOf("AM", "PM")

    val calendar = Calendar.getInstance()
    val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    val currentAmPmIndex = if (currentHour24 >= 12) 1 else 0
    val currentHour12 = if (currentHour24 % 12 == 0) 12 else currentHour24 % 12

//    val amPmList = listOf("AM", "PM")
    val hourState = rememberPickerState(hours.size, initiallySelectedOption = currentHour12 - 1)
    val minuteState = rememberPickerState(minutes.size, initiallySelectedOption = currentMinute)
    val amPmState = rememberPickerState(amPm.size, initiallySelectedOption = currentAmPmIndex)

    val YellowShade = Color(0xFFFDE292)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Time", color = Color.White, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Picker(
                state = hourState,
                modifier = Modifier.size(45.dp, 80.dp),
                contentDescription = "Hour Picker"
            ) { index ->
                Text(
                    text = hours[index],
                    style = MaterialTheme.typography.display1,
                    color = YellowShade,
                    fontSize = 24.sp
                )
            }

            Text(
                ":",
                fontSize = 12.sp,
                color = YellowShade,
                modifier = Modifier.padding(horizontal = 1.dp)
            )

            Picker(
                state = minuteState,
                modifier = Modifier.size(45.dp, 80.dp),
                contentDescription = "Minute Picker"
            ) { index ->
                Text(
                    text = minutes[index],
                    style = MaterialTheme.typography.display1,
                    color = YellowShade,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            Picker(
                state = amPmState,
                modifier = Modifier.size(45.dp, 80.dp),
                contentDescription = "AM/PM Picker"
            ) { index ->
                Text(
                    text = amPm[index],
                    style = MaterialTheme.typography.display1,
                    color = YellowShade,
                    fontSize = 22.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val selectedHour = hours[hourState.selectedOption].toInt()
                    val selectedMinute = minutes[minuteState.selectedOption].toInt()
                    val selectedAmPm = amPm[amPmState.selectedOption]
                    onTimeSelected(selectedHour, selectedMinute, selectedAmPm)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.Black
                )
            }
        }
    }
}

private fun setAlarm(context: Context, hour: Int, minute: Int, amPm: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission(context)
            return
        }
    }

    val hour24 = if (amPm == "PM" && hour != 12) {
        hour + 12
    } else if (amPm == "AM" && hour == 12) {
        0
    } else {
        hour
    }

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour24)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)

        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}

fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

private fun cancelAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)

    AlarmReceiver.ringtone?.let {
        if (it.isPlaying) {
            it.stop()
        }
    }
}