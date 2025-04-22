package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun RoomTestScreen() {

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

        AndroidView(factory = { context ->
            LineChart(context).apply {
                // 1. Data points setup
                val entries = masses.mapIndexed { index, mass -> Entry(
                    index.toFloat(),
                    mass.value.toFloat()
                )}

                val dateLabels = masses.map { parseAndFormatSqlDate(it.date) }

                val dataSet = LineDataSet(entries, "Sample Data").apply {
                    color = Color.RED
                    valueTextColor = Color.BLUE
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawFilled(true)
                    fillColor = Color.WHITE
                    mode = LineDataSet.Mode.STEPPED
                    setDrawValues(false)
                    circleColors = listOf(Color.RED, Color.BLUE)
                }

                data = LineData(dataSet)

                xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.BLACK
                    textSize = 16f
                    granularity = 1f
                    isGranularityEnabled = true
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return dateLabels.getOrNull(index) ?: ""
                        }
                    }
                }

                description.text = "Sample Line Chart"
                setBackgroundColor(Color.WHITE)

                xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.BLACK
                    textSize = 12f
                }

                axisLeft.apply {
                    setDrawGridLines(false)
                    textColor = Color.BLACK
                    textSize = 12f
                }

                axisRight.isEnabled = false

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

//        CustomButton("Delete Last") {
//            viewModel.deleteMassById(masses.last().id)
//        }
    }
}
