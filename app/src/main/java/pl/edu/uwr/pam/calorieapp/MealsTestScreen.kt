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

    Column () {

        Text(text = "All Custom Meals:", fontSize = 20.sp)

        TextField(
            value = mealName,
            onValueChange = { mealName = it },
            label = { Text("Name") }
        )

        Button(
            onClick = { viewModel.addCustomMeal(mealName) }
        ) {
            Text("Add new")
        }

//        Button(
//            onClick = { viewModel.dele }
//        ) {
//            Text("Clear")
//        }

        for (meal in meals) Text(text = "${meal.idm} ${meal.name}", fontSize = 20.sp)
    }
}