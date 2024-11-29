package io.github.stcksmsh.kap.data

import android.content.Context
import io.github.stcksmsh.kap.model.UserData

fun saveUserData(context: Context, userData: UserData) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("age", userData.age)
        putFloat("weight", userData.weight)
        putInt("dailyPhysicalActivityDuration", userData.dailyPhysicalActivity)
        putFloat("dalyWaterGoal", userData.dailyWaterGoal)
        apply()
    }
}

fun loadUserData(context: Context): UserData {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return UserData(
        age = sharedPreferences.getInt("age", 0),
        weight = sharedPreferences.getFloat("weight", 0f),
        dailyPhysicalActivity = sharedPreferences.getInt("dailyPhysicalActivityDuration", -1),
        dailyWaterGoal = sharedPreferences.getFloat("dalyWaterGoal", -1f)
    )
}

fun hasUserData(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.contains("age")  // Check if age or other data exists
}

fun calculateOptimalWaterIntake(age: Int, weight: Float, dailyPhysicalActivity: Int): Float {
    val ageBasedWaterIntake = when {
        age < 18 -> 200
        age < 55 -> 0
        else -> -200
    }
    return ((weight * 0.033) * 1000 + (dailyPhysicalActivity / 60.0) * 0.35 * 1000 + ageBasedWaterIntake).toFloat()
}