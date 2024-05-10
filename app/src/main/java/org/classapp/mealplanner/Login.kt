package org.classapp.mealplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin : Button
    private lateinit var progressBar : ProgressBar
    private lateinit var textView : TextView
    private lateinit var auth: FirebaseAuth // Initialize FirebaseAuth instance

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.Email)
        editTextPassword = findViewById(R.id.Password)
        buttonLogin = findViewById(R.id.button_login)
        progressBar = findViewById(R.id.progressbar)
        textView = findViewById(R.id.registerText)
        textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }


        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth instance

        // Set OnClickListener for the login button
        buttonLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                baseContext,
                                "Login Success.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Login Failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                progressBar.visibility = View.GONE
            }
        }
    }
}