package io.github.stcksmsh.kap.data

import android.content.Context
import android.util.Log
import io.github.stcksmsh.kap.model.UserData

fun saveUserData(context: Context, userData: UserData) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("age", userData.age)
        putInt("weight", userData.weight)
        putInt("dailyPhysicalActivityDuration", userData.dailyPhysicalActivityDuration)
        putInt("dalyWaterGoal", userData.dalyWaterGoal)
        apply()
    }
}

fun loadUserData(context: Context): UserData? {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val age = sharedPreferences.getInt("age", -1)
    if (age == -1) return null  // No data saved
    return UserData(
        age = age,
        weight = sharedPreferences.getInt("weight", 0),
        dailyPhysicalActivityDuration = sharedPreferences.getInt("dailyPhysicalActivityDuration", 0),
        dalyWaterGoal = sharedPreferences.getInt("dalyWaterGoal", 0)
    )
}

fun hasUserData(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.contains("age")  // Check if age or other data exists
}

fun clearUserData(context: Context) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()
}

fun calculateOptimalWaterIntake(age: Int, weight: Int, dailyPhysicalActivity: Int): Int{
    val ageBasedWaterIntake = when {
        age < 18 -> 200
        age < 55 -> 0
        else -> -200
    }
    return ((weight * 0.033) * 1000 + (dailyPhysicalActivity / 60.0) * 0.35 * 1000 + ageBasedWaterIntake).toInt()
}