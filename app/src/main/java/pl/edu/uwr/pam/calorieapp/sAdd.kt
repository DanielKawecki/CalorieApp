package pl.edu.uwr.pam.calorieapp

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun Search(meal: String?, viewModel: ProductViewModel, navController: NavHostController) {
    var productName by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf("g") }

    Column (modifier = Modifier.fillMaxSize()) {
        CustomTextField("Product Name", productName) { newText -> productName = newText }

        Row() {
            CustomNumberField("Amount of Product", amount, false) { newText -> amount = newText }
            UnitDropdownList(listOf("g", "ml"), "g") { selected -> unit = selected }
        }

        CustomButton("Add Product") {
            if (meal != null) viewModel.addWithNutrition(productName, amount + unit, meal)
            navController.navigate(Screens.Home.route)
        }
    }
}

@Composable
fun ChooseRecipe(meal: String?, viewModel: ProductViewModel, navController: NavHostController) {

    val meals by viewModel.customMeals.collectAsStateWithLifecycle()

    Text(
        modifier = Modifier.padding(8.dp),
        text = "Your Recipes",
        fontSize = 16.sp
    )

    LazyColumn {
        items(meals.size) { index ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .background(Color(240, 240, 240))
                    .clickable {
                        viewModel.addMealToProducts(meals[index].idm, meal!!)
                        navController.navigate(Screens.Home.route)
                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 30.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
                    text = meals[index].name,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(meal: String?, navController: NavHostController) {

    val viewModel: ProductViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "ProductViewModel",
        ProductViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Search", "Recipes")

    Column {

        ScreenTitle("Add Product")

        Column(modifier = Modifier.fillMaxWidth()) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            AnimatedContent(
                targetState = tabIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    } else {
                        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    }
                },
                label = "Tab slide animation"
            ) { currentTab ->
                when (currentTab) {
                    0 -> Search(meal, viewModel, navController)
                    1 -> ChooseRecipe(meal, viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun ListOfMeals(meals: List<Meal>) {

    LazyColumn {
        items(meals.size) { index ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .background(Color(240, 240, 240))
                    .clickable {

                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 30.dp, top = 15.dp, end = 15.dp, bottom = 15.dp)
                        .clickable {
                        //viewModel.addMealToProducts(meals[index].idm, meal!!)
                        //navController.navigate(Screens.Home.route)
                    },
                    text = meals[index].name,
                    fontSize = 18.sp
                )
            }
        }
    }
}