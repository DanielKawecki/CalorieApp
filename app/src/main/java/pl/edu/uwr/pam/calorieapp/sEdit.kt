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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun EditScreen(idArg: String?, nameArg: String?, amountArg: String?, navController: NavController) {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    val amountNumber = amountArg!!.filter { it.isDigit() }
    val amountUnit = amountArg.filter { !it.isDigit() }

    var productName by rememberSaveable { mutableStateOf(nameArg!!) }
    var amount by rememberSaveable { mutableStateOf(amountNumber) }
    var unit by rememberSaveable { mutableStateOf(amountUnit) }

    Column {

        Text(text = "Edit", fontSize = 20.sp)

        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Name") }
        )

        Row() {
            CustomNumberField("Amount of Product", amount) { newText -> amount = newText }
            UnitDropdownList(listOf("g", "ml", "piece"), unit) { selected -> unit = selected }
        }

        Button(
            onClick = {
                println("Works: $idArg, $productName, $amount")
                viewModel.updateProductById(idArg!!.toInt(), productName, amount + unit)
                navController.navigate(Screens.Home.route)
            }
        ) {
            Text(
                fontSize = 20.sp,
                text = "Update"
            )
        }
    }
}