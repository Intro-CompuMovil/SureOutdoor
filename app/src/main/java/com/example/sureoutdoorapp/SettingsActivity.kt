package com.example.sureoutdoorapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.example.sureoutdoorapp.databinding.SettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        //Cargar la imagen
        val imageBitmap = SharedData.image
        val personImage = findViewById<ImageView>(R.id.person_image)
        personImage.setImageBitmap(imageBitmap)

        //Cargar la nueva información
        val name = SharedData.name
        val lastName = SharedData.lastName
        val email = SharedData.email
        val age = SharedData.age
        binding.nameEj.setText(name)
        binding.lastNameEj.setText(lastName)
        binding.emailEj.setText(email)
        binding.ageEj.setText(age)


        //Botón para el retorno

        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener{
            onBackPressed()
        }

        //Botón para editar el perfil

        val changeButton = findViewById<Button>(R.id.editProfileButton)

        changeButton.setOnClickListener{
            val intent = Intent(this, NewProfileActivity::class.java)
            startActivity(intent)
        }

    }
}
object SharedData {
    var image: Bitmap? = null
    var name: String = "Pedro"
    var lastName: String = "Ramirez"
    var email: String = "pepesierra@gmail.com"
    var age: String = "23"
}