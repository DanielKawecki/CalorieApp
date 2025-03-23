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
import androidx.navigation.NavHostController

@Composable
fun AddScreen(meal: String?, navController: NavHostController) {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val meals by viewModel.customMeals.collectAsStateWithLifecycle()
    val mealProducts by viewModel.mealDetails.collectAsStateWithLifecycle()

    var productName by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }

    Column {
        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Name") }
        )

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") }
        )

        Button(
            onClick = {
                if (meal != null) viewModel.addWithNutrition(productName, amount, meal)
                navController.navigate(Screens.Home.route)
            }
        ) {
            Text(
                fontSize = 20.sp,
                text = "Add"
            )
        }

        Text(text = "Or add by choosing from the list of custom recipes", fontSize = 16.sp)

        LazyColumn {
            items(meals.size) { index ->
                Row {
                    Text(
                        modifier = Modifier.clickable {
                            viewModel.addMealToProducts(meals[index].idm, meal!!)
                            navController.navigate(Screens.Home.route)
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