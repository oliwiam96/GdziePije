package com.oliwia.piwo.LocalisationService

import com.oliwia.piwo.User.User

interface ILocalise {
    fun getLocalisation(user: String, callback: (User, Location) -> Unit)
    fun putLocalisation(user: String, location: Location)
}