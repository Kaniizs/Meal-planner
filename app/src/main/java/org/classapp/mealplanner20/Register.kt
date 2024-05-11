package org.classapp.mealplanner20

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth


class Register : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonReg: Button
    private lateinit var progressBar : ProgressBar
    private lateinit var auth: FirebaseAuth // Initialize FirebaseAuth instance\
    private lateinit var textView: TextView

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextEmail = findViewById(R.id.Email)
        editTextPassword = findViewById(R.id.Password)
        buttonReg = findViewById(R.id.button_register)
        progressBar = findViewById(R.id.progressbar)
        textView = findViewById(R.id.goToLoginText)
        textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()

        buttonReg.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Show toast message if email or password is empty
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                // Create user with email and password using FirebaseAuth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account has been created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        progressBar.visibility = View.GONE // Hide progress bar
                    }
            }
        }
    }
}
