package com.example.sureoutdoorapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat

class ListAdapter (context: Context, dataArrayList: ArrayList<ListData?>?):
    ArrayAdapter<ListData?>(context,R.layout.place_list,dataArrayList!!){
    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup):View {
        var view = convertView
        val listData = getItem(position)
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.place_list, parent, false)
        }
        val listName = view!!.findViewById<TextView>(R.id.text_image)
        val listIcon = view.findViewById<ImageView>(R.id.icon_a)
        val listCalif = view.findViewById<RatingBar>(R.id.calif)

        listName.text=listData?.place
        when(listData?.type){
            "gym" ->{
                listIcon.setImageResource(R.mipmap.ic_gym_foreground)
                listIcon.setBackgroundColor(Color.BLUE)
            }
            "park" ->{
                listIcon.setImageResource(R.mipmap.ic_park_foreground)
                listIcon.setBackgroundColor(Color.parseColor("#00796B"))
            }
        }
        listCalif.rating=listData!!.rating
        listCalif.isClickable=false
        return view

    }
}