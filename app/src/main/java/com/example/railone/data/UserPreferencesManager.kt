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
            val category = json.optString("ticketCategory", "SEASON")
            TicketData(
                utsNumber = json.optString("utsNumber", if (category == "JOURNEY") "X0HNEG00D8" else "X07DEF61F8"),
                ticketCategory = category,
                ticketType = json.optString("ticketType", if (category == "JOURNEY") "JOURNEY" else "MONTHLY"),
                bookingDateTime = json.optString("bookingDateTime", ""),
                validFrom = json.optString("validFrom", ""),
                validTill = json.optString("validTill", ""),
                sourceStation = json.optString("sourceStation", if (category == "JOURNEY") "KHARGHAR" else "PANVEL"),
                destinationStation = json.optString("destinationStation", "VASHI"),
                viaRoute = json.optString("viaRoute", if (category == "JOURNEY") "------" else "1RT>>JNJ-SNCR"),
                distanceKm = json.optString("distanceKm", if (category == "JOURNEY") "12 km" else "21 km"),
                classType = json.optString("classType", "SECOND"),
                trainType = json.optString("trainType", "ORDINARY"),
                price = json.optString("price", if (category == "JOURNEY") "₹ 10.00" else "₹ 235.00"),
                passengerCount = json.optString("passengerCount", "1 Adult, 0 Child"),
                irCode = json.optString("irCode", "IR:27AAAGM0289C2ZI"),
                userProfile = userProfile
            )
        } catch (e: Exception) {
            TicketData(userProfile = userProfile)
        }
    }

    fun saveActiveTicket(ticket: TicketData) {
        val json = JSONObject().apply {
            put("utsNumber", ticket.utsNumber)
            put("ticketCategory", ticket.ticketCategory)
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
            put("passengerCount", ticket.passengerCount)
            put("irCode", ticket.irCode)
        }

        prefs.edit()
            .putString("active_ticket_json", json.toString())
            .apply()
    }
}
