package io.github.stcksmsh.kap.data

import android.content.Context
import io.github.stcksmsh.kap.model.ReminderSettings
import io.github.stcksmsh.kap.model.TimeOfDay

fun saveReminderSettings(context: Context, reminderSettings: ReminderSettings) {
    val sharedPreferences = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putBoolean("remindersEnabled", reminderSettings.remindersEnabled)
        putInt("intervalMinutes", reminderSettings.intervalMinutes)
        putInt("startTimeHour", reminderSettings.startTime.hour)
        putInt("startTimeMinute", reminderSettings.startTime.minute)
        putInt("endTimeHour", reminderSettings.endTime.hour)
        putInt("endTimeMinute", reminderSettings.endTime.minute)
        putBoolean("vibrationEnabled", reminderSettings.vibrationEnabled)
        putBoolean("soundEnabled", reminderSettings.soundEnabled)
        apply()
    }
}

fun loadReminderSettings(context: Context): ReminderSettings {
    val sharedPreferences = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
    return ReminderSettings(
        remindersEnabled = sharedPreferences.getBoolean("remindersEnabled", false),
        intervalMinutes = sharedPreferences.getInt("intervalMinutes", 90),
        startTime = TimeOfDay(
            hour = sharedPreferences.getInt("startTimeHour", 8),
            minute = sharedPreferences.getInt("startTimeMinute", 0)
        ),
        endTime = TimeOfDay(
            hour = sharedPreferences.getInt("endTimeHour", 22),
            minute = sharedPreferences.getInt("endTimeMinute", 0)
        ),
        vibrationEnabled = sharedPreferences.getBoolean("vibrationEnabled", true),
        soundEnabled = sharedPreferences.getBoolean("soundEnabled", true),
    )
}