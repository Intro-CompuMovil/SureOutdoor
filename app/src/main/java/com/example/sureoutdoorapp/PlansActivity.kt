package com.example.sureoutdoorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth

class PlansActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plans)

        supportActionBar?.hide()

        //Recibe el correo del usuario actual
        var email = intent.getStringExtra("email").toString()

        //Botón para cerrar sesión

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
            intent.putExtra("email", email)
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

        //Botón para ir a ver los planes

        val chatsButton = findViewById<ImageButton>(R.id.chatsButton)

        chatsButton.setOnClickListener{
            val intent = Intent(this, PlansActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        //Botón para programar un ejercicio grupal

        val groupButton = findViewById<ImageButton>(R.id.groupButton)

        groupButton.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }
}