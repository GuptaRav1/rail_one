package com.example.railone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.railone.data.UserPreferencesManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingsActivity : AppCompatActivity() {

    private lateinit var prefsManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make status bar transparent so the blue header extends to the top
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContentView(R.layout.activity_bookings)

        val topBar = findViewById<View>(R.id.topBar)
        ViewCompat.setOnApplyWindowInsetsListener(topBar) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            val density = resources.displayMetrics.density
            v.setPadding(
                v.paddingLeft,
                statusBarHeight + (12 * density).toInt(),
                v.paddingRight,
                (16 * density).toInt()
            )
            insets
        }

        prefsManager = UserPreferencesManager(this)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_view_details).setOnClickListener {
            startActivity(Intent(this, TicketDetailsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        bindTicketData()
    }

    private fun bindTicketData() {
        val ticket = prefsManager.getActiveTicket()
        val isJourney = ticket.ticketCategory.equals("JOURNEY", ignoreCase = true) ||
                ticket.ticketType.equals("JOURNEY", ignoreCase = true)

        val bookingDateTime = if (isJourney) LocalDateTime.now() else LocalDateTime.now().minusDays(11)
        val bookingDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)

        findViewById<TextView>(R.id.tv_uts)?.text = ticket.utsNumber
        findViewById<TextView>(R.id.tv_ticket_type)?.text = ticket.ticketType
        findViewById<TextView>(R.id.tv_booking_date)?.text = bookingDateTime.format(bookingDateFormat)
        findViewById<TextView>(R.id.tv_from)?.text = ticket.sourceStation
        findViewById<TextView>(R.id.tv_to)?.text = ticket.destinationStation
        findViewById<TextView>(R.id.tv_dist)?.text = "— ${ticket.distanceKm} —"
    }
}