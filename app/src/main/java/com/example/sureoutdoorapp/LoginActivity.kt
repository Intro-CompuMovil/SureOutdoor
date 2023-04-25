package com.example.sureoutdoorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class LoginActivity : AppCompatActivity() {

    lateinit var loadingGif : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        supportActionBar?.hide()

        //Botón para iniciar sesión

        loadingGif = findViewById(R.id.sports_gif)
        loadingGif.visibility = View.VISIBLE

        //Cargar pantalla aquí

        loadingGif.visibility = View.GONE

        val startButton = findViewById<Button>(R.id.loginButton)

        startButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}