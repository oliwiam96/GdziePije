package com.oliwia.piwo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class FriendsActivity : AppCompatActivity() {

    var friendsAdapter:FriendsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        friendsAdapter = FriendsAdapter(this)

        val lView = findViewById<ListView>(R.id.listViewFriends)
        lView.adapter = friendsAdapter

    }
}
