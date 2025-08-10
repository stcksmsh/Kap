package io.github.stcksmsh.kap.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.AreaFill.Companion.double
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.LinearGradientShaderProvider
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadAppSettings
import io.github.stcksmsh.kap.data.loadUserSettings
import io.github.stcksmsh.kap.ui.composables.WaterIntakeList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

private const val TAG = "InsightsScreen"

// TODO: fix the different filter options
@Composable
fun InsightsScreen(context: Context, waterIntakeRepository: WaterIntakeRepository) {

    // State to hold selected filter
    val filterOptions = listOf(
        context.getString(R.string.last_week),
        context.getString(R.string.last_month),
        context.getString(R.string.last_year)
    )

    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }

    val userData = loadUserSettings(context)

    val appSettings = loadAppSettings(context)

    // Fetch water intake data based on the selected filter
    val startAndEndDate by remember {
        derivedStateOf {
            getDateRangeForFilterIndex(
                filterOptions.indexOf(selectedFilter)
            )
        }
    }

    val waterIntakeData by waterIntakeRepository.getWaterIntakesBetween(
        startAndEndDate.first, startAndEndDate.second
    ).collectAsState(initial = emptyList())

    // Display the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter Options
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                stringResource(R.string.last_week),
                stringResource(R.string.last_month),
                stringResource(R.string.last_year)

            ).forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    enabled = (filter == stringResource(R.string.last_week))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Graph Section
        if (waterIntakeData.isNotEmpty()) {
            WaterIntakeGraph(
                startDate = startAndEndDate.first,
                endDate = startAndEndDate.second,
                data = waterIntakeData,
                dailyWaterGoal = userData.dailyWaterGoal
            )
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Text("No data available for the selected period.")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Water Intake List
        WaterIntakeList(
            selectedVolumeUnit = appSettings.volumeUnit,
            waterIntakeRepository = waterIntakeRepository,
            modifier = Modifier.weight(1f)
        )
    }
}

fun getDateRangeForFilterIndex(filterIndex: Int): Pair<Date, Date> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    calendar.add(Calendar.DAY_OF_YEAR, 1) // Start from tomorrow
    val endDate = calendar.time // Inclusive today at midnight

    when (filterIndex) {
        0 -> calendar.add(Calendar.DAY_OF_YEAR, -7)
        1 -> calendar.add(Calendar.MONTH, -1)
        2 -> calendar.add(Calendar.YEAR, -1)
        else -> Log.e(TAG, "Invalid filterIndex: $filterIndex")
    }

    val startDate = calendar.time
    return Pair(startDate, endDate)
}

@Composable
fun WaterIntakeGraph(
    startDate: Date, endDate: Date, data: List<WaterIntake>, dailyWaterGoal: Double
) {

    val correctedEndDate =
        Calendar.getInstance().apply { time = endDate; add(Calendar.DAY_OF_YEAR, -1) }.time
    // Generate list of dates from start to end date
    val correctedData = remember(startDate, correctedEndDate, data) {
        groupData(data.groupBy { getDateWithoutTime(it.date) }.map {
            WaterIntake(intakeAmount = it.value.sumOf { it.intakeAmount }, date = it.key)
        }, startDate, correctedEndDate
        )
    }

    val colors = chartColors
    val marker = DefaultCartesianMarker(label = TextComponent())

    // Initialize the model producer
    val modelProducer = remember { CartesianChartModelProducer() }

    val valueFormatter = createValueFormatter(startDate, correctedEndDate)

    // Update model producer for Cartesian Chart when correctedData changes
    LaunchedEffect(correctedData) {
        modelProducer.runTransaction {
            lineSeries {
                series(x = correctedData.indices.toList(),
                    y = List(correctedData.size) { dailyWaterGoal })
                series(
                    x = correctedData.indices.toList(),
                    y = correctedData.map { it.intakeAmount })
            }
        }
    }

    @SuppressLint("RestrictedApi")
    val topAreaShader = remember(colors) {
        LinearGradientShaderProvider(
            colors = intArrayOf(
                colors[0].copy(alpha = 0.2f).toArgb(),
                colors[0].copy(alpha = 0.7f).toArgb()
            ),
            positions = floatArrayOf(0f, 1f),
            isHorizontal = false
        )
    }

    @SuppressLint("RestrictedApi")
    val bottomAreaShader = remember(colors) {
        LinearGradientShaderProvider(
            colors = intArrayOf(
                colors[1].copy(alpha = 0.7f).toArgb(),
                colors[1].copy(alpha = 0.2f).toArgb()
            ),
            positions = floatArrayOf(0f, 1f),
            isHorizontal = false
        )
    }

    // Cartesian Chart with formatted X-axis
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = remember { LineCartesianLayer.LineFill.single(fill(colors[2])) },
                        areaFill = null,
                    ), LineCartesianLayer.rememberLine(
                        fill = remember(colors) {
                            LineCartesianLayer.LineFill.double(
                                fill(colors[0]),
                                fill(colors[1]),
                                splitY = { dailyWaterGoal })
                        }, areaFill = double(
                            topFill = Fill(
                                topAreaShader
                            ),
                            bottomFill = Fill(
                                bottomAreaShader
                            ),
                            splitY = { dailyWaterGoal },
                        )
                    )
                )
            ), startAxis = VerticalAxis.rememberStart(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onBackground)
            ), bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onBackground),
                valueFormatter = valueFormatter
            ), marker = marker
        ), modelProducer = modelProducer, modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

