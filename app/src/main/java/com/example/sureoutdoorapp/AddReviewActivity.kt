package com.example.sureoutdoorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.example.sureoutdoor.databinding.AddReviewBinding

class AddReviewActivity : AppCompatActivity() {

    //private lateinit var binding: AddReviewBinding
    //private val CAMERA_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_review)

        supportActionBar?.hide()

        //Botón para el retorno
        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener{
            onBackPressed()
        }

        //Botón para ir a la configuración del perfil
        val setButton = findViewById<ImageButton>(R.id.settingsButton)

        setButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //Botón al añadir una reseña
        val reviewButton = findViewById<Button>(R.id.sendReviewButton)

        reviewButton.setOnClickListener{
            Toast.makeText(applicationContext, "Reseña compartida", Toast.LENGTH_LONG).show()
        }

    }
}