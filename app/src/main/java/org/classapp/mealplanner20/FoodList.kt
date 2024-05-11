package org.classapp.mealplanner20

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase

class FoodList : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var foodNameEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize EditText fields
        foodNameEditText = findViewById(R.id.foodNameEditText)
        categoryEditText = findViewById(R.id.categoryEditText)
        caloriesEditText = findViewById(R.id.caloriesEditText)

        // Set click listener for the submit button
        submitButton = findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            // Get the data from the EditText fields
            val foodName = foodNameEditText.text.toString()
            val foodType = categoryEditText.text.toString()
            val foodCalories = caloriesEditText.text.toString().toInt() // Parse to Int

            // Create a Food object
            val food = Food(foodName, foodType, foodCalories)

            // Push the food object to Firebase Realtime Database with the key as food name
            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference.child("Foods")
            val foodId = myRef.push().key // Generate unique key for food entry
            if (foodId != null) {
                myRef.child(foodId).setValue(food).addOnSuccessListener {
                    // Clear EditText fields after submitting
                    foodNameEditText.setText("")
                    categoryEditText.setText("")
                    caloriesEditText.setText("")

                    Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }.addOnFailureListener { e ->
                    // Handle error
                    Log.e(TAG, "Error writing data to Firebase", e)
                    Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to generate food ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
