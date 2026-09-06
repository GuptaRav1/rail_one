package com.example.railone.data

data class TicketData(
    val utsNumber: String = "X06ZEE3074",
    val ticketType: String = "MONTHLY",
    val bookingDateTime: String = "",
    val validFrom: String = "",
    val validTill: String = "",
    val sourceStation: String = "BELAPUR C.B.D",
    val destinationStation: String = "VASHI",
    val viaRoute: String = "1RT>>JNJ-SNCR",
    val distanceKm: String = "10 km",
    val classType: String = "SECOND",
    val trainType: String = "ORDINARY",
    val price: String = "₹ 120.00",
    val userProfile: UserProfile = UserProfile()
)
