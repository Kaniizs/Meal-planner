package org.classapp.mealplanner20

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE280FF),
                        Color(0xFF9F44D3)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
            .wrapContentHeight()
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
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Calories: ${food.calories}",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HomeScreen() {
    val foodList = remember { mutableStateListOf<Food>() }

    LaunchedEffect(Unit) {
        fetchDataFromDatabase(foodList)
    }

    val totalCalories = foodList.sumOf { it.calories ?: 0 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE280FF),
                        Color(0xFF9F44D3)
                    )
                ),
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Food List",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Button(
                    onClick = { clearDataFromDatabase(foodList) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "Clear")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE280FF),
                                Color(0xFF9F44D3)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(foodList) { food ->
                    FoodItem(food = food)
                }
            }

            Text(
                text = if (totalCalories > 2000) {
                    "You have eaten too much today!"
                } else {
                    "Today, you have eaten a total of $totalCalories calories"
                },
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun fetchDataFromDatabase(foodList: MutableList<Food>) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("Foods")

    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newFoodList = mutableListOf<Food>()
            for (childSnapshot in snapshot.children) {
                val food = childSnapshot.getValue(Food::class.java)
                food?.let {
                    newFoodList.add(it)
                }
            }
            foodList.clear()
            foodList.addAll(newFoodList)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}

private fun clearDataFromDatabase(foodList: MutableList<Food>) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("Foods")
    ref.removeValue().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            foodList.clear()
        } else {
            // Handle failure
        }
    }
}