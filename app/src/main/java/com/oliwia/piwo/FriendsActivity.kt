package com.oliwia.piwo

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.oliwia.piwo.User.User

class FriendsActivity : AppCompatActivity() {

    var friendsAdapter: FriendsAdapter? = null
    val EXTRA_FRIENDS = "FRIENDS"
    val FRIENDS_ID = "ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        val extras = intent.extras ?: return
        val friends = extras.getSerializable(EXTRA_FRIENDS) as ArrayList<User>

        friendsAdapter = FriendsAdapter(this, friends)

        val lView = findViewById<ListView>(R.id.listViewFriends)
        lView.adapter = friendsAdapter


        lView.setOnItemClickListener { adapterView, view, position, id ->
            run {
                val data = Intent()
                data.putExtra(FRIENDS_ID, id.toString())
                setResult(Activity.RESULT_OK, data)
                super.finish()
            }
        }
    }
}
