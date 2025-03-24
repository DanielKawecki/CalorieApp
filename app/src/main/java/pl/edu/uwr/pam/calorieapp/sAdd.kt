package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(meal: String?, navController: NavHostController) {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val meals by viewModel.customMeals.collectAsStateWithLifecycle()

    val quantities = listOf("g", "ml", "piece")

    var productName by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(quantities[0]) }

    Column {

        ScreenTitle("Add Product")

        Text(
            modifier = Modifier.padding(),
            text = "Your Recipes",
            fontSize = 16.sp
        )

        LazyColumn {
            items(meals.size) { index ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .background(Color(240, 240, 240))
                        .clickable {
                            viewModel.addMealToProducts(meals[index].idm, meal!!)
                            navController.navigate(Screens.Home.route)
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 30.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
                        text = meals[index].name,
                        fontSize = 18.sp
                    )
                }
            }
        }

        CustomTextField("Product Name", productName) { newText -> productName = newText }

        Row() {
            CustomNumberField("Amount of Product", amount) { newText -> amount = newText }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = selected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = !expanded }) {
                    quantities.forEach { text ->
                        DropdownMenuItem(
                            text = { Text(text = text) },
                            onClick = {
                                selected = text
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }

        Text(text = selected, fontSize = 20.sp)

        CustomButton("Add Product") {
            if (meal != null) viewModel.addWithNutrition(productName, amount, meal)
            navController.navigate(Screens.Home.route)
        }
    }
}

@Composable
fun ListOfMeals(meals: List<Meal>) {
//    val meals = listOf(
//        Meal(0, "Oatmeal"),
//        Meal(1, "Beefcake"),
//        Meal(2, "Spaghetti")
//    )

    LazyColumn {
        items(meals.size) { index ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .background(Color(240, 240, 240))
                    .clickable {

                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 30.dp, top = 15.dp, end = 15.dp, bottom = 15.dp)
                        .clickable {
                        //viewModel.addMealToProducts(meals[index].idm, meal!!)
                        //navController.navigate(Screens.Home.route)
                    },
                    text = meals[index].name,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListOfMealsPrev() {
    val meals = listOf(
        Meal(0, "Oatmeal"),
        Meal(1, "Beefcake"),
        Meal(2, "Spaghetti")
    )
    ListOfMeals(meals)
}