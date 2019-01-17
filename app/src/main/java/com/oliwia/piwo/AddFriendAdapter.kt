package com.oliwia.piwo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.*
import com.oliwia.piwo.User.User

class AddFriendAdapter(private val context: Context,private val list: ArrayList<String>): BaseAdapter() {
    private var adaptrDynaListener: View.OnClickListener? = null
    override fun getItemId(p0: Int): Long {
       return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.person_row, null)
        }

        //Handle TextView and display string from your list
        val listItemTextName = view!!.findViewById<TextView>(R.id.textViewFriendName)
        val button = view!!.findViewById<Button>(R.id.addFriendButton).setOnClickListener(View.OnClickListener{
            fun onClick(v:View){
                (v as Button).setText("chuj")

            }
        })
        listItemTextName.text = list[position].toString()

        return view;
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(pos: Int): String {
        return list[pos]
    }



}