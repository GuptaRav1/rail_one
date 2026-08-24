package com.example.railone

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class TicketDetailsActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_details)

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
                tvTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                finish() // Close activity when timer ends as implied by "Dynamic preview will close in"
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
