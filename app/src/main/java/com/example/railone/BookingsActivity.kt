package com.example.railone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat

class BookingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make status bar transparent so the blue header extends to the top
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContentView(R.layout.activity_bookings)
        
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_view_details).setOnClickListener {
            startActivity(Intent(this, TicketDetailsActivity::class.java))
        }
    }
}
