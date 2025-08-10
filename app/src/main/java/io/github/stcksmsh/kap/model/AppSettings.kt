package io.github.stcksmsh.kap.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.math.BigDecimal
import java.math.RoundingMode
import io.github.stcksmsh.kap.R

enum class WeightUnits(
    val kgFactor: Double,
    val decimals: Int,
    @androidx.annotation.StringRes val labelRes: Int,
    val fullName: String
) {
    KGS(1.0,       0, R.string.kilograms, "kilograms"),
    POUNDS(0.45359237, 0, R.string.pounds,    "pounds"),
    STONE(6.35029318,  1, R.string.stone,     "stone");

    fun toUnitString(kilos: Double): String =
        kilos.div(kgFactor).toBigDecimal().setScale(decimals, RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString()

    fun fromUnitToKilos(value: String): Double? = value.toDoubleOrNull()?.times(kgFactor)

    @Composable
    fun label() = stringResource(labelRes)

    fun label(context: Context) = context.getString(labelRes)
}


enum class VolumeUnits(
    val mlFactor: Double,
    val decimals: Int,
    @androidx.annotation.StringRes val labelRes: Int,
    val fullName: String
) {
    MILLILITERS(1.0,      0, R.string.milliliters, "milliliters"),
    LITERS(1000.0,        2, R.string.liters,      "liters"),
    OUNCES(29.5735295625, 1, R.string.ounces,      "ounces"), // US fl oz
    CUPS(240.0,           1, R.string.cups,        "cups"),   // your chosen “cup”
    PINTS(473.176473,     2, R.string.pints,       "pints"),
    GALLONS(3785.411784,  2, R.string.gallons,     "gallons");

    fun toUnitString(ml: Double): String =
        (ml / mlFactor).toBigDecimal().setScale(decimals, RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString()

    @Composable
    fun toUnitWithLabel(ml: Double) =
        "${toUnitString(ml)} ${stringResource(labelRes)}"

    fun toUnitWithLabel(context: Context, ml: Double) =
        "${toUnitString(ml)} ${context.getString(labelRes)}"

    fun fromUnitToMl(value: String): Double? = value.toDoubleOrNull()?.times(mlFactor)

    @Composable
    fun label() = stringResource(labelRes)

    fun label(context: Context) = context.getString(labelRes)
}



data class AppSettings(
    val startupAnimationEnabled: Boolean,
    val weightUnit: WeightUnits,
    val volumeUnit: VolumeUnits,
    val quickWaterAdditionVolumes: List<Double>
) {
    override fun toString(): String {
        return "$startupAnimationEnabled;${weightUnit.name};${volumeUnit.name};${quickWaterAdditionVolumes.joinToString(",")}"
    }

    companion object {
        val defaultQuickWaterAdditionVolumes = listOf(100.0, 250.0, 500.0, 750.0, 1000.0)

        fun fromString(string: String): AppSettings {
            val parts = string.split(";")
            if (parts.size != 4) {
                throw IllegalArgumentException("Invalid settings string: $string")
            }
            return AppSettings(
                parts[0].toBoolean(),
                WeightUnits.valueOf(parts[1]),
                VolumeUnits.valueOf(parts[2]),
                parts[3].split(",").map { it.toDouble() }
            )
        }
    }
}