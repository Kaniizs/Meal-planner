package org.classapp.mealplanner20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileEdit : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var userNameEditText: EditText
    private lateinit var limitCaloriesEditText: EditText
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize EditText
        userNameEditText = findViewById(R.id.userNameEditText)
        limitCaloriesEditText = findViewById(R.id.caloriesAmountEditText)

        // Initialize done button
        doneButton = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener {
            updateUserProfile()
        }

        // Fetch and display user profile data
        fetchAndDisplayUserProfile()
    }
    private fun fetchAndDisplayUserProfile() {
        // Get the currently signed-in user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if a user is signed in
        if (currentUser != null) {
            // Get the user's email
            val userEmail = currentUser.email

            // Check if user email is not null
            if (!userEmail.isNullOrEmpty()) {
                // Get a reference to the Firebase Database
                val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("userProfile")

                // Fetch user profile data
                database.child(userEmail.replace(".", "_")).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // If the profile exists, populate the EditText fields with the retrieved values
                        val userProfile = snapshot.getValue(UserProfile::class.java)
                        userProfile?.let {
                            userNameEditText.setText(it.userName)
                            limitCaloriesEditText.setText(it.limitCalories.toString())
                        }
                    } else {
                        // If the profile doesn't exist, show a toast message
                        Toast.makeText(this, "Profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Failed to fetch profile data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserProfile() {
        // Get the currently signed-in user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if a user is signed in
        if (currentUser != null) {
            // Get the user's email
            val userEmail = currentUser.email

            // Check if user email is not null
            if (!userEmail.isNullOrEmpty()) {
                // Get other data from EditText fields
                val userName = userNameEditText.text.toString()
                val limitCalories = limitCaloriesEditText.text.toString().toIntOrNull() ?: 0 // Set to 0 if not provided or invalid

                // Get a reference to the Firebase Database
                val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("userProfile")

                // Inside the updateUserProfile() function
                database.child(userEmail.replace(".", "_")).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // If the profile exists, update it
                        database.child(userEmail.replace(".", "_")).child("userName").setValue(userName)
                        database.child(userEmail.replace(".", "_")).child("limitCalories").setValue(limitCalories)
                        Toast.makeText(this, "Profile has been updated.", Toast.LENGTH_SHORT).show()
                    } else {
                        // If the profile doesn't exist, create it
                        val userProfile = UserProfile(userName, limitCalories)
                        database.child(userEmail.replace(".", "_")).setValue(userProfile).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Profile creation successful
                                Toast.makeText(this, "Profile created successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                // Profile creation failed
                                Toast.makeText(this, "Failed to create profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // User email is null or empty
                // Show a toast message
                Toast.makeText(this, "User email not found. Please sign in again.", Toast.LENGTH_SHORT).show()
                // Redirect user to login page
                startActivity(Intent(this, Login::class.java))
                // Finish the current activity
                finish()
            }
        } else {
            // No user is signed in
            // Show a toast message
            Toast.makeText(this, "You are not signed in. Please sign in.", Toast.LENGTH_SHORT).show()
            // Redirect user to login page
            startActivity(Intent(this, Login::class.java))
            // Finish the current activity
            finish()
        }
    }
}
