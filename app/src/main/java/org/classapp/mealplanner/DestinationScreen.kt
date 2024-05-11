package org.classapp.mealplanner

sealed class DestinationScreen (val route : String) {
    object Home : DestinationScreen("home")
    object  Add : DestinationScreen("add")
    object  Profile : DestinationScreen("profile")
}