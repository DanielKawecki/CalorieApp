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
    val unit = rememberSaveable { mutableStateOf(amountUnit) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val foods = rememberSaveable { loadFoods(context) }

    Column {

//        Text(text = "Edit", fontSize = 20.sp)
        ScreenTitle("Edit")

//        CustomTextField("Name", productName, showErrors) { newVal -> productName = newVal }
        FoodInputField(
            value = productName,
            onValueChange = { productName = it },
            foodList = foods,
            showError = showErrors
        )
//        TextField(
//            value = productName,
//            onValueChange = { productName = it },
//            label = { Text("Name") }
//        )

        Row() {
            CustomNumberField("Amount of Product", amount, showErrors, false) { newText -> amount = newText }
//            UnitDropdownList(listOf("g", "ml"), unit) { selected -> unit = selected }
            CustomUnitField("Unit", unit, showErrors)
        }

        CustomButton("Update") {
            if (productName == "" || amount == "") {
                showErrors = true
            }
            else {
                viewModel.updateProductById(idArg!!.toInt(), productName, amount + unit.value)
                navController.navigate(Screens.Home.route)
            }
        }
//        Button(
//            onClick = {
//                println("Works: $idArg, $productName, $amount")
//                viewModel.updateProductById(idArg!!.toInt(), productName, amount + unit)
//                navController.navigate(Screens.Home.route)
//            }
//        ) {
//            Text(
//                fontSize = 20.sp,
//                text = "Update"
//            )
//        }
    }
}