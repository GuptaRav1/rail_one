package com.example.railone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.railone.data.UserPreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var prefsManager: UserPreferencesManager
    private lateinit var tvGreeting: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        
        // We trigger the exit animation immediately so the scale-down 
        // starts right at the beginning of the splash screen.
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView
            
            // Initial state: scaled up
            iconView.scaleX = 1.5f
            iconView.scaleY = 1.5f
            
            // Animate scale down to 1.0 over the full 2-second duration
            iconView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(2000)
                .setInterpolator(DecelerateInterpolator())
                .start()

            // Gradually fade out the splash screen background at the end
            splashScreenView.view.animate()
                .alpha(0f)
                .setStartDelay(1500) // Stay solid for 1.5s
                .setDuration(500)     // Fade out during the last 0.5s
                .withEndAction {
                    splashScreenView.remove()
                }
                .start()
        }
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefsManager = UserPreferencesManager(this)
        tvGreeting = findViewById(R.id.tv_greeting)

        // Make status bar icons dark because the background is light
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        findViewById<View>(R.id.card_unreserved)?.setOnClickListener {
            startActivity(Intent(this, BookTicketActivity::class.java))
        }

        findViewById<BottomNavigationView>(R.id.bottomNavigation)
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_bookings -> {
                        startActivity(Intent(this, BookingsActivity::class.java))
                        true
                    }
                    R.id.navigation_you -> {
                        startActivity(Intent(this, YouActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
    }

    override fun onResume() {
        super.onResume()
        val userProfile = prefsManager.getUserProfile()
        tvGreeting.text = "Hi, ${userProfile.name}!"
    }
}
