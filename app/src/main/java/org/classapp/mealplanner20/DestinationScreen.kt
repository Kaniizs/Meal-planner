package org.classapp.mealplanner20

sealed class DestinationScreen (val route : String) {
    object Home : DestinationScreen("home")
    object  Add : DestinationScreen("add")
    object  Profile : DestinationScreen("profile")
}