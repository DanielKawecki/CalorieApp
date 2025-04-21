//package pl.edu.uwr.pam.calorieapp
//
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.tooling.preview.Preview
//import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
//import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
//import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
//import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
//import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
//import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
//import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
//import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
//import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
//
//data class Point(val x: Float, val y: Float)
//
//fun calculateTrendLine(points: List<Point>): List<Point> {
//    val n = points.size
//    val sumX = points.sumOf { it.x.toDouble() }
//    val sumY = points.sumOf { it.y.toDouble() }
//    val sumXY = points.sumOf { it.x * it.y.toDouble() }
//    val sumX2 = points.sumOf { it.x * it.x.toDouble() }
//
//    val slope = ((n * sumXY) - (sumX * sumY)) / ((n * sumX2) - (sumX * sumX))
//    val intercept = (sumY - slope * sumX) / n
//
//    val xMin = points.minOf { it.x }
//    val xMax = points.maxOf { it.x }
//
//    return listOf(
//        Point(xMin, (slope * xMin + intercept).toFloat()),
//        Point(xMax, (slope * xMax + intercept).toFloat())
//    )
//}
//
//@Composable
//fun MealsTestScreen() {
//
////    val viewModel: ProductViewModel = viewModel(
////        LocalViewModelStoreOwner.current!!,
////        "ProductViewModel",
////        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
////    )
////    val meals by viewModel.customMeals.collectAsStateWithLifecycle()
////    val mealDetails by viewModel.mealDetails.collectAsStateWithLifecycle()
////
////    var mealName by rememberSaveable { mutableStateOf("") }
////    var productName by rememberSaveable { mutableStateOf("") }
////    var productAmount by rememberSaveable { mutableStateOf("") }
////    var mealId by rememberSaveable { mutableStateOf("") }
//
//    val modelProducer = remember { CartesianChartModelProducer() }
//    LaunchedEffect(Unit) {
//        modelProducer.runTransaction {
//            columnSeries { series(5, 6, 5, 2, 11, 8, 5, 2, 15, 11, 8, 13, 12, 10, 2, 7) }
//        }
//    }
//    CartesianChartHost(
//        rememberCartesianChart(
//            rememberColumnCartesianLayer(),
//            startAxis = VerticalAxis.rememberStart(),
//            bottomAxis = HorizontalAxis.rememberBottom(),
//        ),
//        modelProducer,
//    )
//}
//
//@Preview
//@Composable
//fun MealsTestScreenPreview() {
//    MaterialTheme {
//        MealsTestScreen()
//    }
//}