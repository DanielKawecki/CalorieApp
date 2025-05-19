package pl.edu.uwr.pam.calorieapp

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

//@Composable
//fun NumberInputField(label: String, state: MutableState<String>) {
//    OutlinedTextField(
//        value = state.value,
//        onValueChange = { input ->
//            if (input.all { it.isDigit() } && input.length <= if (label == "YYYY") 4 else 2)
//                state.value = input
//        },
//        label = { Text(label) },
//        modifier = Modifier.width(if (label == "YYYY") 120.dp else 80.dp).padding(4.dp),
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//        singleLine = true
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {

    val context = LocalContext.current

    var height by rememberSaveable { mutableStateOf("") }
    var bodyMass by rememberSaveable { mutableStateOf("") }

    val day = rememberSaveable { mutableStateOf("") }
    val month = rememberSaveable { mutableStateOf("") }
    val year = rememberSaveable { mutableStateOf("") }

    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    val sex = listOf("Male", "Female")
    val selectedSex = rememberSaveable() { mutableStateOf("") }

    val activity = listOf("None", "Low", "Medium", "Athletic")
    val selectedActivity = rememberSaveable { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        ScreenTitle("Welcome")

        Text(text = "Gender", fontSize = 20.sp, modifier = Modifier.padding(4.dp))
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

        CustomNumberField("Height", height, true) { newVal -> height = newVal }
        CustomNumberField("Body Mass", bodyMass, true) { newVal -> bodyMass = newVal }

        Text(text = "Birth Date", fontSize = 20.sp, modifier = Modifier.padding(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomDateField("DD", day)
            Spacer(Modifier.width(4.dp))
            CustomDateField("MM", month)
            Spacer(Modifier.width(4.dp))
            CustomDateField("YYYY", year)
        }

        Text(text = "Activity Level", fontSize = 20.sp, modifier = Modifier.padding(4.dp))
        activity.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedActivity.value == option,
                    onClick = {
                        selectedActivity.value = option
                        prefs.edit().putString("activity", option).apply()
                    },
                    modifier = Modifier.height(40.dp),
                    colors = RadioButtonDefaults.colors(selectedColor = Color.DarkGray)
                )
                Text(option)
            }
        }

        CustomButton("Continue") {
            val dateStr = "${day.value}/${month.value}/${year.value}"
            prefs.edit()
                .putString("selected_date", dateStr)
                .putString("height", height)
                .putString("body_mass", bodyMass)
                .putBoolean("setup_completed", true)
                .apply()
            navController.navigate(Screens.Home.route)
        }
    }
}