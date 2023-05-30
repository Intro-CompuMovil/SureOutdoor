package com.example.sureoutdoorapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
//import com.example.sureoutdoorapp.SharedData.email
import com.example.sureoutdoorapp.databinding.SettingsBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage as FirebaseStorage

class SettingsActivity : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: SettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        //Inicializar Firebase
        FirebaseApp.initializeApp(this)

        //Inicializa la base
        database = FirebaseFirestore.getInstance()

        //Inicializa la autenticación
        auth = FirebaseAuth.getInstance()

        //Mostrar la información que viene de la base
        var email = intent.getStringExtra("email").toString()

        //Inicializar el Storage
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("users/$email/images/$email.jpg")


        var name = ""
        var lastName = ""
        var age = 0
        var target = 0

        //Poner la información de la base aquí
        database.collection("users").document(email).get().addOnSuccessListener {
            name = it.getString("name").toString()
            Log.i("Nombre es:", name)
            lastName = it.getString("lastname").toString()
            age = ((it.getLong("age")?.toInt() ?: Int) as Int)
            target = ((it.getLong("target")?.toInt() ?: Int) as Int)

            //Mostrar la información
            binding.nameEj.text = "$name"
            binding.lastNameEj.setText(lastName)
            binding.ageEj.setText(age.toString())
            binding.emailEj.setText(email)
            binding.targetEj.setText(target.toString())
        }
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            // Cargar la imagen con Glide utilizando la URL
            Glide.with(this)
                .load(imageUrl)
                .into(binding.personImage)
        }


        //Botón para cerrar sesión

        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Botón para editar el perfil

        val changeButton = findViewById<Button>(R.id.editProfileButton)

        changeButton.setOnClickListener{
            val intent = Intent(this, NewProfileActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

    }
}