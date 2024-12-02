package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.TextComponent
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.data.loadUserData
import io.github.stcksmsh.kap.ui.composables.WaterIntakeList
import java.text.SimpleDateFormat
import java.util.*

// TODO: fix the different filter options
@Composable
fun OverviewScreen(context: Context, waterIntakeRepository: WaterIntakeRepository) {
    // State to hold selected filter
    var selectedFilter by remember { mutableStateOf("Last Week") }

    val userData = loadUserData(context)

    val settingsData = loadSettingsData(context)

    // Fetch water intake data based on the selected filter
    val startAndEndDate by remember { derivedStateOf { getDateRangeForFilter(selectedFilter) } }

    val waterIntakeData by waterIntakeRepository.getWaterIntakesBetween(
        startAndEndDate.first,
        startAndEndDate.second
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
            listOf("Last Week", "Last Month", "Last Year").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    enabled = filter == "Last Week"
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
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No data available for the selected period.")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Water Intake List
        WaterIntakeList(
            selectedVolumeUnit = settingsData.volumeUnit,
            waterIntakeRepository = waterIntakeRepository,
            modifier = Modifier.weight(1f)
        )
    }
}

fun getDateRangeForFilter(filter: String): Pair<Date, Date> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    calendar.add(Calendar.DAY_OF_YEAR, 1) // Start from tomorrow
    val endDate = calendar.time // Inclusive today at midnight

    when (filter) {
        "Last Week" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
        "Last Month" -> calendar.add(Calendar.MONTH, -1)
        "Last Year" -> calendar.add(Calendar.YEAR, -1)
        else -> Log.e("TESTING", "Invalid filter: $filter")
    }

    val startDate = calendar.time
    return Pair(startDate, endDate)
}

@Composable
fun WaterIntakeGraph(startDate: Date, endDate: Date, data: List<WaterIntake>, dailyWaterGoal: Float) {
    // Generate list of dates from start to end date
    val dates = generateSequence(startDate) { date ->
        Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, 1)
        }.time.takeIf { !it.after(endDate) }
    }.toList()

    val colors = chartColors
    val marker = DefaultCartesianMarker(label = TextComponent())

    // Group data by date without time
    val summedIntakes = data.groupBy { getDateWithoutTime(it.date) }

    // Create corrected data aligned with the date range
    val correctedData: List<WaterIntake> =
        dates.map { date ->
            val totalIntake = summedIntakes[date]?.map { it.intakeAmount }?.sum() ?: 0f
            WaterIntake(intakeAmount = totalIntake, date = date)
        }

    // Initialize the model producer
    val modelProducer = remember { CartesianChartModelProducer() }

    // Update model producer for Cartesian Chart when correctedData changes
    LaunchedEffect(correctedData) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = correctedData.indices.toList(),
                    y = correctedData.map { it.intakeAmount }
                )
            }
        }
    }

    Log.d("TESTING", "Corrected Data: ${correctedData.size}")

    // Cartesian Chart with formatted X-axis
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        remember(colors) {
                            LineCartesianLayer.LineFill.double(fill(colors[0]), fill(colors[1]), splitY = {dailyWaterGoal})
                        }
                    ),
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onBackground)
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onBackground),
                valueFormatter = object : CartesianValueFormatter {
                    override fun format(
                        context: CartesianMeasuringContext,
                        value: Double,
                        verticalAxisPosition: Axis.Position.Vertical?
                    ): CharSequence {
                        val index = value.toInt()
                        return if (index in correctedData.indices) {
                            SimpleDateFormat("dd MMM", Locale.getDefault()).format(correctedData[index].date)
                        } else {
                            value.toString()
                        }
                    }
                }
            ),
            marker = marker
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}


fun getDateWithoutTime(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

private val chartColors
    @ReadOnlyComposable
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.error ,
        MaterialTheme.colorScheme.surfaceBright
    )
