package com.example.railone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.railone.data.TicketData
import com.example.railone.data.UserPreferencesManager
import com.example.railone.data.RailwayGraph
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookTicketActivity : AppCompatActivity() {

    private lateinit var prefsManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_book_ticket)

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

        val rgCategory = findViewById<RadioGroup>(R.id.rg_ticket_category)
        val rbJourney = findViewById<RadioButton>(R.id.rb_journey)
        val etSource = findViewById<AutoCompleteTextView>(R.id.et_source)
        val etDestination = findViewById<AutoCompleteTextView>(R.id.et_destination)
        val etPassengerCount = findViewById<EditText>(R.id.et_passenger_count)
        val etClassType = findViewById<AutoCompleteTextView>(R.id.et_class_type)
        val etPrice = findViewById<EditText>(R.id.et_price)

        val mumbaiStations = RailwayGraph.getAllStations()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mumbaiStations)
        etSource.setAdapter(adapter)
        etDestination.setAdapter(adapter)

        val classOptions = listOf("SECOND", "FIRST", "AC")
        val classAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classOptions)
        etClassType.setAdapter(classAdapter)

        // Set up listeners to auto-calculate distance when both stations are selected
        etSource.setOnItemClickListener { _, _, _, _ ->
            calculateRouteDetails(etSource.text.toString().trim(), etDestination.text.toString().trim())
        }
        
        etDestination.setOnItemClickListener { _, _, _, _ ->
            calculateRouteDetails(etSource.text.toString().trim(), etDestination.text.toString().trim())
        }

        etClassType.setOnItemClickListener { _, _, _, _ ->
            calculateRouteDetails(etSource.text.toString().trim(), etDestination.text.toString().trim())
        }

        etPassengerCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateRouteDetails(etSource.text.toString().trim(), etDestination.text.toString().trim())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle category toggle to auto-populate ticket details matching sample tickets
        rgCategory.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_journey) {
                // Single Journey Ticket defaults
                etSource.setText("KHARGHAR", false)
                etDestination.setText("VASHI", false)
                etPassengerCount.setText("1")
                etClassType.setText("SECOND", false)
                etPrice.setText("₹ 10.00")
            } else {
                // Season Pass / Monthly Ticket defaults
                etSource.setText("PANVEL", false)
                etDestination.setText("VASHI", false)
                etPassengerCount.setText("1")
                etClassType.setText("SECOND", false)
                etPrice.setText("₹ 235.00")
            }
        }

        findViewById<Button>(R.id.btn_generate_ticket).setOnClickListener {
            val source = etSource.text.toString().trim()
            val destination = etDestination.text.toString().trim()

            if (source.isEmpty() || destination.isEmpty()) {
                Toast.makeText(this, "Please enter Source and Destination stations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isJourney = rbJourney.isChecked
            val ticketCategory = if (isJourney) "JOURNEY" else "SEASON"

            val now = LocalDateTime.now()
            val image1Format = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            val dateFormatOnly = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

            val bookingDateTimeStr = now.format(image1Format)
            val validFromStr = now.format(dateFormatOnly)
            val validTillStr = if (isJourney) {
                now.plusHours(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH))
            } else {
                now.plusMonths(1).minusDays(1).format(dateFormatOnly)
            }

            val userProfile = prefsManager.getUserProfile()
            val travelClass = etClassType.text.toString().trim().ifEmpty { "SECOND" }
            val routeResult = RailwayGraph.findShortestPath(source, destination, travelClass)
            
            val finalDistance = if (routeResult != null) "${routeResult.distanceKm} km" else if (isJourney) "12 km" else "21 km"
            val finalVia = if (routeResult != null) routeResult.via else if (isJourney) "------" else "1RT>>JNJ-SNCR"
            val finalPrice = if (routeResult != null) "₹ ${routeResult.fare}.00" else etPrice.text.toString()

            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val randomUts = (1..10).map { chars.random() }.joinToString("")

            val newTicket = TicketData(
                utsNumber = randomUts,
                ticketCategory = ticketCategory,
                ticketType = if (isJourney) "JOURNEY" else "MONTHLY",
                bookingDateTime = bookingDateTimeStr,
                validFrom = validFromStr,
                validTill = validTillStr,
                sourceStation = source,
                destinationStation = destination,
                viaRoute = finalVia,
                distanceKm = finalDistance,
                classType = travelClass,
                trainType = if (travelClass == "AC") "AC" else "ORDINARY",
                price = finalPrice,
                passengerCount = "${etPassengerCount.text.toString().trim().ifEmpty { "1" }} Adult, 0 Child",
                irCode = "IR:27AAAGM0289C2ZI",
                userProfile = userProfile
            )

            prefsManager.saveActiveTicket(newTicket)

            Toast.makeText(this, "Ticket Booked Successfully!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, BookingsActivity::class.java))
            finish()
        }
    }

    private fun calculateRouteDetails(source: String, destination: String) {
        val etPrice = findViewById<EditText>(R.id.et_price)
        val etClassType = findViewById<AutoCompleteTextView>(R.id.et_class_type)
        val etPassengerCount = findViewById<EditText>(R.id.et_passenger_count)

        if (source.isEmpty() || destination.isEmpty()) return

        val travelClass = etClassType.text.toString().trim().ifEmpty { "SECOND" }
        val routeResult = RailwayGraph.findShortestPath(source, destination, travelClass)
        
        val passengers = etPassengerCount.text.toString().trim().toIntOrNull() ?: 1

        if (routeResult != null) {
            val totalFare = routeResult.fare * passengers
            etPrice.setText("₹ ${totalFare}.00")
        } else {
            // Fallback for invalid paths
            etPrice.setText("₹ 0.00")
        }
    }
}