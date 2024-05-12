package org.classapp.mealplanner20

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(context: Context) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val scope = rememberCoroutineScope()
    val uploadedImageUrl = remember { mutableStateOf<Uri?>(null) }
    val username = remember { mutableStateOf("") }

    // Function to fetch the profile image URL from Firebase
    fun fetchProfileImageUrl() {
        val storageRef = FirebaseStorage.getInstance().reference
        currentUser?.let { user ->
            val imageRef = storageRef.child("profile_pictures/${user.uid}")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                uploadedImageUrl.value = uri
            }.addOnFailureListener {
                // Handle failure
            }
        }
    }

    // Fetch profile image URL when the screen is launched
    LaunchedEffect(Unit) {
        fetchProfileImageUrl()
        fetchUsernameFromDatabase(username)
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
    )

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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = username.value,
                modifier = Modifier.padding(top = 25.dp),
            )
            // User icon
            uploadedImageUrl.value?.let { imageUrl ->
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .size(150.dp)
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "Image",
                modifier = Modifier
                    .padding(top = 50.dp)
                    .size(150.dp)
            )

            // Change Profile Picture button
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    // Upload the selected image to Firebase Storage
                    scope.launch {
                        uploadProfileImage(it)
                        uploadedImageUrl.value = it
                    }
                }
            }

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF6200EE)
                )

            ) {
                Text(text = "Change Profile Picture")
            }


            // Edit Profile button
            Button(
                onClick = {
                    val intent = Intent(context, ProfileEdit::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
                    .width(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Text(text = "Edit Profile")
            }

            // Logout button
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
                    .width(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red
                )
            ) {
                Text(text = "Logout")
            }
        }
    }
}

// Modify the uploadProfileImage function to save the image to Firebase Storage
fun uploadProfileImage(imageUri: Uri) {
    val storageRef = FirebaseStorage.getInstance().reference
    val currentUser = FirebaseAuth.getInstance().currentUser

    currentUser?.let { user ->
        val imageRef = storageRef.child("profile_pictures/${user.uid}")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { _ ->
            }
            .addOnFailureListener {
                // Handle upload failure
            }
    }
}

private fun fetchUsernameFromDatabase(userName: MutableState<String>) {
    val database = FirebaseDatabase.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: ""
    val userProfileRef = database.getReference("userProfile").child(userEmail.replace(".", "_"))

    // Fetch username
    userProfileRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val username = snapshot.child("userName").getValue(String::class.java)
            username?.let {
                userName.value = it
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}