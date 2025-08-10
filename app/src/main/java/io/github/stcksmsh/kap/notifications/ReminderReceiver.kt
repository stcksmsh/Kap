package io.github.stcksmsh.kap.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.stcksmsh.kap.data.loadReminderSettings
import io.github.stcksmsh.kap.notifications.ReminderScheduler.ACTION_REMIND_TICK

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent: ${intent.action}")
        if (intent.action != ACTION_REMIND_TICK) return

        val settings = loadReminderSettings(context)
        if (!settings.remindersEnabled) return

        // 1. Show notification if inside today's window
        val now = System.currentTimeMillis()
        val start = settings.startTime.toTodayCalendar().timeInMillis
        val end = settings.endTime.toTodayCalendar().timeInMillis

        if (now in start..end) {
            NotificationHelper.showNotification(context)
        }

        // 2. Schedule the next reminder
        ReminderScheduler.scheduleReminders(context, settings)
    }
}
