package com.oliwia.piwo.LocalisationService

import android.util.Log
import com.google.firebase.database.*
import com.oliwia.piwo.Firebase.FirebaseConnector
import com.oliwia.piwo.User.User
import java.util.*


class FirebaseLocator : ILocalise {
    override fun getLocalisation(user: String, callback: (User, Location) -> Unit) {
        val reference = dbConnector.getReference("$locationReferenceBase/$user")
        reference.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i(loggerTag, "onCancelled: $p0")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.i(loggerTag, "Data has been changed: $p0")
                    callback.invoke(User(user), Location(0.0, 0.0, Date()))
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

    companion object {
        private fun removeMailFromUser(user: String) = user.split("@").first()
        fun removeForbiddenCharactersFromEmail(email: String) : String =
                removeMailFromUser(email).replace(Regex("""[. [ # $ ] ]"""), "_")
    }
}