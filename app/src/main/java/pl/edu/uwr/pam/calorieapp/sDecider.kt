package pl.edu.uwr.pam.calorieapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun DeciderScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val setupDone = prefs.getBoolean("setup_completed", false)

    LaunchedEffect(Unit) {
        navController.popBackStack()
        if (setupDone) {
            navController.navigate(Screens.Home.route)
        } else {
            navController.navigate(Screens.Welcome.route)
        }
    }
}
