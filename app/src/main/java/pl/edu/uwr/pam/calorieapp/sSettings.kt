package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    var checked by rememberSaveable { mutableStateOf(prefs.getBoolean("notifications_enabled", true)) }
    var name by rememberSaveable { mutableStateOf(prefs.getString("user_name", "Daniel")) }
    var height by rememberSaveable { mutableStateOf(prefs.getString("user_height", "176")) }

    Column {

        ScreenTitle("Settings")

        CustomTextField("Name", name!!) {
            newVal -> name = newVal
            prefs.edit().putString("user_name", newVal).apply()
        }
        CustomNumberField("Height", height!!, true) {
            newVal -> height = newVal
            prefs.edit().putString("user_height", newVal).apply()
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1.0f),
                text = "Notifications",
                fontSize = 20.sp
            )
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    prefs.edit().putBoolean("notifications_enabled", it).apply()
                }
            )
        }
        DebugNotificationButton()

        Button(
            onClick = {
                viewModel.addSampleMass(60.0, "2025-04-10 12:00:00")
                viewModel.addSampleMass(63.0, "2025-04-13 12:15:00")
                viewModel.addSampleMass(62.0, "2025-04-20 12:30:00")
                viewModel.addSampleMass(63.5, "2025-04-24 12:30:00")
                viewModel.addSampleMass(64.4, "2025-04-31 12:30:00")
                viewModel.addSampleMass(63.3, "2025-05-01 12:30:00")
                viewModel.addSampleMass(65.0, "2025-05-02 12:30:00")
                viewModel.addSampleMass(64.4, "2025-05-03 12:30:00")

                viewModel.addSampleProduct(2300, "2025-04-10 12:00:00")
                viewModel.addSampleProduct(2370, "2025-04-13 12:15:00")
                viewModel.addSampleProduct(2137, "2025-04-20 12:30:00")
                viewModel.addSampleProduct(2380, "2025-04-24 12:30:00")
                viewModel.addSampleProduct(2450, "2025-04-31 12:30:00")
                viewModel.addSampleProduct(2390, "2025-05-01 12:30:00")
                viewModel.addSampleProduct(2420, "2025-05-02 12:30:00")
                viewModel.addSampleProduct(2460, "2025-05-03 12:30:00")
            }
        ) {
            Text(text = "Insert Sample Data", fontSize = 20.sp)
        }

        Button(
            onClick = {
                viewModel.clearAllProducts()
                viewModel.clearAllMass()
            }
        ) {
            Text(text = "Clear All Data", fontSize = 20.sp)
        }



    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen()
}