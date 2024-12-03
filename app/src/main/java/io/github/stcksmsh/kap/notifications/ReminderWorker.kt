package io.github.stcksmsh.kap.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadReminderSettings
import io.github.stcksmsh.kap.data.loadUserSettings
import io.github.stcksmsh.kap.model.TimeOfDay

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "Notification check")
        val repository = WaterIntakeRepository(
            WaterIntakeDatabase.getDatabase(context = applicationContext).waterIntakeDao()
        )
        val currentIntake = repository.getCurrentIntake().value
        val dailyIntakeGoal = loadUserSettings(context = applicationContext).dailyWaterGoal

        val reminderSettings = loadReminderSettings(applicationContext)
        if (!isDrinkingEnoughWater(currentIntake, dailyIntakeGoal, reminderSettings.startTime, reminderSettings.endTime)) {
            NotificationHelper.showNotification(applicationContext)
        }

        return Result.success()
    }
}

fun isDrinkingEnoughWater(currentIntake: Float, dailyIntake: Float, startTime: TimeOfDay, endTimeOfDay: TimeOfDay): Boolean{
    val currentTime = TimeOfDay.now()

    // calculate the time difference between the start and end time
    val timeInDay = endTimeOfDay - startTime
    val elapsedTime = currentTime - startTime

    val percentTime = elapsedTime.toFloat() / timeInDay
    val percentIntake = currentIntake / dailyIntake

    return percentIntake >= percentTime
}
