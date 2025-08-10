package io.github.stcksmsh.kap.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.stcksmsh.kap.model.ReminderSettings
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"
    private const val REQ_CODE = 10042
    const val ACTION_REMIND_TICK = "kap.REMIND_TICK"


    fun scheduleReminders(context: Context, settings: ReminderSettings) {
        Log.d(TAG, "Scheduling reminders with settings: $settings")
        cancelReminders(context)

        if (!settings.remindersEnabled) {
            Log.d(TAG, "Reminders disabled, nothing to schedule")
            return
        }

        val nextAt = NextTickCalculator.nextTriggerUtcMillis(settings)
        setWindowAlarm(context, nextAt)
    }

    fun cancelReminders(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.cancel(reminderPI(context))
    }

    private fun setWindowAlarm(context: Context, atMillis: Long) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.setWindow(
            AlarmManager.RTC_WAKEUP,
            atMillis,
            5 * 60 * 1000L, // 5-minute flex
            reminderPI(context)
        )
        Log.d(TAG, "Scheduled next reminder at: ${java.util.Date(atMillis)}")
    }

    private fun reminderPI(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).setAction(ACTION_REMIND_TICK)
        return PendingIntent.getBroadcast(
            context, REQ_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

object NextTickCalculator {
    fun nextTriggerUtcMillis(settings: ReminderSettings, nowUtc: Long = System.currentTimeMillis()): Long {
        val start = settings.startTime.toTodayCalendar()
        val end   = settings.endTime.toTodayCalendar()
        val intervalMs = TimeUnit.MINUTES.toMillis(settings.intervalMinutes.toLong())

        return when {
            nowUtc < start.timeInMillis -> start.timeInMillis // before window starts, schedule for start time today
            nowUtc >= end.timeInMillis -> start.apply { add(java.util.Calendar.DAY_OF_MONTH, 1) }.timeInMillis // after window ends, schedule for next day
            else -> { // during window → round up to next interval
                val sinceStart = nowUtc - start.timeInMillis
                val k = ceil(sinceStart.toDouble() / intervalMs).toLong()
                val next = start.timeInMillis + k * intervalMs
                if (next <= end.timeInMillis) next
                else start.apply { add(java.util.Calendar.DAY_OF_MONTH, 1) }.timeInMillis
            }
        }
    }
}
