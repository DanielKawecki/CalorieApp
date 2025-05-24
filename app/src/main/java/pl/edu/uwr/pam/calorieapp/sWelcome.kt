package pl.edu.uwr.pam.calorieapp

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun WelcomeScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    var height by rememberSaveable { mutableStateOf("") }
    var bodyMass by rememberSaveable { mutableStateOf("") }

    val day = rememberSaveable { mutableStateOf("") }
    val month = rememberSaveable { mutableStateOf("") }
    val year = rememberSaveable { mutableStateOf("") }

    val sex = listOf("Male", "Female")
    val selectedSex = rememberSaveable { mutableStateOf("") }

    val activity = listOf("Sedentary", "Low", "Medium", "Athletic")
    val selectedActivity = rememberSaveable { mutableIntStateOf(-1) }

    Column(Modifier.padding(16.dp)) {

        ScreenTitle("Welcome")

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

        CustomNumberField("Height", height, true) { newVal -> height = newVal }
        CustomNumberField("Body Mass", bodyMass, true) { newVal -> bodyMass = newVal }

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

        CustomButton("Continue") {
            val dateStr = "${day.value}/${month.value}/${year.value}"

            val selectedDate = LocalDate.of(year.value.toInt(), month.value.toInt(), day.value.toInt())
            val today = LocalDate.now()
            val age = ChronoUnit.YEARS.between(selectedDate, today)

            val budget = calculateCalorieBudget(
                    selectedSex.value,
                    height.toDouble(),
                    bodyMass.toDouble(),
                    age.toInt(),
                    selectedActivity.intValue + 1
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

fun calculateCalorieBudget(
    sex: String,
    heightCm: Double,
    weightKg: Double,
    age: Int,
    activityLevel: Int
): Int {
    val bmr = if (sex.lowercase() == "male") {
        10 * weightKg + 6.25 * heightCm - 5 * age + 5
    } else {
        10 * weightKg + 6.25 * heightCm - 5 * age - 161
    }

    val activityMultiplier = when (activityLevel) {
        1 -> 1.2
        2 -> 1.4
        3 -> 1.6
        4 -> 1.8
        else -> 1.2
    }

    return (bmr * activityMultiplier).toInt()
}

data class Macronutrients(val proteinGrams: Int, val fatGrams: Int, val carbGrams: Int)

fun calculateMacros(calorieBudget: Int): Macronutrients {
    val proteinCalories = calorieBudget * 0.20
    val fatCalories = calorieBudget * 0.25
    val carbCalories = calorieBudget * 0.55

    val proteinGrams = (proteinCalories / 4).toInt()
    val fatGrams = (fatCalories / 9).toInt()
    val carbGrams = (carbCalories / 4).toInt()

    return Macronutrients(proteinGrams, fatGrams, carbGrams)
}