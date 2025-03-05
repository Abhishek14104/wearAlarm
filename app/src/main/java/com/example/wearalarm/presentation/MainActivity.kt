package com.example.wearalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.example.wearalarm.presentation.AlarmReceiver
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings
import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.wearalarm.ui.theme.WearAlarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearAlarmTheme {
                WearAlarmApp()
            }
        }
    }
}


@Composable
fun WearAlarmApp() {
    val context = LocalContext.current

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isAlarmSet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showTimePicker) {
            TimePickerScreen { hour, minute ->
                selectedTime = hour to minute
                showTimePicker = false
                isAlarmSet = true
                setAlarm(context, hour, minute)
            }
        } else {
            if (isAlarmSet) {
                Button(
                    onClick = {
                        cancelAlarm(context)
                        isAlarmSet = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(85.dp)
                        .height(30.dp)
                ) {
                    Text(
                        text = "Dismiss Alarm",
                        fontSize = 12.sp,
                        style = TextStyle(lineHeight = 14.sp),
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = { showTimePicker = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(80.dp)
                        .height(30.dp)
                ) {
                    Text(
                        text = "Set Alarm",
                        fontSize = 12.sp,
                        style = TextStyle(lineHeight = 14.sp),
                        color = Color.White
                    )
                }
            }

            selectedTime?.let { (hour, minute) ->
                Text(
                    "Selected Time: %02d:%02d".format(hour, minute),
                    color = Color.White
                )
            }
        }
    }
}



@Composable
fun TimePickerScreen(onTimeSelected: (Int, Int) -> Unit) {
    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    val hourState = rememberPickerState(hours.size, initiallySelectedOption = 12)
    val minuteState = rememberPickerState(minutes.size, initiallySelectedOption = 0)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Picker(
                state = hourState,
                modifier = Modifier.size(70.dp, 70.dp),
                contentDescription = "Hour Picker"
            ) { index ->
                Text(
                    hours[index],
                    style = MaterialTheme.typography.display1,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            Picker(
                state = minuteState,
                modifier = Modifier.size(70.dp, 70.dp),
                contentDescription = "Minute Picker"
            ) { index ->
                Text(
                    minutes[index],
                    style = MaterialTheme.typography.display1,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val selectedHour = hourState.selectedOption
                val selectedMinute = minuteState.selectedOption
                onTimeSelected(selectedHour, selectedMinute)
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.DarkGray,
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(90.dp)
                .height(35.dp)
        ) {
            Text(
                text = "Confirm Alarm",
                fontSize = 12.sp,
                style = TextStyle(lineHeight = 14.sp),
                color = Color.White
            )
        }
    }
}



private fun setAlarm(context: Context, hour: Int, minute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission(context)
            return
        }
    }

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)

        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}


fun requestExactAlarmPermission(context: Context) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
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


