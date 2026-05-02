package com.example.childshield.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.childshield.screens.dashboard.DashboardScreen
import com.example.childshield.screens.login.LoginScreen
import com.example.childshield.screens.reports.AddReportScreen
import com.example.childshield.screens.splashscreens.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Splash.path
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Splash.path) {
            SplashScreen()
        }
        composable(Route.Login.path) {
            LoginScreen()
        }
        composable(Route.Dashboard.path) {
            DashboardScreen()
        }
        composable(Route.AddReport.path) {
            AddReportScreen()
        }
        // Add other routes here
    }
}
