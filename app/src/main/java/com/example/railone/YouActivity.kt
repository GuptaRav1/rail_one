package com.example.railone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.railone.data.UserProfile
import com.example.railone.data.UserPreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class YouActivity : AppCompatActivity() {

    private lateinit var prefsManager: UserPreferencesManager
    private lateinit var tvName: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvId: TextView
    private lateinit var tvAge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_you)

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

        tvName = findViewById(R.id.tv_profile_name)
        tvMobile = findViewById(R.id.tv_profile_mobile)
        tvId = findViewById(R.id.tv_profile_id)
        tvAge = findViewById(R.id.tv_profile_age)

        loadProfileData()

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_edit_profile).setOnClickListener {
            showEditProfileDialog()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.navigation_you
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    finish()
                    true
                }
                R.id.navigation_bookings -> {
                    startActivity(Intent(this, BookingsActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }
    }

    private fun loadProfileData() {
        val profile = prefsManager.getUserProfile()
        tvName.text = profile.name
        tvMobile.text = profile.mobileNumber
        tvId.text = "${profile.idType} (${profile.idNumber})"
        tvAge.text = "${profile.age} yrs"
    }

    private fun showEditProfileDialog() {
        val currentProfile = prefsManager.getUserProfile()

        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val etName = layout.findViewById<EditText>(R.id.et_edit_name)
        val etMobile = layout.findViewById<EditText>(R.id.et_edit_mobile)
        val etIdType = layout.findViewById<EditText>(R.id.et_edit_id_type)
        val etIdNumber = layout.findViewById<EditText>(R.id.et_edit_id_number)
        val etAge = layout.findViewById<EditText>(R.id.et_edit_age)

        etName.setText(currentProfile.name)
        etMobile.setText(currentProfile.mobileNumber)
        etIdType.setText(currentProfile.idType)
        etIdNumber.setText(currentProfile.idNumber)
        etAge.setText(currentProfile.age.toString())

        val dialog = MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert
        )
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val newName = etName.text.toString().trim().ifEmpty { currentProfile.name }
                val newMobile = etMobile.text.toString().trim().ifEmpty { currentProfile.mobileNumber }
                val newIdType = etIdType.text.toString().trim().ifEmpty { currentProfile.idType }
                val newIdNumber = etIdNumber.text.toString().trim().ifEmpty { currentProfile.idNumber }
                val newAge = etAge.text.toString().toIntOrNull() ?: currentProfile.age

                val updatedProfile = UserProfile(
                    name = newName,
                    mobileNumber = newMobile,
                    idType = newIdType,
                    idNumber = newIdNumber,
                    age = newAge
                )

                prefsManager.saveUserProfile(updatedProfile)
                loadProfileData()
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton?.setTextColor(Color.parseColor("#0066FF"))
            positiveButton?.textSize = 16f

            negativeButton?.setTextColor(Color.parseColor("#757575"))
            negativeButton?.textSize = 16f
        }

        dialog.show()
    }
}
