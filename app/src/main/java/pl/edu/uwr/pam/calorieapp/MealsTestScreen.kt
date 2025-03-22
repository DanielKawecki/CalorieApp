package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MealsTestScreen() {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val meals by viewModel.customMeals.collectAsStateWithLifecycle()

    var mealName by rememberSaveable { mutableStateOf("") }
    var productName by rememberSaveable { mutableStateOf("") }
    var productAmount by rememberSaveable { mutableStateOf("") }
    var mealId by rememberSaveable { mutableStateOf("") }

    Column () {

        Text(text = "All Custom Meals:", fontSize = 20.sp)

        TextField(
            value = mealName,
            onValueChange = { mealName = it },
            label = { Text("Meal Name") }
        )

        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") }
        )

        TextField(
            value = productAmount,
            onValueChange = { productAmount = it },
            label = { Text("Product Amount") }
        )

        TextField(
            value = mealId,
            onValueChange = { mealId = it },
            label = { Text("Meal Id") }
        )

        Button(
            onClick = { viewModel.addCustomMeal(mealName) }
        ) {
            Text("Add Meal")
        }

        Button(
            onClick = { viewModel.addMealDetail(mealId.toInt(), productName, productAmount) }
        ) {
            Text("Add Meal Product")
        }

//        Button(
//            onClick = { viewModel.dele }
//        ) {
//            Text("Clear")
//        }

        for (meal in meals) Text(text = "${meal.idm} ${meal.name}", fontSize = 20.sp)
    }
}