package org.classapp.mealplanner20

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    val limitCalories = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        fetchDataFromDatabase(foodList, limitCalories)
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

            val userLimitCalories = limitCalories.intValue

            Text(
                text = if (totalCalories > userLimitCalories) {
                    "You have exceeded your daily calorie limit of $userLimitCalories calories!"
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

private fun fetchDataFromDatabase(foodList: MutableList<Food>, limitCalories: MutableState<Int>) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("Foods")
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: ""
    val userProfileRef = database.getReference("userProfile").child(userEmail.replace(".", "_"))

    // Add ValueEventListener to fetch both food list and limit calories
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

    // Fetch user's calorie limit
    userProfileRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val limit = snapshot.child("limitCalories").getValue(Int::class.java)
            limit?.let {
                limitCalories.value = it
            }
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