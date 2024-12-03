package io.github.stcksmsh.kap.notifications

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*
import java.util.concurrent.TimeUnit

class DailySchedulerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val startHour = inputData.getInt("startHour", 8)
        val startMinute = inputData.getInt("startMinute", 0)
        val endHour = inputData.getInt("endHour", 22)
        val endMinute = inputData.getInt("endMinute", 0)
        val intervalMinutes = inputData.getInt("intervalMinutes", 60)

        Log.d(
            "DailySchedulerWorker",
            "Scheduling daily reminders from $startHour:$startMinute to $endHour:$endMinute with interval $intervalMinutes minutes"
        )

        val workManager = WorkManager.getInstance(applicationContext)

        // Start and end times
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
            set(Calendar.SECOND, 0)
        }
        val endTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
            set(Calendar.SECOND, 0)
        }

        // Schedule reminders for the day
        var nextReminderTime = startTime.timeInMillis
        while (nextReminderTime < endTime.timeInMillis) {
            val delay = nextReminderTime - System.currentTimeMillis()
            if (delay >= 0) {
                Log.d(
                    "DailySchedulerWorker",
                    "Scheduling reminder at ${Date(nextReminderTime)} in $delay miliseconds"
                )
                val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(
                    delay,
                    TimeUnit.MILLISECONDS
                ).addTag(ReminderScheduler.NOTIFICATION_TAG).build()
                workManager.enqueue(reminderRequest)
            }
            nextReminderTime += TimeUnit.MINUTES.toMillis(intervalMinutes.toLong())
        }

        // start tomorrow at startTime
        val delay = Calendar.getInstance().apply {
            time = startTime.time
            add(Calendar.DAY_OF_MONTH, 1)

            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis - System.currentTimeMillis()

        Log.d(
            "DailySchedulerWorker",
            "Scheduling next day's scheduler at ${Date(startTime.timeInMillis)} in $delay miliseconds"
        )

        // Schedule the next day's scheduler
        val nextDaySchedulerRequest =
            OneTimeWorkRequestBuilder<DailySchedulerWorker>().setInitialDelay(
                delay,
                TimeUnit.MILLISECONDS
            ) // Run again in 24 hours
                .setInputData(inputData) // Pass the same reminder settings
                .addTag(ReminderScheduler.NOTIFICATION_TAG).build()

        workManager.enqueue(nextDaySchedulerRequest)

        return Result.success()
    }
}
