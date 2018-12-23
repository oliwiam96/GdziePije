package com.oliwia.piwo.LocalisationService

import android.util.Log
import com.google.firebase.database.*
import com.oliwia.piwo.Firebase.FirebaseConnector
import java.util.*


class FirebaseLocator : ILocalise {
    override fun getLocalisation(user: String, callback: (Location) -> Unit) {
        val reference = dbConnector.getReference("$locationReferenceBase/$user")
        reference.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i(loggerTag, "onCancelled: $p0")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.i(loggerTag, "Data has been changed: $p0")
                    callback.invoke(Location(1f, 2f, user, Date()))
                }
            }
        )
    }

    override fun putLocalisation(user: String, location: Location) {
        val reference = dbConnector.getReference("$locationReferenceBase/$user")
        reference.setValue(location)
    }

    private val dbConnector : FirebaseConnector by lazy {
        FirebaseConnector()
    }

    private val locationReferenceBase = "users-location"
    private val loggerTag = "FirebaseLocator"
}