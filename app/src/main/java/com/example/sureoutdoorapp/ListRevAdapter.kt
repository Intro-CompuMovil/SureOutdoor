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

class ListRevAdapter (context: Context, dataArrayList: ArrayList<ListRev?>?):
    ArrayAdapter<ListRev?>(context,R.layout.place_list,dataArrayList!!){
    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup):View {
        var view = convertView
        val listRev = getItem(position)
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.general_reviews, parent, false)
        }
        val listReview = view!!.findViewById<TextView>(R.id.text_image)
        val listCal=view.findViewById<RatingBar>(R.id.calif)

        listReview.text=listRev?.review
        listCal.rating=listRev!!.rating
        listCal.isClickable=false
        return view

    }
}