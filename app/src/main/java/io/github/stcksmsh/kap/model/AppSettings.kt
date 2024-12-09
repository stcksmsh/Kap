package io.github.stcksmsh.kap.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.math.BigDecimal
import java.math.RoundingMode
import io.github.stcksmsh.kap.R

enum class WeightUnits(
    val kilograms: Float,
    val decimals: Int,
    val fullName: String
) {
    KGS(1.0f, 0, "kilograms"),
    POUNDS(0.453592f, 0, "pounds"),
    STONE(6.35029f, 1, "stone");

    fun convertKilosToString(value: Float): String {
        val scaledValue = value / kilograms
        val roundedValue =
            BigDecimal(scaledValue.toDouble()).setScale(decimals, RoundingMode.HALF_UP)
        return roundedValue.stripTrailingZeros().toPlainString()
    }

    fun convertStringToKilos(value: String): Float? {
        return value.toFloatOrNull()?.times(kilograms)
    }

    @Composable
    fun getLocalizedSymbol(): String {
        return when (this) {
            KGS -> stringResource(R.string.kilograms)
            POUNDS -> stringResource(R.string.pounds)
            STONE -> stringResource(R.string.stone)
        }
    }

    // these exist because the widget doesn't have access to the Composable function, and it just throws `java.lang.IllegalStateException: CompositionLocal LocalConfiguration not present`

    fun getLocalizedSymbol(context: Context): String{
        return when (this) {
            KGS -> context.getString(R.string.kilograms)
            POUNDS -> context.getString(R.string.pounds)
            STONE -> context.getString(R.string.stone)
        }
    }

}

enum class VolumeUnits(
    val milliliters: Float,
    val decimals: Int,
    val fullName: String
) {
    MILLILITERS(1.0f, 0, "milliliters"),
    LITERS(1000.0f, 2, "liters"),
    OUNCES(29.5735f, 1, "ounces"),
    CUPS(240.0f, 1, "cups"),
    PINTS(473.176f, 2, "pints"),
    GALLONS(3785.41f, 2, "gallons");

    fun convertMillisToString(value: Float): String {
        val scaledValue = value / milliliters
        val roundedValue =
            BigDecimal(scaledValue.toDouble()).setScale(decimals, RoundingMode.HALF_UP)
        return roundedValue.stripTrailingZeros().toPlainString()
    }

    @Composable
    fun convertMillisToUnitString(value: Float): String {
        return "${convertMillisToString(value)} ${getLocalizedSymbol()}"
    }

    fun convertStringToMillis(value: String): Float? {
        return value.toFloatOrNull()?.times(milliliters)
    }

    @Composable
    fun getLocalizedSymbol(): String {
        return when (this) {
            MILLILITERS -> stringResource(R.string.milliliters)
            LITERS -> stringResource(R.string.liters)
            OUNCES -> stringResource(R.string.ounces)
            CUPS -> stringResource(R.string.cups)
            PINTS -> stringResource(R.string.pints)
            GALLONS -> stringResource(R.string.gallons)
        }
    }

    // these exist because the widget doesn't have access to the Composable function, and it just throws `java.lang.IllegalStateException: CompositionLocal LocalConfiguration not present`

    fun convertMillisToUnitString(context: Context, value: Float): String {
        return "${convertMillisToString(value)} ${getLocalizedSymbol(context)}"
    }

    fun getLocalizedSymbol(context: Context): String{
        return when (this) {
            MILLILITERS -> context.getString(R.string.milliliters)
            LITERS -> context.getString(R.string.liters)
            OUNCES -> context.getString(R.string.ounces)
            CUPS -> context.getString(R.string.cups)
            PINTS -> context.getString(R.string.pints)
            GALLONS -> context.getString(R.string.gallons)
        }
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