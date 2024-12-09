package io.github.stcksmsh.kap.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
import io.github.stcksmsh.kap.model.ReminderSettings

object ReminderScheduler {

    const val NOTIFICATION_TAG = "kap_hydration_reminder"

    fun scheduleReminders(context: Context, settings: ReminderSettings) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(NOTIFICATION_TAG) // Clear existing reminders

        if (workManager.getWorkInfosByTag(NOTIFICATION_TAG).get().isNotEmpty()) {
            Log.d("ReminderScheduler", "Existing reminders found, cancelling all")
            workManager.cancelAllWorkByTag(NOTIFICATION_TAG)
        }

        val constraints = Constraints.Builder()
            .build()


        // Enqueue the initial daily reminder scheduler
        val dailySchedulerRequest = OneTimeWorkRequestBuilder<DailySchedulerWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(constraints)
            .setInputData(createInputData(settings))
            .addTag(NOTIFICATION_TAG)
            .build()

        Log.d("ReminderScheduler", "Scheduling daily reminder")

        workManager.enqueueUniqueWork(
            "daily_reminder",
            ExistingWorkPolicy.REPLACE,
            dailySchedulerRequest
        )
    }

    fun cancelReminders(context: Context) {
        Log.d("ReminderScheduler", "Cancelling all reminders")
        WorkManager.getInstance(context).cancelAllWorkByTag(NOTIFICATION_TAG)
    }

    private fun createInputData(settings: ReminderSettings): Data {
        return Data.Builder()
            .putInt("startHour", settings.startTime.hour)
            .putInt("startMinute", settings.startTime.minute)
            .putInt("endHour", settings.endTime.hour)
            .putInt("endMinute", settings.endTime.minute)
            .putInt("intervalMinutes", settings.intervalMinutes)
            .build()
    }
}
