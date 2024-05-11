package org.classapp.mealplanner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItemInfo(
    val label:String = "",
    val icon:ImageVector = Icons.Filled.Star,
    val route:String = "") {
    fun getAllNavItems() : List<NavItemInfo> {
        return  listOf(
            NavItemInfo("Home", Icons.Filled.Home, DestinationScreen.Home.route),
            NavItemInfo("Add", Icons.Filled.AddCircle, DestinationScreen.Add.route),
            NavItemInfo("Profile", Icons.Filled.Person, DestinationScreen.Profile.route)

        )
    }
}
