package com.example.childshield.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.childshield.screens.dashboard.DashboardScreen
import com.example.childshield.screens.login.LoginScreen
import com.example.childshield.screens.profile.ProfileScreen
import com.example.childshield.screens.register.RegisterScreen
import com.example.childshield.screens.reports.AddReportScreen
import com.example.childshield.screens.reports.PosterScreen
import com.example.childshield.screens.reports.ReportListScreen
import com.example.childshield.screens.reports.UpdateReportScreen
import com.example.childshield.screens.settings.SettingsScreen
import com.example.childshield.screens.splashscreens.SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Splash.path
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable(Route.Login.path) {
            LoginScreen(navController)
        }
        composable(Route.Register.path) {
            RegisterScreen(navController)
        }
        composable(Route.Splash.path) {
            SplashScreen(navController)
        }
        composable(Route.Dashboard.path) {
            DashboardScreen(navController)
        }
        composable(Route.ReportList.path) {
            ReportListScreen(navController)
        }
        composable(Route.AddReport.path) {
            AddReportScreen(navController)
        }
        composable(
            route = Route.UpdateReport.path + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            UpdateReportScreen(navController, id)
        }
        composable(
            route = "poster/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            PosterScreen(navController, id)
        }
        composable(Route.Profile.path) {
            ProfileScreen(navController)
        }
        composable(Route.Settings.path) {
            SettingsScreen(navController)
        }
    }
}
