package com.example.railone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.railone.data.TicketData
import com.example.railone.data.UserPreferencesManager
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

        val etSource = findViewById<EditText>(R.id.et_source)
        val etDestination = findViewById<EditText>(R.id.et_destination)
        val etViaRoute = findViewById<EditText>(R.id.et_via_route)
        val etDistance = findViewById<EditText>(R.id.et_distance)
        val etTicketType = findViewById<EditText>(R.id.et_ticket_type)
        val etClassType = findViewById<EditText>(R.id.et_class_type)
        val etTrainType = findViewById<EditText>(R.id.et_train_type)
        val etPrice = findViewById<EditText>(R.id.et_price)
        val etUtsNumber = findViewById<EditText>(R.id.et_uts_number)

        findViewById<Button>(R.id.btn_generate_ticket).setOnClickListener {
            val source = etSource.text.toString().trim()
            val destination = etDestination.text.toString().trim()

            if (source.isEmpty() || destination.isEmpty()) {
                Toast.makeText(this, "Please enter Source and Destination stations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val now = LocalDateTime.now()
            val image1Format = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            val dateFormatOnly = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

            val bookingDateTimeStr = now.format(image1Format)
            val validFromStr = now.format(dateFormatOnly)
            val validTillStr = now.plusMonths(1).minusDays(1).format(dateFormatOnly)

            val userProfile = prefsManager.getUserProfile()

            val newTicket = TicketData(
                utsNumber = etUtsNumber.text.toString().trim().ifEmpty { "X06ZEE" + (1000..9999).random() },
                ticketType = etTicketType.text.toString().trim().ifEmpty { "MONTHLY" },
                bookingDateTime = bookingDateTimeStr,
                validFrom = validFromStr,
                validTill = validTillStr,
                sourceStation = source,
                destinationStation = destination,
                viaRoute = etViaRoute.text.toString().trim().ifEmpty { "1RT>>DIRECT" },
                distanceKm = etDistance.text.toString().trim().ifEmpty { "10 km" },
                classType = etClassType.text.toString().trim().ifEmpty { "SECOND" },
                trainType = etTrainType.text.toString().trim().ifEmpty { "ORDINARY" },
                price = etPrice.text.toString().trim().ifEmpty { "₹ 120.00" },
                userProfile = userProfile
            )

            prefsManager.saveActiveTicket(newTicket)

            Toast.makeText(this, "Ticket Booked Successfully!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, BookingsActivity::class.java))
            finish()
        }
    }
}
