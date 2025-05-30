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
import androidx.compose.ui.unit.sp
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

fun formatXAxis(xAxis: XAxis) {
    xAxis.apply {
        setDrawGridLines(true)
        position = XAxis.XAxisPosition.BOTTOM
        textColor = Color.BLACK
        textSize = 12f
        granularity = 24 * 60 * 60 * 1000f
        isGranularityEnabled = true
        gridColor = Color.LTGRAY
        gridLineWidth = 2f
        valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                return dateFormat.format(value.toLong())
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

    val numerator = entries.sumOf { (it.x - xMean) * (it.y - yMean) }
    val denominator = entries.sumOf { (it.x - xMean).pow(2) }

    val slope = numerator / denominator
    val intercept = yMean - slope * xMean

    val oneDayMillis = 24 * 60 * 60 * 1000f
    val firstX = entries.minOf { it.x }
    val lastX = entries.maxOf { it.x }
    val futureEnd = lastX + daysAhead * oneDayMillis

    val step = oneDayMillis
    val totalRange = generateSequence(firstX) { it + step }
        .takeWhile { it <= futureEnd }
        .toList()

    return totalRange.map { x ->
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
    val calorieByDate by viewModel.calorieSumByDate.collectAsStateWithLifecycle()
    var showErrors by rememberSaveable { mutableStateOf(false) }

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
                    description.text = "Body Mass"
                    description.textSize = 14f
                }
            },
            update = { chart ->

                val entries = masses.map { mass ->
                    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .parse(mass.date)?.time?.toFloat() ?: 0f
                    Entry(timestamp, mass.value.toFloat())
                }

                val massDataSet = createMassDataSet(entries)

                val trendEntries = trendLine(entries, 5)
                val trendDataSet = LineDataSet(trendEntries, "Trend").apply {
                    color = Color.GRAY
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                    enableDashedLine(10f, 5f, 0f)
                }

                chart.data = LineData(massDataSet, trendDataSet)
                formatXAxis(chart.xAxis)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp)
                .padding(horizontal = 8.dp, vertical = 15.dp)
        )

//        for (item in calorieByDate) {
//            Text(text = "Date: ${item.date}, Value: ${item.calorie}", fontSize = 20.sp)
//        }
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
                    description.text = "Calorie"
                    description.textSize = 14f
                }
            },
            update = { chart ->
                val entries = calorieByDate.mapNotNull { sum ->
                    val timestamp = try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(sum.date)?.time?.toFloat()
                    } catch (e: Exception) {
                        null
                    }

                    timestamp?.let { Entry(it, sum.calorie.toFloat()) }
                }

                val calorieDataSet = createMassDataSet(entries).apply {
                    color = Color.argb(255, 255,  102, 0)
                }

                chart.data = LineData(calorieDataSet)
                formatXAxis(chart.xAxis)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp)
                .padding(horizontal = 8.dp, vertical = 15.dp)
        )

        CustomNumberField("Body Mass", bodyMass, showErrors, true) { newValue -> bodyMass = newValue }
        CustomButton("Save") {
            if (todayMass == null) {
                if (bodyMass != "") {
                    viewModel.addMass(bodyMass.toDouble())
                    showErrors = false
                }
                else {
                    showErrors = true
                }
            }

            else {
                if (bodyMass != "") {
                    viewModel.updateMassById(todayMass.id, bodyMass.toDouble())
                    showErrors = false
                }
                else {
                    showErrors = true
                }
            }
        }
    }
}