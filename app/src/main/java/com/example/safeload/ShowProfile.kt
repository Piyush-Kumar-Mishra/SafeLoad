package com.example.safeload

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShowProfile : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var contactTextView: TextView
    private lateinit var emergencyContactTextView: TextView
    private lateinit var dobTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        nameTextView = findViewById(R.id.nameTextView)
        ageTextView = findViewById(R.id.ageTextView)
        contactTextView = findViewById(R.id.contactTextView)
        emergencyContactTextView = findViewById(R.id.emergencyContactTextView)
        dobTextView = findViewById(R.id.dobTextView)

        val name = intent.getStringExtra("name") ?: "No Name"
        val age = intent.getStringExtra("age") ?: "No Age"
        val contact = intent.getStringExtra("contact") ?: "No Contact"
        val emergencyContact = intent.getStringExtra("emergencyContact") ?: "No Emergency Contact"
        val dob = intent.getStringExtra("dob") ?: "No DOB"

        // Display data
        nameTextView.text = "Name: $name"
        ageTextView.text = "Age: $age"
        contactTextView.text = "Contact: $contact"
        emergencyContactTextView.text = "Emergency Contact: $emergencyContact"
        dobTextView.text = "Date of Birth: $dob"
    }
}