fun createValueFormatter(startDate: Date, endDate: Date): CartesianValueFormatter {
    // get number of days in range
    val days = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
    val dateFormat = SimpleDateFormat(
        when (days) {
            // day/month
            in 0..31 -> "dd/MM"
            // just month
            else -> "MMM"
        }, Locale.getDefault()
    )
    return object : CartesianValueFormatter {
        override fun format(
            context: CartesianMeasuringContext,
            value: Double,
            verticalAxisPosition: Axis.Position.Vertical?
        ): CharSequence {
            val date = Date(startDate.time + (value * 24 * 60 * 60 * 1000).toLong())
            return dateFormat.format(date)
        }

    }
}

private fun groupData(
    waterIntakeData: List<WaterIntake>, startDate: Date, endDate: Date
): List<WaterIntake> {
    val calendar = Calendar.getInstance()
    // get number of days in range
    val days = calendar.run {
        time = endDate
        val endDay = get(Calendar.DAY_OF_YEAR)
        time = startDate
        val startDay = get(Calendar.DAY_OF_YEAR)
        endDay - startDay
    }
    when (days) {
        in 1..7 -> {
            val dates = (0..days).map { Date(startDate.time + (it * 24 * 60 * 60 * 1000)) }
            return dates.map { date ->
                WaterIntake(intakeAmount = waterIntakeData.filter { getDateWithoutTime(it.date) == date }
                    .sumOf { it.intakeAmount }, date = date
                )
            }
        }

        in 8..31 -> {
            // get number of weeks in range
            val weeks = calendar.run {
                time = endDate
                val endWeek = get(Calendar.WEEK_OF_YEAR)
                time = startDate
                val startWeek = get(Calendar.WEEK_OF_YEAR)
                endWeek - startWeek + 1
            }
            calendar.apply {
                time = startDate
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            val dates = generateSequence(calendar.apply {
                time = startDate; set(
                Calendar.DAY_OF_WEEK, Calendar.MONDAY
            )
            }) {
                it.apply { add(Calendar.DAY_OF_YEAR, 7) }
            }.take(weeks).toList().map {
                it.time
            }
            return dates.map { date ->
                WaterIntake(intakeAmount = waterIntakeData.filter {
                    calendar.apply {
                        time = getDateWithoutTime(it.date)
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    }.time == date
                }.sumOf { it.intakeAmount }, date = date
                )
            }
        }

        else -> {
            // get number of months in range
            val weeks = calendar.run {
                time = endDate
                val endMonth = get(Calendar.MONTH)
                time = startDate
                val startMonth = get(Calendar.MONTH)
                endMonth - startMonth + 1
            }
            calendar.apply {
                time = startDate
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val dates = generateSequence(calendar.apply {
                time = startDate
                set(Calendar.DAY_OF_MONTH, 1)
            }) {
                it.apply { add(Calendar.MONTH, 1) }
            }.take(weeks).toList().map {
                it.time
            }
            return dates.map { date ->
                WaterIntake(intakeAmount = waterIntakeData.filter {
                    calendar.apply {
                        time = getDateWithoutTime(it.date)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }.time == date
                }.sumOf { it.intakeAmount }, date = date
                )
            }
        }
    }
}

private fun getDateWithoutTime(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

private fun Color.brighten(factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[2] = min(1f, hsv[2] * factor) // Increase brightness (value)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

private val chartColors
    @ReadOnlyComposable @Composable get() = listOf(
        MaterialTheme.colorScheme.primary.brighten(2f),
        MaterialTheme.colorScheme.error.brighten(2f),
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
    )

