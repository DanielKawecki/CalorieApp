package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun MealsScreen(navController: NavController) {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val meals by viewModel.customMeals.collectAsStateWithLifecycle()

    var mealName by rememberSaveable { mutableStateOf("") }
//    var mealDetailCount by rememberSaveable { mutableStateOf("Loading...") }

    Column {

        ScreenTitle("Recipes")
        CustomTextField("Recipe Name", mealName) { newText -> mealName = newText }
        CustomButton("Add New Recipe") {
            viewModel.addCustomMeal(mealName)
            mealName = ""
        }

        LazyColumn {
            items(meals.size) { index ->

                Column(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .background(Color(240, 240, 240))
                        .height(60.dp)
                        .clickable { navController.navigate(Screens.MealDetails.route + "/${meals[index].idm}/${meals[index].name}") }
                ) {
                    Row(
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .weight(1.0f),
                            text = meals[index].name,
                            fontSize = 18.sp,
                        )
//                        Text(
//                            modifier = Modifier
//                                .padding(start = 15.dp)
//                                .weight(1.0f),
//                            text = mealDetailCount,
//                            fontSize = 18.sp,
//                        )

                        Icon(
                            modifier = Modifier.padding(end = 15.dp)
                                .clickable { viewModel.deleteCustomMealById(meals[index].idm) },
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealDetailsScreen(idm: String?, mealArg: String?, navController: NavController) {

    val mealID = idm!!.toInt()

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val mealDetails by viewModel.mealDetails.collectAsStateWithLifecycle()
    viewModel.getMealDetail(mealID)

    var mealName by rememberSaveable { mutableStateOf(mealArg!!) }
    var productName by rememberSaveable { mutableStateOf("") }
    var productAmount by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf("g") }

    Column {

        ScreenTitle("Meal Details")

        CustomTextField("Meal Name", mealName) {
            newText -> mealName = newText
            viewModel.updateMealByID(mealID, mealName)
        }

        CustomTextField("Product Name", productName) { newText -> productName = newText }
        Row() {
            CustomNumberField("Amount of Product", productAmount, false) { newText ->
                productAmount = newText
            }
            UnitDropdownList(listOf("g", "ml"), unit) { selected -> unit = selected }
        }
        CustomButton("Add Product") {
            viewModel.addMealDetail(
                mealID,
                productName,
                productAmount + unit
            )
            productName = ""
            productAmount = ""
            unit = "g"
        }

        LazyColumn {
            items(mealDetails.size) { index ->

                DetailEntry(
                    detail = mealDetails[index],
                    onEdit = {},
                    onDelete = { viewModel.deleteMealDetailById(mealDetails[index].idd) }
                )
            }
        }
    }
}