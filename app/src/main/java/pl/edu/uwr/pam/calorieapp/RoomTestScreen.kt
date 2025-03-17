package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import java.sql.Date

@Composable
fun RoomTestScreen() {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val products by viewModel.todayProductState.collectAsStateWithLifecycle()
    val breakfastProducts = products.filter { it.meal == "Breakfast" }
    val launchProducts = products.filter { it.meal == "Launch" }
    val dinnerProducts = products.filter { it.meal == "Dinner" }

    var productName by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var meal by rememberSaveable { mutableStateOf("") }

    Column () {

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

        TextField(
            value = meal,
            onValueChange = { meal = it },
            label = { Text("Meal") }
        )

        Button(
            onClick = { viewModel.addWithNutrition(productName, amount, meal, Date(System.currentTimeMillis())) }
        ) {
            Text("Add new")
        }

        Button(
            onClick = { viewModel.clearProducts() }
        ) {
            Text("Clear")
        }

        Text("Breakfast")
        for (product in breakfastProducts) {
            ProductEntry(
                product = product,
                onDelete = { viewModel.deleteProductById(product.id) }
            )
        }

        Text("Launch")
        for (product in launchProducts) {
            ProductEntry(
                product = product,
                onDelete = { viewModel.deleteProductById(product.id) }
            )
        }

        Text("Dinner")
        for (product in dinnerProducts) {
            ProductEntry(
                product = product,
                onDelete = { viewModel.deleteProductById(product.id) }
            )
        }
    }
}