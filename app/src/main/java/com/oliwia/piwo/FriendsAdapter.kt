package com.oliwia.piwo

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.oliwia.piwo.User.User

/**
 * Created by Oliwia on 16.01.2019.
 */

class FriendsAdapter(private val context: Context, private val list: ArrayList<User>) : BaseAdapter(), ListAdapter {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(pos: Int): User {
        return list[pos]
    }

    override fun getItemId(pos: Int): Long {
        return list[pos].id
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.friends_row, null)
        }

        //Handle TextView and display string from your list
        val listItemTextName = view!!.findViewById<TextView>(R.id.textViewFriendName)
        listItemTextName.text = list[position].username

        return view;
    }


}