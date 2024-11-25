package io.github.stcksmsh.kap.data

import android.content.Context
import io.github.stcksmsh.kap.model.SettingsData
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits

fun saveSettingsData(context: Context, settingsData: SettingsData){
    val sharedProgression = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    with(sharedProgression.edit()){
        putBoolean("startupAnimationEnabled", settingsData.startupAnimationEnabled)
        putString("weightUnit", settingsData.weightUnit.name)
        putString("volumeUnit", settingsData.volumeUnit.name)
        putString("quickWaterAdditionVolumes", settingsData.quickWaterAdditionVolumes.joinToString(";"))
        apply()
    }
}

fun loadSettingsData(context: Context): SettingsData {
    val sharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    return SettingsData(
        startupAnimationEnabled = sharedPreferences.getBoolean("startupAnimationEnabled", true),
        weightUnit = WeightUnits.valueOf(sharedPreferences.getString("weightUnit", WeightUnits.KGS.name)!!),
        volumeUnit = VolumeUnits.valueOf(sharedPreferences.getString("volumeUnit", VolumeUnits.MILLILITERS.name)!!),
        quickWaterAdditionVolumes = sharedPreferences.getString("quickWaterAdditionVolumes", null)?.split(';')?.map { it.toFloat()} ?: SettingsData.defaultQuickWaterAdditionVolumes
    )
}