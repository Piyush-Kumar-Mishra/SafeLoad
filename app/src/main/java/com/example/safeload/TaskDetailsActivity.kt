package com.example.safeload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

import androidx.appcompat.app.AppCompatActivity

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var etStartingLocation: EditText
    private lateinit var etEndingLocation: EditText
    private lateinit var btnEnterStartingLocation: ImageButton
    private lateinit var btnEnterEndingLocation: ImageButton
    private lateinit var btnSaveTask: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        etStartingLocation = findViewById(R.id.etStartingLocation)
        etEndingLocation = findViewById(R.id.etEndingLocation)
        btnEnterStartingLocation = findViewById(R.id.btnEnterStartingLocation)
        btnEnterEndingLocation = findViewById(R.id.btnEnterEndingLocation)
        btnSaveTask = findViewById(R.id.btnSaveTask)

        // Pre-fill previous locations if available
        etStartingLocation.setText(intent.getStringExtra("startingLocation"))
        etEndingLocation.setText(intent.getStringExtra("endingLocation"))

        btnEnterStartingLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, 1)
        }

        btnEnterEndingLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, 2)
        }

        btnSaveTask.setOnClickListener {
            val startingLocation = etStartingLocation.text.toString()
            val endingLocation = etEndingLocation.text.toString()

            if (startingLocation.isNotBlank() && endingLocation.isNotBlank()) {
                val distance = calculateDistance(startingLocation, endingLocation)
                val resultIntent = Intent().apply {
                    putExtra("startingLocation", startingLocation)
                    putExtra("endingLocation", endingLocation)
                    putExtra("distance", distance)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                etStartingLocation.error = "Enter starting location"
                etEndingLocation.error = "Enter ending location"
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val location = data.getStringExtra("location")
            if (requestCode == 1) etStartingLocation.setText(location)
            else if (requestCode == 2) etEndingLocation.setText(location)
        }
    }

    private fun calculateDistance(start: String, end: String): String {
        // Placeholder for actual distance calculation
        return "10 km"
    }
}