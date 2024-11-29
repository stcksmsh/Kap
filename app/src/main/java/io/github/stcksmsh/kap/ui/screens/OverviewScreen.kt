package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OverviewScreen(context: Context, waterIntakeRepository: WaterIntakeRepository) {
    val coroutineScope = rememberCoroutineScope()

    // State to hold selected filter
    var selectedFilter by remember { mutableStateOf("Last Week") }

    // State to hold water intake data
    var waterIntakeData by remember { mutableStateOf<List<WaterIntake>>(emptyList()) }

    // Fetch water intake data based on the selected filter
    LaunchedEffect(selectedFilter) {
        val (startDate, endDate) = getDateRangeForFilter(selectedFilter)
        waterIntakeRepository.getWaterIntakesBetween(startDate, endDate).collectLatest { data ->
            waterIntakeData = data
        }
    }

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
                    label = { Text(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph Section
        if (waterIntakeData.isNotEmpty()) {
            WaterIntakeGraph(data = waterIntakeData)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No data available for the selected period.")
            }
        }
    }
}

// Helper function to calculate the date range based on the filter
fun getDateRangeForFilter(filter: String): Pair<Date, Date> {
    val calendar = Calendar.getInstance()
    val endDate = calendar.time

    when (filter) {
        "Last Week" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
        "Last Month" -> calendar.add(Calendar.MONTH, -1)
        "Last Year" -> calendar.add(Calendar.YEAR, -1)
    }

    val startDate = calendar.time
    return Pair(startDate, endDate)
}

// Sample implementation of the graph
@Composable
fun WaterIntakeGraph(data: List<WaterIntake>) {
    val dailyIntakes: Map<String, Float> =
        data.groupBy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.date) }
            .mapValues { entry -> entry.value.sumOf { it.intakeAmount.toDouble() }.toFloat() }

    val dates = dailyIntakes.keys.sorted()
    val intakes = dates.map { dailyIntakes[it] ?: 0f }

    // Replace with your graph library of choice or custom graph implementation
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Daily Water Intake",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Example placeholder for a graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Text("Graph Placeholder", Modifier.align(Alignment.Center))
        }
    }
}
