package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlin.math.pow

fun parseAndFormatSqlDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        ""
    }
}

fun formatXAxis(xAxis: XAxis, dateLabels: List<String>) {
    xAxis.apply {
        setDrawGridLines(true)
        position = XAxis.XAxisPosition.BOTTOM
        textColor = Color.BLACK
        textSize = 12f
        granularity = 1f
        isGranularityEnabled = true
        gridColor = Color.LTGRAY
        gridLineWidth = 2f
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return dateLabels.getOrNull(index) ?: ""
            }
        }
    }
}

fun createMassDataSet(entries: List<Entry>): LineDataSet {
    return LineDataSet(entries, "Mass over Time").apply {
        color = Color.rgb(54,162,235)
        valueTextColor = Color.BLACK
        lineWidth = 3f
        setDrawFilled(true)
        fillColor = Color.WHITE
        mode = LineDataSet.Mode.LINEAR
        setDrawValues(false)
        setDrawCircles(false)
    }
}

fun trendLine(entries: List<Entry>, daysAhead: Int): List<Entry> {
    if (entries.size < 2) return emptyList()

    val n = entries.size
    val xMean = entries.sumOf { it.x.toDouble() } / n
    val yMean = entries.sumOf { it.y.toDouble() } / n

    val numerator = entries.sumOf { (it.x - xMean).toDouble() * (it.y - yMean).toDouble() }
    val denominator = entries.sumOf { (it.x - xMean).toDouble().pow(2) }

    val slope = numerator / denominator
    val intercept = yMean - slope * xMean

    val lastX = entries.maxOf { it.x }

    return (1..daysAhead).map { i ->
        val x = lastX + i
        val y = (slope * x + intercept).toFloat()
        Entry(x, y)
    }
}

@Composable
fun ChartScreen() {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val masses by viewModel.massesState.collectAsStateWithLifecycle()
    val todayMass by viewModel.todayMassState.collectAsStateWithLifecycle()

    var bodyMass by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(todayMass) {
        if (todayMass != null) {
            if (todayMass.value != -1.0)
                bodyMass = todayMass.value.toString()
        }
    }

    Column {

        ScreenTitle("Measurements")

        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    setBackgroundColor(Color.WHITE)
                    axisRight.isEnabled = false
                    axisLeft.apply {
                        setDrawGridLines(true)
                        textColor = Color.BLACK
                        textSize = 12f
                    }
                    legend.isEnabled = false
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

                chart.data = LineData(createMassDataSet(entries))
                formatXAxis(chart.xAxis, dateLabels)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 8.dp, vertical = 15.dp)
        )

        CustomNumberField("Body Mass", bodyMass) { newValue -> bodyMass = newValue }
        CustomButton("Save") {
            if (todayMass == null)
                viewModel.addMass(bodyMass.toDouble())
            else
                viewModel.updateMassById(todayMass.id, bodyMass.toDouble())
        }

        DebugNotificationButton()
    }
}