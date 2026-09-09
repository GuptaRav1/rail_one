package com.example.railone

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.railone.data.UserPreferencesManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TicketDetailsActivity : AppCompatActivity() {

    private lateinit var tvTimer: RollingTimerView
    private lateinit var pbTimerMiddle: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private lateinit var prefsManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_ticket_details)

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

        tvTimer = findViewById(R.id.tv_timer)
        pbTimerMiddle = findViewById(R.id.pb_timer_middle)

        bindDynamicData()

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        startTimer(5 * 60 * 1000) // 5 minutes
    }

    private fun bindDynamicData() {
        val ticket = prefsManager.getActiveTicket()
        val user = ticket.userProfile

        val isJourney = ticket.ticketCategory.equals("JOURNEY", ignoreCase = true) ||
                ticket.ticketType.equals("JOURNEY", ignoreCase = true)

        // Border & Progress Bar Accent Color: Amber/Yellow for Single Journey, Light Green for Season Pass
        val accentColor = if (isJourney) Color.parseColor("#F5B000") else Color.parseColor("#8BC34A")
        findViewById<View>(R.id.v_border_top)?.setBackgroundColor(accentColor)
        findViewById<View>(R.id.v_border_bottom)?.setBackgroundColor(accentColor)
        pbTimerMiddle.progressTintList = ColorStateList.valueOf(accentColor)

        // Top Header
        findViewById<TextView>(R.id.tv_header_mobile)?.text = "Mobile: ${user.mobileNumber}"

        // Greeting
        findViewById<TextView>(R.id.tv_greeting)?.text = "Thank You ${user.name}, Happy Journey !"

        // Booking Timestamp Logic: Real-time (current date) for Journey Ticket, minus 11 days for Season Ticket
        val bookingDateTime = if (isJourney) LocalDateTime.now() else LocalDateTime.now().minusDays(11)

        val largeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val dateTimeWithSeconds = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
        val dateTimeShort = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        val dateOnlyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

        val bookingTimeDisplay = bookingDateTime.format(largeFormat)
        findViewById<TextView>(R.id.tv_booking_date_time_large)?.text = bookingTimeDisplay
        findViewById<TextView>(R.id.tv_subcode)?.text = if (isJourney) "R17906" else "R17779"

        findViewById<TextView>(R.id.tv_type)?.text = if (isJourney) "Journey Ticket" else "Season Ticket"
        findViewById<TextView>(R.id.tv_uts)?.text = ticket.utsNumber

        // Stations & Route
        findViewById<TextView>(R.id.tv_src)?.text = ticket.sourceStation
        findViewById<TextView>(R.id.tv_dest)?.text = ticket.destinationStation
        findViewById<TextView>(R.id.tv_dist)?.text = "—${ticket.distanceKm}—"
        findViewById<TextView>(R.id.tv_via)?.text = ticket.viaRoute

        // Via / Booked on & Validity Dates
        val tvLblBookedOn = findViewById<TextView>(R.id.lbl_booked_on)
        val tvBookedOn = findViewById<TextView>(R.id.tv_booked_on)
        val tvLblVFrom = findViewById<TextView>(R.id.lbl_v_from)
        val tvVFrom = findViewById<TextView>(R.id.tv_v_from)
        val tvLblVTill = findViewById<TextView>(R.id.lbl_v_till)
        val tvVTill = findViewById<TextView>(R.id.tv_v_till)
        val tvFareSummary = findViewById<TextView>(R.id.tv_fare_summary)
        val tvIrCode = findViewById<TextView>(R.id.tv_ir_code)
        val tvJourneyDisclaimer = findViewById<TextView>(R.id.tv_journey_disclaimer)
        val layoutDashedCutout = findViewById<View>(R.id.layout_dashed_cutout)
        val layoutPassengerDetails = findViewById<View>(R.id.layout_passenger_details)

        // Dashed cutout line is present on BOTH Journey & Season tickets
        layoutDashedCutout?.visibility = View.VISIBLE

        if (isJourney) {
            // Journey Ticket Specific Layout
            tvLblBookedOn?.text = "Passenger"
            tvBookedOn?.text = ticket.passengerCount

            tvLblVFrom?.text = "Booked on"
            tvVFrom?.text = bookingDateTime.format(dateTimeWithSeconds)

            tvLblVTill?.text = "*Valid Till"
            tvVTill?.text = bookingDateTime.plusHours(1).format(dateTimeShort)

            tvFareSummary?.text = "${ticket.classType} | ${ticket.trainType} | JOURNEY | ${ticket.price}"

            tvIrCode?.text = ticket.irCode
            tvIrCode?.visibility = View.VISIBLE

            tvJourneyDisclaimer?.text = "*Valid for start of journey within 1 hour or until departure of the first train."
            tvJourneyDisclaimer?.visibility = View.VISIBLE

            layoutPassengerDetails?.visibility = View.GONE
        } else {
            // Season Ticket Specific Layout
            tvLblBookedOn?.text = "Booked on"
            tvBookedOn?.text = bookingDateTime.format(dateTimeShort)

            tvLblVFrom?.text = "Valid From"
            tvVFrom?.text = bookingDateTime.plusDays(1).format(dateOnlyFormat)

            tvLblVTill?.text = "*Valid Till"
            tvVTill?.text = bookingDateTime.plusMonths(1).minusDays(1).format(dateOnlyFormat)

            tvFareSummary?.text = "${ticket.ticketType} | ${ticket.trainType} | ${ticket.classType} | ${ticket.price}"

            tvIrCode?.visibility = View.GONE
            tvJourneyDisclaimer?.visibility = View.GONE

            layoutPassengerDetails?.visibility = View.VISIBLE

            // Passenger Details
            findViewById<TextView>(R.id.tv_name)?.text = user.name
            findViewById<TextView>(R.id.tv_passenger_age)?.text = "${user.age} years"
            findViewById<TextView>(R.id.tv_passenger_id_type)?.text = user.idType
            findViewById<TextView>(R.id.tv_passenger_id_num)?.text = user.idNumber
        }
    }

    private fun startTimer(millis: Long) {
        val totalMillis = millis.toInt()
        pbTimerMiddle.max = totalMillis
        pbTimerMiddle.progress = 0

        countDownTimer = object : CountDownTimer(millis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                tvTimer.setTime(minutes, seconds)

                val elapsedTime = totalMillis - millisUntilFinished.toInt()
                pbTimerMiddle.progress = elapsedTime
            }

            override fun onFinish() {
                tvTimer.setTime(0, 0)
                pbTimerMiddle.progress = totalMillis
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}