package com.example.wearalarm.storage

import android.content.Context
import com.example.wearalarm.model.AlarmModel

object AlarmStorage {
    private const val PREFS_NAME = "alarms_prefs"
    private const val ALARMS_KEY = "alarms_list"

    fun saveAlarm(context: Context, alarmId: Int, hour: Int, minute: Int, amPm: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alarmsSet = prefs.getStringSet(ALARMS_KEY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        alarmsSet.add("$alarmId,$hour,$minute,$amPm")
        prefs.edit().putStringSet(ALARMS_KEY, alarmsSet).apply()
    }

    fun removeAlarm(context: Context, alarmId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alarmsSet = prefs.getStringSet(ALARMS_KEY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        alarmsSet.removeIf { it.startsWith("$alarmId,") }
        prefs.edit().putStringSet(ALARMS_KEY, alarmsSet).apply()
    }

    fun getStoredAlarms(context: Context): List<AlarmModel> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alarmsSet = prefs.getStringSet(ALARMS_KEY, mutableSetOf()) ?: return emptyList()

        return alarmsSet.mapNotNull { alarmString ->
            val parts = alarmString.split(",")
            if (parts.size == 4) {
                val alarmId = parts[0].toInt()
                val hour = parts[1].toInt()
                val minute = parts[2].toInt()
                val amPm = parts[3]
                AlarmModel(alarmId, hour, minute, amPm)
            } else null
        }
    }
}
