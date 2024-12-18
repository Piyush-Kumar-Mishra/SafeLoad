package com.example.safeload


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        val toolbar: Toolbar = findViewById(R.id.top_nav_bar)
        setSupportActionBar(toolbar)

        // Set the title of the app
        supportActionBar?.title = "My App"


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)


        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    replaceFragment(Fragment0())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    replaceFragment(Fragment1())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_settings -> {
                    replaceFragment(Fragment2())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_about -> {
                    replaceFragment(Fragment3())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.About -> {
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.Profile -> {
                val sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE)
                val userId = sharedPreferences.getString("UserId", null)

                if (userId != null) {
                    // Retrieve user data
                    val name = sharedPreferences.getString("name_$userId", "No Name")
                    val age = sharedPreferences.getString("age_$userId", "No Age")
                    val contact = sharedPreferences.getString("contact_$userId", "No Contact")
                    val emergencyContact = sharedPreferences.getString("emergencyContact_$userId", "No Emergency Contact")
                    val dob = sharedPreferences.getString("dob_$userId", "No DOB")

                    // Pass data to ShowProfile activity
                    val intent = Intent(this, ShowProfile::class.java).apply {
                        putExtra("name", name)
                        putExtra("age", age)
                        putExtra("contact", contact)
                        putExtra("emergencyContact", emergencyContact)
                        putExtra("dob", dob)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
