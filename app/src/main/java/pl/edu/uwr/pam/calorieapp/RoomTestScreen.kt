package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val products by viewModel.productState.collectAsStateWithLifecycle()

    Column () {
        Button(
            onClick = { viewModel.addWithNutrition("Potatoes", "136g", "Breakfast", Date(System.currentTimeMillis())) }
        ) {
            Text("Add new")
        }

        Button(
            onClick = { viewModel.clearProducts() }
        ) {
            Text("Clear")
        }

        for (product in products) {

            ProductEntry(
                product = product,
                onDelete = { viewModel.deleteProductById(product.id) }
            )
        }
    }

}