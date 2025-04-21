package pl.edu.uwr.pam.calorieapp

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomMenu(navController = navController)},
        content = { innerPadding ->
            BottomNavGraph(navController = navController, padding = innerPadding) },
    )
}

@Composable
fun BottomNavGraph(navController: NavHostController, padding: PaddingValues){
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(route = Screens.Home.route) { HomeScreen(navController) } //{ HomeScreen(navController) }
        composable(route = Screens.Meals.route) { MealsScreen(navController) }
        composable(route = Screens.History.route) { RoomTestScreen() }
        composable(route = Screens.Add.route + "/{meal}") {
            val meal = it.arguments?.getString("meal")
            AddScreen(meal, navController)
        }
        composable(route = Screens.Edit.route + "/{idArg}/{nameArg}/{amountArg}") {
            val id = it.arguments?.getString("idArg")
            val name = it.arguments?.getString("nameArg")
            val amount = it.arguments?.getString("amountArg")
            EditScreen(id, name, amount, navController)
        }
        composable(route = Screens.MealDetails.route + "/{idm}/{mealArg}") {
            val idm = it.arguments?.getString("idm")
            val mealArg = it.arguments?.getString("mealArg")
            MealDetailsScreen(idm, mealArg, navController)
        }
    }
}

@Composable
fun BottomMenu(navController: NavHostController){
    val screens = listOf(
        BottomBar.Home,  BottomBar.Recipes, BottomBar.History
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(elevation = 12.dp, shape = RectangleShape, spotColor = DefaultShadowColor)
    ){
        screens.forEach{screen ->
            NavigationBarItem(
                label = { Text(text = screen.title)},
                icon = {Icon(imageVector = screen.icon, contentDescription = "icon")},
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {navController.navigate(screen.route)},
                colors = NavigationBarItemColors(
                    selectedIconColor =  Color.DarkGray,//Color(88, 190, 249),
                    selectedTextColor = Color.DarkGray,//Color(88, 190, 249),
                    selectedIndicatorColor = Color(220, 220, 220, 0),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    disabledIconColor = Color.Red,
                    disabledTextColor = Color.Red
                )
            )
        }
    }
}
