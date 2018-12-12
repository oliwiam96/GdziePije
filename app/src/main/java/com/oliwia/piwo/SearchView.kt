package com.oliwia.piwo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_search_view.*

class SearchView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)
        getTextFromLookup()

        editText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(typedText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                typedText?.let {
                    mockResults(it)
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun getTextFromLookup(){
        intent.extras.get(LOOKUP_TEXT)?.let {
            val text = it.toString()
            if(!text.isEmpty()){
                findViewById<EditText>(R.id.editText)?.setText(text)
                mockResults(text)
            }
        }
    }
    private fun mockResults(searchText: CharSequence){
        when(searchText.first()){
            'p' -> p_constraint.visibility = View.VISIBLE
            else -> {
                p_constraint.visibility = View.INVISIBLE
            }
        }
    }
}
