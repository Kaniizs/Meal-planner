package org.classapp.mealplanner20

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun AddScreen(context: Context) {
    val isAddButtonEnabled = remember { mutableStateOf(true) }
    val buttonText = if (isAddButtonEnabled.value) {
        "Add a new food list here!"
    } else {
        "You cannot add any food because you have exceeded the limit today!"
    }

    val foodList = remember { mutableStateListOf<Food>() }
    val limitCalories = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        fetchDataFromDatabase(foodList, limitCalories)
    }

    val totalCalories = foodList.sumOf { it.calories ?: 0 }

    if (totalCalories > limitCalories.intValue) {
        isAddButtonEnabled.value = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE280FF), // Start color
                        Color(0xFF9F44D3)// End color
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.food),
                contentDescription = "Food",
                modifier = Modifier.size(350.dp)
            )

            Button(
                onClick = {
                    if (isAddButtonEnabled.value) {
                        // Create intent to navigate to FoodList activity
                        val intent = Intent(context, FoodList::class.java)
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = if (isAddButtonEnabled.value) Color(0xFF6200EE) else Color.Gray
                ),
                enabled = isAddButtonEnabled.value
            ) {
                Text(
                    text = buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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