package pl.edu.uwr.pam.calorieapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ScreenTitle(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .padding(top = 30.dp),
        text = title,
        fontSize = 40.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray

    )
}

@Composable
fun CustomButton(content: String , function: () -> Unit) {

    Button(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color.White)
            .size(60.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp)),
        onClick = { function() },
        colors = ButtonColors(
            contentColor = Color.DarkGray,
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Red,
            disabledContentColor = Color.Red,
        )
    ) {
        Text(
            text = content,
            fontSize = 18.sp
        )
    }
}

@Composable
fun CustomTextField(
    label: String,
    content: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color.White)
            .size(60.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp)),
        value = content,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.DarkGray,
            unfocusedTextColor = Color.DarkGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.DarkGray
        ),
        textStyle = TextStyle(fontSize = 20.sp)
    )
}

@Composable
fun FAB(navController: NavHostController) {
    FloatingActionButton(
        onClick = {navController.navigate(Screens.Add.route)},
        containerColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.DarkGray
        )
    }
}

@Composable
fun NutritionIndicator(title: String, unit: String, amount: Int, total: Int, color: Color) {

    Column (
        modifier = Modifier
            .width(100.dp)
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
    ) {
        Text(
            text = "$title $amount",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            color = Color.Black)

        Text(text = "/$total $unit",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            color = Color.Gray)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 10.dp, end = 0.dp, bottom = 0.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
        ) {
            LinearProgressIndicator(
                progress = {
                    amount.toFloat() / total.toFloat()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = color,
                trackColor = Color.Transparent,
            )
        }
    }
}

@Composable
fun ProductEntry(product: Product, onDelete: () -> Unit) {

    Column (
        modifier = Modifier
            .padding(top = 5.dp)
            .background(Color(240, 240, 240))
    ) {
        Row (
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .weight(1.0f),
                text = product.name,
                fontSize = 18.sp,
            )
            Icon(
                modifier = Modifier.padding(end = 15.dp).clickable { onDelete() },
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }

        Row () {
            Text(
                modifier = Modifier.padding(start = 15.dp, bottom = 10.dp),
                text = "${product.calorie} kcal",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(start = 30.dp, bottom = 10.dp),
                text = "${product.protein} g",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(start = 30.dp, bottom = 10.dp),
                text = "${product.fats} g",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(start = 30.dp, bottom = 10.dp),
                text = "${product.carbs} g",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductEntryPreview() {
//    ProductEntry(Product(0, "Salt", "20g", "Dinner", 237, 0.3, 2.3, 67.0, "23"))
}

@Composable
fun MealSection(title: String, products: List<Product>, viewModel: ProductViewModel, onAdd: () -> Unit) {

    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Icon(
                modifier = Modifier.clickable { expanded = !expanded },
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )

            AddButton(onAdd)
        }

        if (expanded) {
            for (product in products) ProductEntry(product, { viewModel.deleteProductById(product.id) })
        }
    }
}

@Composable
fun AddButton(onAdd: () -> Unit) {
    OutlinedButton(
        onClick = { onAdd() },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Color.Transparent,

        )
    ) {
        Text("Add")
    }
}

@Preview(showBackground = true)
@Composable
fun AddButtonPreview() {
//    AddButton()
}