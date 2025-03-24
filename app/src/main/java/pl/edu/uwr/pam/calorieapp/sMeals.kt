package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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


    Column () {

        ScreenTitle("Recipes")
        CustomTextField("Recipe Name", mealName) { newText -> mealName = newText }
        CustomButton("Add New Recipe") { viewModel.addCustomMeal(mealName) }

        LazyColumn {
            items(meals.size) { index ->
                Row {
                    Text(
                        modifier = Modifier.clickable {
                            navController.navigate(Screens.MealDetails.route + "/${meals[index].idm}")
                        },
                        text = meals[index].name,
                        fontSize = 20.sp
                    )
                    Icon(
                        modifier = Modifier.padding(horizontal = 15.dp).clickable { viewModel.deleteCustomMealById(meals[index].idm) },
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun MealDetailsScreen(idm: String?, navController: NavController) {

    val mealID = idm!!.toInt()

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val mealDetails by viewModel.mealDetails.collectAsStateWithLifecycle()
    viewModel.getMealDetail(mealID)

    var productName by rememberSaveable { mutableStateOf("") }
    var productAmount by rememberSaveable { mutableStateOf("") }

    Column {

        ScreenTitle("Meal Details")

        CustomTextField("Product Name", productName) { newText -> productName = newText }
        CustomTextField("Product Amount", productAmount) { newText -> productAmount = newText }
        CustomButton("Add Product") { viewModel.addMealDetail(mealID, productName, productAmount) }

        LazyColumn {
            items(mealDetails.size) { index ->

                DetailEntry(
                    detail = mealDetails[index],
                    onEdit = {},
                    onDelete = { viewModel.deleteMealDetailById(mealDetails[index].idd) }
                )
//                Row {
//                    Text(
//                        modifier = Modifier.clickable {  },
//                        text = mealDetails[index].name,
//                        fontSize = 20.sp
//                    )
//                    Icon(
//                        modifier = Modifier.padding(horizontal = 15.dp).clickable { viewModel.deleteMealDetailById(mealDetails[index].idd) },
//                        imageVector = Icons.Default.Clear,
//                        contentDescription = null
//                    )
//                }
            }
        }
    }
}