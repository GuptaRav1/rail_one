package com.example.railone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        val startTime = System.currentTimeMillis()
        splashScreen.setKeepOnScreenCondition {
            System.currentTimeMillis() - startTime < 2000
        }
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_bookings -> {
                        startActivity(android.content.Intent(this, BookingsActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
    }
}
