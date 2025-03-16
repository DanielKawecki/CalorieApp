package pl.edu.uwr.pam.calorieapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewHome() {

    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ScreenTitle("Calorie App")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(240, 240, 240, 255))
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NutritionIndicator("Kcal", "kcal", 1567, 2516, Color(238,137,237,255))
            NutritionIndicator("Protein", "g", 58, 126, Color(152,218,254,255))
            NutritionIndicator("Fat", "g", 42, 84, Color(227,211,152,255))
            NutritionIndicator("Carbs", "g", 179, 324, Color(168,158,224,255))
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Breakfast",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

                Icon(
                    modifier = Modifier.clickable { expanded = !expanded },
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )

                Text("Add")

            }

            if (expanded) {

                val  products = listOf("Rice", "Chicken", "Pizza")

                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp, end = 0.dp, top = 10.dp, bottom = 0.dp)
                ) {
                    items(products.size) { index ->
                        val product = products[index]

                        ProductEntry(product)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NewHome()
}