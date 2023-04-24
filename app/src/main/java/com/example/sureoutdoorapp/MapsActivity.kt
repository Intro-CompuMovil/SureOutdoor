package com.example.sureoutdoorapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MapsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)

        supportActionBar?.hide()

        //Botón para regresar

        val returnButton = findViewById<ImageButton>(R.id.imageButton)

        returnButton.setOnClickListener{
            onBackPressed()
        }

        //Botón para ir a configuración de la cuenta

        val setButton = findViewById<ImageButton>(R.id.imageButton2)

        setButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir al inicio

        val homeButton = findViewById<ImageButton>(R.id.imageButton3)

        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a los lugares

        val placeButton = findViewById<ImageButton>(R.id.imageButton4)

        placeButton.setOnClickListener{
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a los chats

        val chatsButton = findViewById<ImageButton>(R.id.imageButton6)

        chatsButton.setOnClickListener{
            val intent = Intent(this, PlansActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a programar un ejercicio grupal

        val groupButton = findViewById<ImageButton>(R.id.imageButton7)

        groupButton.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

    }
}