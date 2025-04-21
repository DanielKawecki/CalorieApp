package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale

fun parseAndFormatSqlDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault()) // e.g. "21 Apr"

    return try {
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "" // fallback if parsing fails
    }
}

@Composable
fun RoomTestScreen() {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val masses by viewModel.massesState.collectAsStateWithLifecycle()

    var bodyMass by rememberSaveable { mutableStateOf("") }

    Column {
        ScreenTitle("Measurements")

        AndroidView(factory = { context ->
            LineChart(context).apply {
                // 1. Data points setup
                val entries = masses.mapIndexed { index, mass -> Entry(
                    index.toFloat(),
                    mass.value.toFloat()
                )}

                val dateLabels = masses.map { parseAndFormatSqlDate(it.date) }

                val dataSet = LineDataSet(entries, "Sample Data").apply {
                    color = Color.RED // Line color (modern purple)
                    valueTextColor = Color.BLUE // Color of value labels
                    lineWidth = 2f // Line width
                    circleRadius = 4f // Size of circles on data points
                    setDrawFilled(true) // Fill area under the curve
                    fillColor = Color.WHITE // Fill color (lighter shade of purple)
                    mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth cubic bezier curve
                    setDrawValues(false) // Hide the values at data points
                    circleColors = listOf(Color.RED, Color.BLUE)
                }

                // 2. Set the data for the chart
                data = LineData(dataSet)

                xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.BLACK
                    textSize = 12f
                    granularity = 1f
                    isGranularityEnabled = true
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return dateLabels.getOrNull(index) ?: ""
                        }
                    }
                }

                // 3. Styling the chart
                description.text = "Sample Line Chart"
                setBackgroundColor(Color.WHITE) // White background

                // 4. Customizing X and Y Axes
                xAxis.apply {
                    setDrawGridLines(false) // No vertical grid lines
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.BLACK
                    textSize = 12f
                }

                axisLeft.apply {
                    setDrawGridLines(false) // No horizontal grid lines
                    textColor = Color.BLACK
                    textSize = 12f
                }

                axisRight.isEnabled = false // Disable right Y axis

                // 5. Disable the legend for simplicity
                legend.isEnabled = false

                // 6. Touch and scaling enabled for interactivity
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
            }
        },
            update = { chart ->
                val entries = masses.mapIndexed { index, mass ->
                    Entry(index.toFloat(), mass.value.toFloat())
                }
                val dateLabels = masses.map { parseAndFormatSqlDate(it.date) }

                val dataSet = LineDataSet(entries, "Mass over Time").apply {
                    color = Color.RED
                    valueTextColor = Color.BLUE
                    lineWidth = 3f
                    circleRadius = 6f
                    setDrawFilled(true)
                    fillColor = Color.TRANSPARENT
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawValues(false)
                }

                chart.data = LineData(dataSet)

                chart.xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.BLACK
                    textSize = 12f
                    granularity = 1f
                    isGranularityEnabled = true
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return dateLabels.getOrNull(index) ?: ""
                        }
                    }
                }
                chart.invalidate() // Refresh the chart
            },
            modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Height of the chart
        )

        CustomNumberField("Body Mass", bodyMass) { newValue -> bodyMass = newValue }
        CustomButton("Add Mass") {
            viewModel.addMass(bodyMass.toDouble())
            bodyMass = ""
        }
    }
}
