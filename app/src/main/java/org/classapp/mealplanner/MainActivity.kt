package org.classapp.mealplanner

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.classapp.mealplanner.ui.theme.MealPlannerTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        setContent {
            MealPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentUser != null) {
                        HomeScreenWithNavBar(context = this)
                    } else {
                        navigateToLogin()
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        // Redirect to login activity
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}
@Composable
fun HomeScreenWithNavBar(context: Context) {
    val navController = rememberNavController()
    var navSelectedItem by remember {
        mutableStateOf(0)
    }
    Scaffold ( bottomBar =  {
        NavigationBar {
            NavItemInfo().getAllNavItems().forEachIndexed { index, itemInfo ->
                NavigationBarItem(
                    selected = (index==navSelectedItem),
                    onClick = {
                        navSelectedItem = index
                        navController.navigate(itemInfo.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = itemInfo.icon, contentDescription = itemInfo.label
                        )
                    },
                    label = { Text(text = itemInfo.label) })
            }
        }
    }
    ) { paddingValues ->
        NavHost(navController = navController,
            startDestination = DestinationScreen.Home.route,
            modifier = Modifier.padding(paddingValues)) {
            // Nav builder
            composable( route = DestinationScreen.Home.route ) {
                HomeScreen()
            }
            composable( route = DestinationScreen.Add.route ) {
                AddScreen()
            }
            composable( route = DestinationScreen.Profile.route ) {
                ProfileScreen(context = context)
            }
        }
    }
}

