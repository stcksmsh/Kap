package io.github.stcksmsh.kap.data

import android.content.Context
import io.github.stcksmsh.kap.model.UserSettings

fun saveUserSettings(context: Context, userSettings: UserSettings) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("age", userSettings.age)
        putDouble("weight", userSettings.weight)
        putInt("dailyPhysicalActivityDuration", userSettings.dailyPhysicalActivity)
        putDouble("dalyWaterGoal", userSettings.dailyWaterGoal)
        apply()
    }
}

fun loadUserSettings(context: Context): UserSettings {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return UserSettings(
        age = sharedPreferences.getInt("age", 0),
        weight = sharedPreferences.getDouble("weight", 0.0),
        dailyPhysicalActivity = sharedPreferences.getInt("dailyPhysicalActivityDuration", -1),
        dailyWaterGoal = sharedPreferences.getDouble("dalyWaterGoal", 0.0)
    )
}

fun hasUserSettings(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.contains("age")  // Check if age or other data exists
}

fun calculateOptimalWaterIntake(age: Int, weight: Double, dailyPhysicalActivity: Int): Double {
    val ageBasedWaterIntake = when {
        age < 18 -> 200
        age < 55 -> 0
        else -> -200
    }
    return ((weight * 0.033) * 1000 + (dailyPhysicalActivity / 60.0) * 0.35 * 1000 + ageBasedWaterIntake)
}