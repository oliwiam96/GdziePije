package com.oliwia.piwo
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ListView
import android.widget.Toast
import com.oliwia.piwo.User.User
import android.widget.TextView



class AddFriendActivity :AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
        val people = arrayListOf<String>()
        people.add("Mateusz Urbaniak")
        people.add("Marek Wydmuch")
        val people2 = arrayListOf<String>()
        val people3 = arrayListOf<String>()
        people3.add("Mateusz Urbaniak")
        var addFirendAdapter = AddFriendAdapter(this, people)
        var addFirendAdapter2 = AddFriendAdapter(this, people2)
        var addFirendAdapter3 = AddFriendAdapter(this, people3)
        val textView = findViewById(R.id.searchBox) as TextView
        textView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(s.isNotEmpty()){
                if(s[0].equals('m',true)) {
                    val lView = findViewById<ListView>(R.id.peopleList)
                    lView.adapter = addFirendAdapter
                }

                else{
                    val lView = findViewById<ListView>(R.id.peopleList)
                    lView.adapter = addFirendAdapter2

            }
                if(s.startsWith("mat",true)){
                    val lView = findViewById<ListView>(R.id.peopleList)
                    lView.adapter = addFirendAdapter3
                }}
                else{
                    val lView = findViewById<ListView>(R.id.peopleList)
                    lView.adapter = addFirendAdapter2
                }
        }})
    }
}
