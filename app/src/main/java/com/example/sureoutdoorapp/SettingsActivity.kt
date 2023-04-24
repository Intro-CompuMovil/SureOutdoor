package com.example.sureoutdoorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

//import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        supportActionBar?.hide()

        //Botón para el retorno

        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener{
            onBackPressed()
        }

        //Botón para editar el perfil

        val changeButton = findViewById<Button>(R.id.editProfileButton)

        changeButton.setOnClickListener{
            Toast.makeText(applicationContext, "En desarrollo...", Toast.LENGTH_LONG).show()
        }

    }
}