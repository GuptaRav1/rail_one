package com.example.railone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
        val etViaRoute = findViewById<EditText>(R.id.et_via_route)
        val etDistance = findViewById<EditText>(R.id.et_distance)
        val etTicketType = findViewById<EditText>(R.id.et_ticket_type)
        val etPassengerCount = findViewById<EditText>(R.id.et_passenger_count)
        val etClassType = findViewById<AutoCompleteTextView>(R.id.et_class_type)
        val etTrainType = findViewById<EditText>(R.id.et_train_type)
        val etPrice = findViewById<EditText>(R.id.et_price)
        val etUtsNumber = findViewById<EditText>(R.id.et_uts_number)

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

        // Handle category toggle to auto-populate ticket details matching sample tickets
        rgCategory.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_journey) {
                // Single Journey Ticket defaults
                etSource.setText("KHARGHAR", false)
                etDestination.setText("VASHI", false)
                etViaRoute.setText("------")
                etDistance.setText("12 km")
                etTicketType.setText("JOURNEY")
                etPassengerCount.setText("1 Adult, 0 Child")
                etClassType.setText("SECOND", false)
                etTrainType.setText("ORDINARY")
                etPrice.setText("₹ 10.00")
                etUtsNumber.setText("X0HNEG00D8")
            } else {
                // Season Pass / Monthly Ticket defaults
                etSource.setText("PANVEL", false)
                etDestination.setText("VASHI", false)
                etViaRoute.setText("1RT>>JNJ-SNCR")
                etDistance.setText("21 km")
                etTicketType.setText("MONTHLY")
                etPassengerCount.setText("1 Adult, 0 Child")
                etClassType.setText("SECOND", false)
                etTrainType.setText("ORDINARY")
                etPrice.setText("₹ 235.00")
                etUtsNumber.setText("X07DEF61F8")
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

            val newTicket = TicketData(
                utsNumber = etUtsNumber.text.toString().trim().ifEmpty {
                    if (isJourney) "X0HNEG00D8" else "X07DEF61F8"
                },
                ticketCategory = ticketCategory,
                ticketType = etTicketType.text.toString().trim().ifEmpty { if (isJourney) "JOURNEY" else "MONTHLY" },
                bookingDateTime = bookingDateTimeStr,
                validFrom = validFromStr,
                validTill = validTillStr,
                sourceStation = source,
                destinationStation = destination,
                viaRoute = etViaRoute.text.toString().trim().ifEmpty { if (isJourney) "------" else "1RT>>JNJ-SNCR" },
                distanceKm = etDistance.text.toString().trim().ifEmpty { if (isJourney) "12 km" else "21 km" },
                classType = etClassType.text.toString().trim().ifEmpty { "SECOND" },
                trainType = etTrainType.text.toString().trim().ifEmpty { "ORDINARY" },
                price = etPrice.text.toString().trim().ifEmpty { if (isJourney) "₹ 10.00" else "₹ 235.00" },
                passengerCount = etPassengerCount.text.toString().trim().ifEmpty { "1 Adult, 0 Child" },
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
        val etViaRoute = findViewById<EditText>(R.id.et_via_route)
        val etDistance = findViewById<EditText>(R.id.et_distance)
        val etPrice = findViewById<EditText>(R.id.et_price)
        val etClassType = findViewById<AutoCompleteTextView>(R.id.et_class_type)

        if (source.isEmpty() || destination.isEmpty()) return

        val travelClass = etClassType.text.toString().trim().ifEmpty { "SECOND" }
        val routeResult = RailwayGraph.findShortestPath(source, destination, travelClass)

        if (routeResult != null) {
            etDistance.setText("${routeResult.distanceKm} km")
            etPrice.setText("₹ ${routeResult.fare}.00")
            etViaRoute.setText(routeResult.via)
        } else {
            // Fallback for invalid paths
            etDistance.setText("0 km")
            etPrice.setText("₹ 0.00")
            etViaRoute.setText("------")
        }
    }
}