package io.github.stcksmsh.kap.model

import java.math.BigDecimal
import java.math.RoundingMode

enum class WeightUnits(
    val symbol: String,
    val kilograms: Float,
    val decimals: Int,
    val fullName: String
) {
    KGS("kg", 1.0f, 0, "kilograms"),
    POUNDS("lbs", 0.453592f, 0, "pounds"),
    STONES("st", 6.35029f, 1, "stones");

    fun convertKilosToString(value: Float): String {
        val scaledValue = value / kilograms
        val roundedValue =
            BigDecimal(scaledValue.toDouble()).setScale(decimals, RoundingMode.HALF_UP)
        return roundedValue.stripTrailingZeros().toPlainString()
    }

    fun convertStringToKilos(value: String): Float? {
        return value.toFloatOrNull()?.times(kilograms)
    }

}

enum class VolumeUnits(
    val symbol: String,
    val milliliters: Float,
    val decimals: Int,
    val fullName: String
) {
    MILLILITERS("ml", 1.0f, 0, "milliliters"),
    LITERS("L", 1000.0f, 2, "liters"),
    FLUID_OUNCES("fl oz", 29.5735f, 1, "fluid ounces"),
    CUPS("cups", 240.0f, 1, "cups"),
    PINTS("pt", 473.176f, 2, "pints"),
    GALLONS("gal", 3785.41f, 2, "gallons");

    fun convertMillisToString(value: Float): String {
        val scaledValue = value / milliliters
        val roundedValue =
            BigDecimal(scaledValue.toDouble()).setScale(decimals, RoundingMode.HALF_UP)
        return roundedValue.stripTrailingZeros().toPlainString()
    }

    fun convertMillisToUnitString(value: Float): String {
        return "${convertMillisToString(value)} $symbol"
    }

    fun convertStringToMillis(value: String): Float? {
        return value.toFloatOrNull()?.times(milliliters)
    }

}

data class AppSettings(
    val startupAnimationEnabled: Boolean,
    val weightUnit: WeightUnits,
    val volumeUnit: VolumeUnits,
    val quickWaterAdditionVolumes: List<Float>
) {
    companion object {
        val defaultQuickWaterAdditionVolumes = listOf(100.0f, 250.0f, 500.0f, 750.0f, 1000.0f)
    }
}