package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val products by viewModel.todayProductState.collectAsStateWithLifecycle()
    val total by viewModel.nutrientsSum.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ScreenTitle("Calorie App")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(240, 240, 240, 255))
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NutritionIndicator("Kcal", "kcal", total.calorie, 2516, Color(238,137,237,255))
            NutritionIndicator("Protein", "g", total.protein.toInt(), 126, Color(152,218,254,255))
            NutritionIndicator("Fats", "g", total.fats.toInt(), 84, Color(227,211,152,255))
            NutritionIndicator("Carbs", "g", total.carbs.toInt(), 324, Color(168,158,224,255))
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        val sections = listOf("Breakfast", "Lunch", "Dinner")

        LazyColumn {
            items(sections.size) { index ->
                val title = sections[index]
                val sectionProducts = products.filter { it.meal == title }

                MealSection(title, sectionProducts, viewModel, navController) { navController.navigate(Screens.Add.route + "/$title") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
//    HomeScreen()
}