package com.oliwia.piwo.LocalisationService

import com.oliwia.piwo.User.User
import java.io.Serializable
import java.util.*

data class Location
    (val latitude : Double, val longitude: Double, val timestamp : Date) : Serializable
{
    companion object {
        fun emptyLocation(user: String) = Location(0.0, 0.0, Date())
    }
}