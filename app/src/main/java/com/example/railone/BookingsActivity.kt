package com.example.railone

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make status bar transparent so the blue header extends to the top
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContentView(R.layout.activity_bookings)

        // Set dynamic booking date (Current Date - 11 days)
        val bookingDate = LocalDate.now().minusDays(11)
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yy", Locale.ENGLISH)
        findViewById<TextView>(R.id.tv_booking_date).text = bookingDate.format(formatter)
        
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_view_details).setOnClickListener {
            startActivity(Intent(this, TicketDetailsActivity::class.java))
        }
    }
}
