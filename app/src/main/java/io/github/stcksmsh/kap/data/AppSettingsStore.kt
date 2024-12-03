package io.github.stcksmsh.kap.data

import android.content.Context
import io.github.stcksmsh.kap.model.AppSettings
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits

fun saveSettingsData(context: Context, appSettings: AppSettings) {
    val sharedProgression = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    with(sharedProgression.edit()) {
        putBoolean("startupAnimationEnabled", appSettings.startupAnimationEnabled)
        putString("weightUnit", appSettings.weightUnit.name)
        putString("volumeUnit", appSettings.volumeUnit.name)
        putString(
            "quickWaterAdditionVolumes",
            appSettings.quickWaterAdditionVolumes.joinToString(";")
        )
        apply()
    }
}

fun loadSettingsData(context: Context): AppSettings {
    val sharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    return AppSettings(
        startupAnimationEnabled = sharedPreferences.getBoolean("startupAnimationEnabled", true),
        weightUnit = WeightUnits.valueOf(
            sharedPreferences.getString(
                "weightUnit",
                WeightUnits.KGS.name
            )!!
        ),
        volumeUnit = VolumeUnits.valueOf(
            sharedPreferences.getString(
                "volumeUnit",
                VolumeUnits.MILLILITERS.name
            )!!
        ),
        quickWaterAdditionVolumes = sharedPreferences.getString("quickWaterAdditionVolumes", null)
            ?.split(';')?.map { it.toFloat() } ?: AppSettings.defaultQuickWaterAdditionVolumes
    )
}