package com.example.wearalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.unit.sp
import com.example.wearalarm.presentation.AlarmReceiver
import java.util.Calendar
import android.os.Build
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import com.example.wearalarm.storage.AlarmStorage
import com.example.wearalarm.ui.theme.WearAlarmTheme
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.example.wearalarm.model.AlarmModel

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
    var alarms by remember { mutableStateOf(AlarmStorage.getStoredAlarms(context)) }
    var selectedAlarm by remember { mutableStateOf<AlarmModel?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showTimePicker) {
            TimePickerScreen(
                initialAlarm = selectedAlarm,
                onTimeSelected = { hour, minute, amPm ->
                    if (selectedAlarm != null) {
                        cancelAlarm(context, selectedAlarm!!.alarmId)
                        alarms = alarms.filter { it.alarmId != selectedAlarm!!.alarmId }
                    }

                    val newAlarmId = setAlarm(context, hour, minute, amPm)
                    alarms = AlarmStorage.getStoredAlarms(context)

                    showTimePicker = false
                    selectedAlarm = null
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (alarms.isEmpty()) {
                    Text(
                        text = "Set New Alarm",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                ScalingLazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alarms) { alarm ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(1.dp)
                                .clickable {
                                    selectedAlarm = alarm
                                    showTimePicker = true
                                }
                        ) {
                            Text(
                                text = "Alarm at %02d:%02d %s".format(alarm.hour, alarm.minute, alarm.amPm),
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    cancelAlarm(context, alarm.alarmId)
                                    alarms = AlarmStorage.getStoredAlarms(context)
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                modifier = Modifier.size(35.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss Alarm", tint = Color.Red)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        selectedAlarm = null
                        showTimePicker = true
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Set Alarm", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
fun TimePickerScreen(initialAlarm: AlarmModel? = null, onTimeSelected: (Int, Int, String) -> Unit) {
    val calendar = Calendar.getInstance()
    val defaultHour24 = initialAlarm?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
    val defaultMinute = initialAlarm?.minute ?: calendar.get(Calendar.MINUTE)
    val defaultAmPmIndex = if ((initialAlarm?.hour ?: defaultHour24) >= 12) 1 else 0
    val defaultHour12 = if (defaultHour24 % 12 == 0) 12 else defaultHour24 % 12

    val hours = (1..12).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }
    val amPm = listOf("AM", "PM")

    val hourState = rememberPickerState(hours.size, initiallySelectedOption = defaultHour12 - 1)
    val minuteState = rememberPickerState(minutes.size, initiallySelectedOption = defaultMinute)
    val amPmState = rememberPickerState(amPm.size, initiallySelectedOption = defaultAmPmIndex, repeatItems = false)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Time", color = Color.White, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Picker(
                state = hourState,
                modifier = Modifier.size(45.dp, 60.dp),
                contentDescription = "Hour Picker"
            ) { index ->
                Text(
                    text = hours[index],
                    fontSize = if (index == hourState.selectedOption) 20.sp else 16.sp,
                    color = Color.Yellow
                )
            }

            Text(":", fontSize = 20.sp, color = Color.Yellow, modifier = Modifier.padding(horizontal = 1.dp))

            Picker(
                state = minuteState,
                modifier = Modifier.size(45.dp, 60.dp),
                contentDescription = "Minute Picker"
            ) { index ->
                Text(
                    text = minutes[index],
                    fontSize = if (index == minuteState.selectedOption) 20.sp else 16.sp,
                    color = Color.Yellow
                )
            }

            Picker(
                state = amPmState,
                modifier = Modifier.size(45.dp, 50.dp),
                contentDescription = "AM/PM Picker"
            ) { index ->
                Text(
                    text = amPm[index],
                    fontSize = if (index == amPmState.selectedOption) 20.sp else 16.sp,
                    color = Color.Yellow
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val selectedHour = hours[hourState.selectedOption].toInt()
                val selectedMinute = minutes[minuteState.selectedOption].toInt()
                val selectedAmPm = amPm[amPmState.selectedOption]
                onTimeSelected(selectedHour, selectedMinute, selectedAmPm)
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Confirm", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(0.2.dp))
    }
}

private fun setAlarm(context: Context, hour: Int, minute: Int, amPm: String): Int {
    val alarmId = System.currentTimeMillis().toInt()
    val alarmTime = "%02d:%02d %s".format(hour, minute, amPm)

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("alarm_id", alarmId)
        putExtra("alarm_time", alarmTime)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, if (amPm == "PM" && hour != 12) hour + 12 else hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Enable exact alarms in settings", Toast.LENGTH_LONG).show()
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    } catch (e: SecurityException) {
        Toast.makeText(context, "Permission required for exact alarms", Toast.LENGTH_LONG).show()
    }

    AlarmStorage.saveAlarm(context, alarmId, hour, minute, amPm)
    return alarmId
}



private fun cancelAlarm(context: Context, alarmId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
    AlarmStorage.removeAlarm(context, alarmId)
}
