package com.example.railone.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("rail_one_prefs", Context.MODE_PRIVATE)

    fun getUserProfile(): UserProfile {
        val name = prefs.getString("user_name", "PAVAN BHAGVAT MHAISNE") ?: "PAVAN BHAGVAT MHAISNE"
        val mobile = prefs.getString("user_mobile", "9021223771") ?: "9021223771"
        val idType = prefs.getString("user_id_type", "Govt. issued Icard") ?: "Govt. issued Icard"
        val idNumber = prefs.getString("user_id_number", "691988272617") ?: "691988272617"
        val age = prefs.getInt("user_age", 23)

        return UserProfile(
            name = name,
            mobileNumber = mobile,
            idType = idType,
            idNumber = idNumber,
            age = age
        )
    }

    fun saveUserProfile(profile: UserProfile) {
        prefs.edit()
            .putString("user_name", profile.name)
            .putString("user_mobile", profile.mobileNumber)
            .putString("user_id_type", profile.idType)
            .putString("user_id_number", profile.idNumber)
            .putInt("user_age", profile.age)
            .apply()
    }

    fun getActiveTicket(): TicketData {
        val ticketJson = prefs.getString("active_ticket_json", null)
        val userProfile = getUserProfile()

        if (ticketJson.isNullOrEmpty()) {
            return TicketData(userProfile = userProfile)
        }

        return try {
            val json = JSONObject(ticketJson)
            TicketData(
                utsNumber = json.optString("utsNumber", "X06ZEE3074"),
                ticketType = json.optString("ticketType", "MONTHLY"),
                bookingDateTime = json.optString("bookingDateTime", ""),
                validFrom = json.optString("validFrom", ""),
                validTill = json.optString("validTill", ""),
                sourceStation = json.optString("sourceStation", "BELAPUR C.B.D"),
                destinationStation = json.optString("destinationStation", "VASHI"),
                viaRoute = json.optString("viaRoute", "1RT>>JNJ-SNCR"),
                distanceKm = json.optString("distanceKm", "10 km"),
                classType = json.optString("classType", "SECOND"),
                trainType = json.optString("trainType", "ORDINARY"),
                price = json.optString("price", "₹ 120.00"),
                userProfile = userProfile
            )
        } catch (e: Exception) {
            TicketData(userProfile = userProfile)
        }
    }

    fun saveActiveTicket(ticket: TicketData) {
        val json = JSONObject().apply {
            put("utsNumber", ticket.utsNumber)
            put("ticketType", ticket.ticketType)
            put("bookingDateTime", ticket.bookingDateTime)
            put("validFrom", ticket.validFrom)
            put("validTill", ticket.validTill)
            put("sourceStation", ticket.sourceStation)
            put("destinationStation", ticket.destinationStation)
            put("viaRoute", ticket.viaRoute)
            put("distanceKm", ticket.distanceKm)
            put("classType", ticket.classType)
            put("trainType", ticket.trainType)
            put("price", ticket.price)
        }

        prefs.edit()
            .putString("active_ticket_json", json.toString())
            .apply()
    }
}
