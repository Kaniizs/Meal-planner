package org.classapp.mealplanner20

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*

fun getFoodData(onDataLoaded: (List<Food>) -> Unit, onError: (DatabaseError) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("Foods")

    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val foodList = mutableListOf<Food>()
            for (childSnapshot in snapshot.children) {
                val food = childSnapshot.getValue(Food::class.java)
                food?.let {
                    foodList.add(it)
                }
            }
            onDataLoaded(foodList)
        }

        override fun onCancelled(error: DatabaseError) {
            onError(error)
        }
    })
}

@Composable
fun FoodItem(food: Food) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Food Name: ${food.foodName ?: "N/A"}",
            color = Color.Black,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Category: ${food.category ?: "N/A"}",
            color = Color.Gray,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Calories: ${food.calories}",
            color = Color.Gray,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HomeScreen() {
    val foodList = remember { mutableStateListOf<Food>() }

    // Retrieve data from Firebase
    LaunchedEffect(Unit) {
        getFoodData(
            onDataLoaded = { fetchedFoodList ->
                foodList.clear()
                foodList.addAll(fetchedFoodList)
            },
            onError = { error ->
                // Handle error by showing a toast message

            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE280FF), // Start color
                        Color(0xFF9F44D3) // End color
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Food List",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            items(foodList) { food ->
                FoodItem(food = food)
            }
        }
    }
}