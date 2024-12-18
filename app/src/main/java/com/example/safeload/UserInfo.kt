package com.example.safeload

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class UserInfo : AppCompatActivity() {

    private lateinit var nameField: TextInputEditText
    private lateinit var ageField: TextInputEditText
    private lateinit var contactField: TextInputEditText
    private lateinit var emergencyContactField: TextInputEditText
    private lateinit var dobField: TextInputEditText
    private lateinit var submitButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        nameField = findViewById(R.id.nameField)
        ageField = findViewById(R.id.ageField)
        contactField = findViewById(R.id.contactField)
        emergencyContactField = findViewById(R.id.emergencyContactField)
        dobField = findViewById(R.id.dobField)
        submitButton = findViewById(R.id.submitButton)

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE)
        val userId = sharedPreferences.getString("UserId", null)

        if (userId == null) {
            Log.e("UserInfo", "User ID is null")
            Toast.makeText(this, "User not found, please login again", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupLogin::class.java))
            finish()
            return
        }

        val isInfoSubmitted = sharedPreferences.getBoolean("IsInfoSubmitted_$userId", false)
        if (isInfoSubmitted) {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        dobField.setOnClickListener {
            showDatePicker()
        }

        submitButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val age = ageField.text.toString().trim()
            val contact = contactField.text.toString().trim()
            val emergencyContact = emergencyContactField.text.toString().trim()
            val dob = dobField.text.toString().trim()

            if (name.isEmpty() || age.isEmpty() || contact.isEmpty() || emergencyContact.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userInfo = mapOf(
                "name" to name,
                "age" to age,
                "contact" to contact,
                "emergencyContact" to emergencyContact,
                "dob" to dob
            )

            val userRef = database.reference.child("Users").child(userId)
            userRef.child("userInfo").setValue(userInfo) // Save user info under "userInfo"

                .addOnSuccessListener {
                    // Save locally
                    sharedPreferences.edit().apply {
                        putString("name_$userId", name)
                        putString("age_$userId", age)
                        putString("contact_$userId", contact)
                        putString("emergencyContact_$userId", emergencyContact)
                        putString("dob_$userId", dob)
                        putBoolean("IsInfoSubmitted_$userId", true)
                        apply()
                    }

                    Toast.makeText(this, "User Info Saved!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Dashboard::class.java))
                    finish()
                }
                .addOnFailureListener { error ->
                    Log.e("FirebaseError", "Error saving user info: ${error.message}")
                    Toast.makeText(this, "Failed to save user info. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.format(Date(selection))
            dobField.setText(date)
        }
        datePicker.show(supportFragmentManager, "DOB_DATE_PICKER")
    }
}
