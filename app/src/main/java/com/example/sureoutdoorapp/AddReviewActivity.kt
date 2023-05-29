package com.example.sureoutdoorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sureoutdoorapp.databinding.AddReviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


//import com.example.sureoutdoor.databinding.AddReviewBinding

class AddReviewActivity : AppCompatActivity() {

    //private lateinit var binding: AddReviewBinding
    //private val CAMERA_REQUEST_CODE = 1
    var binding: AddReviewBinding? = null

    lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_review)

        supportActionBar?.hide()
        database = FirebaseFirestore.getInstance()
        var Greeting: Int =0

        var place: String? = String()

        var places= ArrayList<String>()
        places.add("")
        val spin= findViewById<Spinner>(R.id.siteName)
        database.collection("places").get().addOnSuccessListener { snapshot ->
            for (lugar in snapshot) {
                place = lugar.id
                assert(place != null)
                places.add(place.toString())
                Log.v("Obtiene la lista", "Elementos de lista adquiridos")
            }
        }

        spin.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_item,places)


        //Botón cerrar sesión

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
        //Botón al añadir una reseña
        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                Greeting=i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        val reviewButton = findViewById<Button>(R.id.sendReviewButton)
        reviewButton.setOnClickListener{
            val reviewPlace: String = places[Greeting]
            var simpleRatingBar =
                findViewById<View>(R.id.ratingB) as RatingBar
            var ratingNumber = simpleRatingBar.rating
            if (reviewPlace != "") {
                val data = hashMapOf(
                    "place" to reviewPlace,
                    "opinion" to findViewById<EditText>(R.id.infoReview).text.toString(),
                    "rating" to ratingNumber
                )

                database.collection("reviews").document().set(data).addOnSuccessListener {
                    //Datos guardados correctamente
                    Toast.makeText(applicationContext, "Reseña compartida", Toast.LENGTH_LONG)
                        .show()
                }
            }
            else{
                Toast.makeText(applicationContext, "No haz seleccionado un lugar de reseña", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

}