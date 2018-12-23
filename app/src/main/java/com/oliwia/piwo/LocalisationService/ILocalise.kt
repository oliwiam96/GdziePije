package com.oliwia.piwo.LocalisationService

interface ILocalise {
    fun getLocalisation(user: String, callback: (Location) -> Unit)
    fun putLocalisation(user: String, location: Location)
}