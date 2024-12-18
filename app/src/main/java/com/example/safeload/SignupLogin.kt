package com.example.safeload

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class SignupLogin : AppCompatActivity() {

    private lateinit var loginForm: CardView
    private lateinit var signupForm: CardView
    private lateinit var signupCard: CardView
    private lateinit var loginCard: CardView
    private lateinit var loginSubtitle: TextView
    private lateinit var signupSubtitle: TextView
    private lateinit var line1: ImageView
    private lateinit var line2: ImageView
    private lateinit var line3: ImageView
    private lateinit var line4: ImageView

    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_login)

        // Initialize views
        loginForm = findViewById(R.id.loginFormCard)
        signupForm = findViewById(R.id.signupFormCard)
        signupCard = findViewById(R.id.signupCard)
        loginCard = findViewById(R.id.loginCard)
        loginSubtitle = findViewById(R.id.loginSubtitle)
        signupSubtitle = findViewById(R.id.signupSubtitle)
        line1=findViewById(R.id.line1)
        line2=findViewById(R.id.line2)
        line3=findViewById(R.id.line3)
        line4=findViewById(R.id.line4)

        val textLogin = findViewById<TextView>(R.id.login_text)
        val userUsername = findViewById<TextInputEditText>(R.id.signupUsername)
        val userEmail = findViewById<TextInputEditText>(R.id.signupEmail)
        val userPassword = findViewById<TextInputEditText>(R.id.signupPassword)
        val btnSignup = findViewById<Button>(R.id.signupButton)
        val loginUsername = findViewById<TextInputEditText>(R.id.loginUsername)
        val loginPassword = findViewById<TextInputEditText>(R.id.loginPassword)
        val btnLogin = findViewById<Button>(R.id.loginButton)

        animateLoginText(textLogin)
        showLogin()

        // Show login form when loginCard is clicked
        loginCard.setOnClickListener {
            Log.d("SignupActivity", "Login Card Clicked")
            showLogin()
        }

        // Show signup form when signupCard is clicked
        signupCard.setOnClickListener {
            Log.d("SignupActivity", "Signup Card Clicked")
            showSignup()
        }

        // Signup button clicked
        btnSignup.setOnClickListener {
            val username = userUsername.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = database.reference.push().key ?: ""
            val user = mapOf(
                "id" to userId,
                "username" to username,
                "email" to email,
                "password" to password
            )

            // Save user to Firebase
            database.reference.child("Users").child(userId).setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Save User ID to SharedPreferences after successful sign-up
                        val editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit()
                        editor.putString("UserId", userId)
                        editor.putString("Username", username)
                        editor.apply()
                        Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        btnLogin.setOnClickListener {
            val email = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the user exists in Firebase
            database.reference.child("Users").get().addOnSuccessListener { snapshot ->
                var foundUser = false
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.value as Map<*, *>
                    if (user["email"] == email && user["password"] == password) {
                        foundUser = true
                        // Pass data to the next activity
                        val intent = Intent(this, UserInfo::class.java).apply {
                            putExtra("Username", user["username"] as String)
                            putExtra("UserId", userSnapshot.key)
                        }
                        startActivity(intent)
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
                if (!foundUser) {
                    Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Animate login text
    private fun animateLoginText(textView: TextView) {
        val colorAnimator = ObjectAnimator.ofArgb(
            textView, "textColor",
            Color.parseColor("#8b00ff"), Color.parseColor("#94C5AC"), Color.WHITE
        )
        colorAnimator.duration = 2000
        colorAnimator.repeatCount = ValueAnimator.INFINITE
        colorAnimator.repeatMode = ValueAnimator.REVERSE
        colorAnimator.start()

        val translationAnimator = ObjectAnimator.ofFloat(textView, "translationX", -10f, 10f)
        translationAnimator.duration = 1000
        translationAnimator.repeatCount = ValueAnimator.INFINITE
        translationAnimator.repeatMode = ValueAnimator.REVERSE
        translationAnimator.start()

        val skewAnimator = ObjectAnimator.ofFloat(textView, "rotationX", 0f, 10f, -10f, 0f)
        skewAnimator.duration = 1000
        skewAnimator.repeatCount = ValueAnimator.INFINITE
        skewAnimator.start()
    }
    private fun showLogin() {
        loginForm.visibility = View.VISIBLE
        signupForm.visibility = View.GONE

        line1.visibility = View.VISIBLE
        line3.visibility = View.VISIBLE
        line2.visibility = View.INVISIBLE
        line4.visibility = View.INVISIBLE
        loginCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        signupSubtitle.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    private fun showSignup() {
        loginForm.visibility = View.GONE
        signupForm.visibility = View.VISIBLE
        line2.visibility = View.VISIBLE
        line4.visibility = View.VISIBLE
        line1.visibility = View.INVISIBLE
        line3.visibility = View.INVISIBLE
        signupCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        loginSubtitle.setTextColor(ContextCompat.getColor(this, R.color.black))
    }
}