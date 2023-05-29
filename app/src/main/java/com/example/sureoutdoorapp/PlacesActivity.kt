package com.example.sureoutdoorapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.sureoutdoorapp.databinding.PlacesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PlacesActivity : AppCompatActivity() {

    var email: String = ""
    lateinit var database: FirebaseFirestore
    var dataArrayList = ArrayList<ListData?>()
    private lateinit var listData: ListData
    private lateinit var listAdapter: ListAdapter
    private lateinit var binding: PlacesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.places)

        supportActionBar?.hide()
        binding= PlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseFirestore.getInstance()
        //Botón para cerrar sesión

        //Recibe el correo del usuario de la actividad de donde viene
        email = intent.getStringExtra("email").toString()

        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a la configuración del perfil
        val setButton = findViewById<ImageButton>(R.id.settingsButton)

        setButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir al inicio

        val homeButton = findViewById<ImageButton>(R.id.homeButton)

        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        //Botón para ir a ver los lugares

        val placeButton = findViewById<ImageButton>(R.id.placesButton)

        placeButton.setOnClickListener{
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
        }

        //Botón para el mapa

        val mapButton = findViewById<ImageButton>(R.id.mapButton)

        mapButton.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a los chats

        val chatsButton = findViewById<ImageButton>(R.id.chatsButton)

        chatsButton.setOnClickListener{
            val intent = Intent(this, PlansActivity::class.java)
            startActivity(intent)
        }

        //Botón para programar un ejercicio grupal

        val groupButton = findViewById<ImageButton>(R.id.groupButton)

        groupButton.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

        //Botón para crear una nueva reseña

        val reviewButton = findViewById<Button>(R.id.newReview)

        reviewButton.setOnClickListener{
            val intent = Intent(this, AddReviewActivity::class.java)
            startActivity(intent)
        }

        var place: String? = String()
        var type: String? = String()
        var rating: Float = 0F
        database.collection("places").get().addOnSuccessListener { snapshot ->
            for (lugar in snapshot) {
                place = lugar.id
                assert(place != null)
                rating= lugar.get("rating").toString().toFloat()
                type=lugar.get("type").toString()
                listData= ListData(place.toString(),rating, type!!)
                dataArrayList.add(listData)
                listAdapter=ListAdapter(this,dataArrayList)
                binding.list.adapter=listAdapter
                binding.list.isClickable=true
                Log.v("Obtiene la lista", "Elementos de lista adquiridos")
            }
        }
        binding.list.onItemClickListener=AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent=Intent(this, InfoReviewActivity::class.java)
            intent.putExtra("Name", dataArrayList[i]?.place.toString())
            intent.putExtra("Calif", dataArrayList[i]?.rating?.toFloat())
            startActivity(intent)
        }

        //Botón para filtrar por parques

        val parkButton = findViewById<ImageButton>(R.id.parkButton)

        parkButton.setOnClickListener{
            var place: String? = String()
            var type: String? = String()
            var rating: Float = 0F
            listAdapter.clear()
            listAdapter.notifyDataSetChanged()
            database.collection("places").get().addOnSuccessListener { snapshot ->
                for (lugar in snapshot) {
                    place = lugar.id
                    assert(place != null)
                    rating= lugar.get("rating").toString().toFloat()
                    type=lugar.get("type").toString()
                    if(type == "park"){
                        listData= ListData(place.toString(),rating, type!!)
                        dataArrayList.add(listData)
                        listAdapter=ListAdapter(this,dataArrayList)
                        binding.list.adapter=listAdapter
                        binding.list.isClickable=true
                    }

                    Log.v("Obtiene la lista", "Elementos filtrados (parques)")
                }
            }
        }


        //Botón para filtrar por gimnasios

        val gymButton = findViewById<ImageButton>(R.id.gymButton)

        gymButton.setOnClickListener{
            var place: String? = String()
            var type: String? = String()
            var rating: Float = 0F
            listAdapter.clear()
            listAdapter.notifyDataSetChanged()
            database.collection("places").get().addOnSuccessListener { snapshot ->
                for (lugar in snapshot) {
                    place = lugar.id
                    assert(place != null)
                    rating= lugar.get("rating").toString().toFloat()
                    type=lugar.get("type").toString()
                    if(type == "gym"){
                        listData= ListData(place.toString(),rating, type!!)
                        dataArrayList.add(listData)
                        listAdapter=ListAdapter(this,dataArrayList)
                        binding.list.adapter=listAdapter
                        binding.list.isClickable=true
                    }

                    Log.v("Obtiene la lista", "Elementos filtrados (gimnasios)")
                }
            }
        }

        //Botón para filtrar por tiendas

        val storeButton = findViewById<ImageButton>(R.id.storeButton)

        storeButton.setOnClickListener{
            var place: String? = String()
            var type: String? = String()
            var rating: Float = 0F
            listAdapter.clear()
            listAdapter.notifyDataSetChanged()
            database.collection("places").get().addOnSuccessListener { snapshot ->
                for (lugar in snapshot) {
                    place = lugar.id
                    assert(place != null)
                    rating= lugar.get("rating").toString().toFloat()
                    type=lugar.get("type").toString()
                    if(type == "store"){
                        listData= ListData(place.toString(),rating, type!!)
                        dataArrayList.add(listData)
                        listAdapter=ListAdapter(this,dataArrayList)
                        binding.list.adapter=listAdapter
                        binding.list.isClickable=true
                    }

                    Log.v("Obtiene la lista", "Elementos filtrados (tiendas)")
                }
            }
        }

        //Botón para filtrar por rating
        binding.ratingFilter.isClickable=true
        binding.ratingFilter.setOnRatingBarChangeListener { ratingBar, fl, b ->
            var place: String? = String()
            var type: String? = String()
            var rating: Float = 0F
            listAdapter.clear()
            listAdapter.notifyDataSetChanged()
            database.collection("places").get().addOnSuccessListener { snapshot ->
                for (lugar in snapshot) {
                    place = lugar.id
                    assert(place != null)
                    rating= lugar.get("rating").toString().toFloat()
                    type=lugar.get("type").toString()
                    if(fl == rating){
                        listData= ListData(place.toString(),rating, type!!)
                        dataArrayList.add(listData)
                        listAdapter=ListAdapter(this,dataArrayList)
                        binding.list.adapter=listAdapter
                        binding.list.isClickable=true
                    }

                    Log.v("Obtiene la lista", "Elementos filtrados (estrellas)")
                }
            }
        }




    }


}

