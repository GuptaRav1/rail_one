package com.example.railone

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

        bindDynamicData()

        tvTimer = findViewById(R.id.tv_timer)
        pbTimerMiddle = findViewById(R.id.pb_timer_middle)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        startTimer(5 * 60 * 1000) // 5 minutes
    }

    private fun bindDynamicData() {
        val ticket = prefsManager.getActiveTicket()
        val user = ticket.userProfile

        // Top Header
        findViewById<TextView>(R.id.tv_header_mobile)?.text = "Mobile: ${user.mobileNumber}"

        // Greeting
        findViewById<TextView>(R.id.tv_greeting)?.text = "Thank You ${user.name}, Happy Journey !"

        // Rolling 11-day booking date logic (moves relative to current day)
        val dynamicBookingDateTime = LocalDateTime.now().minusDays(11)
        val largeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val standardFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        val dateFormatOnly = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

        val bookingTimeDisplay = dynamicBookingDateTime.format(largeFormat)
        findViewById<TextView>(R.id.tv_booking_date_time_large)?.text = bookingTimeDisplay
        findViewById<TextView>(R.id.tv_type)?.text = ticket.ticketType
        findViewById<TextView>(R.id.tv_uts)?.text = ticket.utsNumber

        // Stations & Route
        findViewById<TextView>(R.id.tv_src)?.text = ticket.sourceStation
        findViewById<TextView>(R.id.tv_dest)?.text = ticket.destinationStation
        findViewById<TextView>(R.id.tv_dist)?.text = "— ${ticket.distanceKm} —"
        findViewById<TextView>(R.id.tv_via)?.text = ticket.viaRoute

        // Dates updated relative to current day (11 days ago to +1 month)
        val bookedOnDisplay = dynamicBookingDateTime.format(standardFormat)
        val validFromDisplay = dynamicBookingDateTime.format(dateFormatOnly)
        val validTillDisplay = dynamicBookingDateTime.plusMonths(1).minusDays(1).format(dateFormatOnly)

        findViewById<TextView>(R.id.tv_booked_on)?.text = bookedOnDisplay
        findViewById<TextView>(R.id.tv_v_from)?.text = validFromDisplay
        findViewById<TextView>(R.id.tv_v_till)?.text = validTillDisplay

        // Fare Line
        val fareSummary = "${ticket.ticketType} | ${ticket.trainType} | ${ticket.classType} | ${ticket.price}"
        findViewById<TextView>(R.id.tv_fare_summary)?.text = fareSummary

        // Passenger Details
        findViewById<TextView>(R.id.tv_name)?.text = user.name
        findViewById<TextView>(R.id.tv_passenger_age)?.text = "${user.age} years"
        findViewById<TextView>(R.id.tv_passenger_id_type)?.text = user.idType
        findViewById<TextView>(R.id.tv_passenger_id_num)?.text = user.idNumber
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
