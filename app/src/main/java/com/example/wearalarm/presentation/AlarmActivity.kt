package com.example.wearalarm.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            AlarmScreen()
        }
    }
}


@Composable
fun AlarmScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Alarm Ringing!",
            fontSize = 18.sp,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ❌ Dismiss Button
        Button(
            onClick = {
                AlarmReceiver.ringtone?.stop() // ✅ Stop the ringtone
                AlarmReceiver.vibrator?.cancel() // ✅ Stop vibration
                AlarmReceiver.ringtone = null
                AlarmReceiver.vibrator = null
                (context as ComponentActivity).finish() // ✅ Close the alarm screen
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            ),
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss Alarm",
                modifier =  Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}
