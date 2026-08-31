package com.example.railone

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TicketDetailsActivity : AppCompatActivity() {

    private lateinit var tvTimer: RollingTimerView
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_details)

        // Set dynamic dates (Booking date = Now - 11 days)
        val bookingDateTime = LocalDateTime.now().minusDays(11)
        
        // Formats
        val largeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val standardFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        val dateFormatOnly = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

        findViewById<TextView>(R.id.tv_booking_date_time_large).text = bookingDateTime.format(largeFormat)
        findViewById<TextView>(R.id.tv_booked_on).text = bookingDateTime.format(standardFormat)
        findViewById<TextView>(R.id.tv_v_from).text = bookingDateTime.format(dateFormatOnly)
        
        // Valid Till = 1 month from booking date minus 1 day
        val validTill = bookingDateTime.plusMonths(1).minusDays(1)
        findViewById<TextView>(R.id.tv_v_till).text = validTill.format(dateFormatOnly)

        tvTimer = findViewById(R.id.tv_timer)

        findViewById<android.view.View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        startTimer(5 * 60 * 1000) // 5 minutes
    }

    private fun startTimer(millis: Long) {
        countDownTimer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                tvTimer.setTime(minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.setTime(0, 0)
                finish() // Close activity when timer ends as implied by "Dynamic preview will close in"
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
