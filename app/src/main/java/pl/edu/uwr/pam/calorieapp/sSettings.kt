package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val sex = listOf("Male", "Female")
    val activity = listOf("Sedentary", "Low", "Medium", "Athletic")

    val date = prefs.getString("selected_date", "0/0/0")!!.split("/")
    val day = rememberSaveable { mutableStateOf(date[0]) }
    val month = rememberSaveable { mutableStateOf(date[1]) }
    val year = rememberSaveable { mutableStateOf(date[2]) }

    var checked by rememberSaveable { mutableStateOf(prefs.getBoolean("notifications_enabled", true)) }
    var height by rememberSaveable { mutableStateOf(prefs.getString("height", "error")) }
    var bodyMass by rememberSaveable { mutableStateOf(prefs.getString("body_mass", "error")) }
    val selectedSex = rememberSaveable { mutableStateOf(prefs.getString("sex", "error")) }
    val selectedActivity = rememberSaveable { mutableIntStateOf(prefs.getInt("activity", -1)) }

    Column(modifier = Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {

        ScreenTitle("Settings")

        Text(text = "Sex", fontSize = 20.sp, modifier = Modifier.padding(4.dp))
        Row {
            sex.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSex.value == option,
                        onClick = {
                            selectedSex.value = option
                            prefs.edit().putString("sex", option).apply()
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = Color.DarkGray)
                    )
                    Text(option)
                }
            }
        }

        CustomNumberField("Height", height!!, true) { newVal -> height = newVal }
        CustomNumberField("Body Mass", bodyMass!!, true) { newVal -> bodyMass = newVal }

        Text(text = "Date of Birth", fontSize = 20.sp, modifier = Modifier.padding(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomDateField("DD", day)
            Spacer(Modifier.width(4.dp))
            CustomDateField("MM", month)
            Spacer(Modifier.width(4.dp))
            CustomDateField("YYYY", year)
        }

        Text(text = "Activity Level", fontSize = 20.sp, modifier = Modifier.padding(4.dp))
        activity.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedActivity.intValue == index,
                    onClick = {
                        selectedActivity.intValue = index
                        prefs.edit().putInt("activity", index).apply()
                    },
                    modifier = Modifier.height(40.dp),
                    colors = RadioButtonDefaults.colors(selectedColor = Color.DarkGray)
                )
                Text(option)
            }
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

        CustomButton("Reload Calorie Budget") {
            val dateStr = "${day.value}/${month.value}/${year.value}"

            val selectedDate = LocalDate.of(year.value.toInt(), month.value.toInt(), day.value.toInt())
            val today = LocalDate.now()
            val age = ChronoUnit.YEARS.between(selectedDate, today)

            val budget = calculateCalorieBudget(
                selectedSex.value!!,
                height!!.toDouble(),
                bodyMass!!.toDouble(),
                age.toInt(),
                selectedActivity.intValue
            )

            val nutrients = calculateMacros(budget)

            prefs.edit()
                .putString("selected_date", dateStr)
                .putInt("age", age.toInt())
                .putInt("budget", budget)
                .putInt("protein", nutrients.proteinGrams)
                .putInt("fats", nutrients.fatGrams)
                .putInt("carbs", nutrients.carbGrams)
                .putString("height", height)
                .putString("body_mass", bodyMass)
                .putBoolean("setup_completed", true)
                .apply()
            navController.navigate(Screens.Home.route)
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun SettingsPreview() {
//    SettingsScreen()
//}