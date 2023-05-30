package com.example.sureoutdoorapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sureoutdoorapp.databinding.InfoReviewBinding
import com.google.firebase.firestore.FirebaseFirestore

class InfoReviewActivity:AppCompatActivity() {

    private lateinit var binding: InfoReviewBinding
    lateinit var database: FirebaseFirestore
    var revArrayList = ArrayList<ListRev?>()
    private lateinit var listRev: ListRev
    private lateinit var listAdapter: ListRevAdapter
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_review)
        binding= InfoReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()
        val intent = this.intent
        if (intent!=null){
            val name=intent.getStringExtra("Name")
            val cal =intent.getFloatExtra("Calif",0F)

            binding.name.text= name
            binding.ratingBarGen.rating=cal
        }
        binding.imageReference.setImageResource(R.mipmap.im_exercise)
        var place: String? = String()
        var rating: Float = 0F
        var review: String? = String()
        database.collection("reviews").get().addOnSuccessListener { snapshot ->
            for (lugar in snapshot) {
                place = lugar.get("place").toString()
                rating= lugar.get("rating").toString().toFloat()
                review=lugar.get("opinion").toString()
                if(intent.getStringExtra("Name") == place){
                    listRev= ListRev(review!!,rating)
                    revArrayList.add(listRev)
                    listAdapter=ListRevAdapter(this,revArrayList)
                    binding.listRe.adapter=listAdapter
                    binding.listRe.isClickable=true
                }

                Log.v("Obtiene la lista", "Elementos de lista adquiridos")
            }
        }
    }
}