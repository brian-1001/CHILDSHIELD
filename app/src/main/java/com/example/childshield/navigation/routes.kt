package com.example.childshield.navigation

sealed class Route(val path: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Register : Route("register")
    object Dashboard : Route("dashboard")
    object AddReport : Route("add_report")
    object ReportList : Route("report_list")
    object Profile : Route("profile")
    object Settings : Route("settings")
}
