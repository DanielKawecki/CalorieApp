package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val calorieBudget = prefs.getInt("budget", -1)
    val protein = prefs.getInt("protein", -1)
    val fats = prefs.getInt("fats", -1)
    val carbs = prefs.getInt("carbs", -1)

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val products by viewModel.todayProductState.collectAsStateWithLifecycle()
    val total by viewModel.nutrientsSum.collectAsStateWithLifecycle()

    var showMessage by rememberSaveable { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


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
                NutritionIndicator(
                    "Kcal",
                    "kcal",
                    total.calorie,
                    calorieBudget,
                    Color(238, 137, 237, 255)
                )
                NutritionIndicator(
                    "Protein",
                    "g",
                    total.protein.toInt(),
                    protein,
                    Color(152, 218, 254, 255)
                )
                NutritionIndicator("Fats", "g", total.fats.toInt(), fats, Color(227, 211, 152, 255))
                NutritionIndicator(
                    "Carbs",
                    "g",
                    total.carbs.toInt(),
                    carbs,
                    Color(168, 158, 224, 255)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            val sections = listOf("Breakfast", "Lunch", "Dinner")

            LazyColumn {
                items(sections.size) { index ->
                    val title = sections[index]
                    val sectionProducts = products.filter { it.meal == title }

                    MealSection(
                        title,
                        sectionProducts,
                        viewModel,
                        navController
                    ) { navController.navigate(Screens.Add.route + "/$title") }
                }
            }
        }
        if (showMessage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFFB9D8B1), RoundedCornerShape(8.dp))
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://nutritionsource.hsph.harvard.edu/healthy-eating-plate/")
                        )
                        context.startActivity(intent)
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't know how to eat healthy? Click here",
                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                    fontSize = 14.sp
                )
                IconButton(
                    onClick = { showMessage = false },
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {}
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
//    HomeScreen()
}