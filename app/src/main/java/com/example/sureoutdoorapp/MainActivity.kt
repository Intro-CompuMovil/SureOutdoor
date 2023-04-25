package com.example.sureoutdoorapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.sureoutdoorapp.R.id.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    //override fun onStart() {
        //super.onStart()
        //setContentView(R.layout.login)
    //}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        supportActionBar?.hide()

        //Botón para el retorno

        val loginButton = findViewById<ImageButton>(returnButton)

        loginButton.setOnClickListener{
            finish()
        }

        //Botón para ir a la configuración del perfil
        val setButton = findViewById<ImageButton>(settingsButton)

        setButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a ver los lugares

        val placeButton = findViewById<ImageButton>(placesButton)

        placeButton.setOnClickListener{
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
        }

        //Botón para el mapa

        val mapButton = findViewById<ImageButton>(mapButton)

        mapButton.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a los planes

        val chatsButton = findViewById<ImageButton>(plansButton)

        chatsButton.setOnClickListener{
            val intent = Intent(this, PlansActivity::class.java)
            startActivity(intent)
        }

        //Botón para programar un ejercicio grupal

        val groupButton = findViewById<ImageButton>(groupButton)

        groupButton.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

    }
}
