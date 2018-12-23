package com.oliwia.piwo.Firebase

import com.google.firebase.database.FirebaseDatabase

class FirebaseConnector {

    fun getReference(referenceName : String)  = database.getReference(referenceName)

    companion object {
        private val database : FirebaseDatabase by lazy {
            FirebaseDatabase.getInstance()
        }
    }
}