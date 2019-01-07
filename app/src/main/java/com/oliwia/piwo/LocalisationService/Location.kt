package com.oliwia.piwo.LocalisationService

import com.google.android.gms.maps.model.LatLng
import com.oliwia.piwo.User.User
import java.io.Serializable
import java.util.*

data class Location
    (val latitude : Double, val longitude: Double, val timestamp : Date) : Serializable
{
    fun toLatLng(): LatLng = LatLng(latitude, longitude)

    override fun toString(): String {
        return "Lat: $latitude, Lon: $longitude, Timestamp: $timestamp"
    }
    companion object {
        fun emptyLocation(user: String) = Location(0.0, 0.0, Date())
    }
}