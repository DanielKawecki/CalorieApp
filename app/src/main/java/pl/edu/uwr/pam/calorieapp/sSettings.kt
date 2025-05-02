package pl.edu.uwr.pam.calorieapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    var checked by rememberSaveable { mutableStateOf(true) }
    var name by rememberSaveable { mutableStateOf("Daniel") }
    var height by rememberSaveable { mutableStateOf("176") }

    Column {

        ScreenTitle("Settings")

        CustomTextField("Name", name) { newVal -> name = newVal }
        CustomNumberField("Height", height, true) { newVal -> height = newVal }

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
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen()
}