package com.example.sureoutdoorapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.example.sureoutdoorapp.R.id.*
import com.example.sureoutdoorapp.databinding.MainBinding
import com.example.sureoutdoorapp.databinding.RegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    val database = FirebaseFirestore.getInstance()
    //private val auth = FirebaseAuth.getInstance()

    private lateinit var binding: MainBinding

    @SuppressLint("WrongViewCast")
    //override fun onStart() {
        //super.onStart()
        //setContentView(R.layout.login)
    //}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.main)
        binding = MainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        //Inicializar Firebase
        FirebaseApp.initializeApp(this)

        //Creo variables para recibir lo de la base
        var name: String = ""
        var lastName: String = ""
        var age: String = ""
        var walk: String = ""
        var target: String = ""

        //Actualizar información con lo que viene de la base

        val email = intent.getStringExtra("email").toString()
        //Log.i("Correo es:", email)
        database.collection("users").get().addOnSuccessListener {snapshot->
            for(documento in snapshot){
                if(documento.id == email){
                    name = documento.getString("name").toString()
                    Log.i("Adentro", "Entramos")
                    lastName = documento.getString("lastname").toString()
                    age = documento.getString("age").toString()
                    walk = documento.getString("walk").toString()
                    target = documento.getString("target").toString()
                    //Falta foto
                    //Log.i("nombre", name)
                }
            }
            //Actualizar info con nombre
            binding.infoS.text = "¡Hola $name! Esta es la cantidad de pasos que llevas hoy..."

            //Actualizar cantidad de pasos completados
            binding.textPaso.setText(walk+" pasos completados")
            //Actualizar cantidad de pasos faltantes
            var w1 = walk.toInt()
            var t1  = target.toInt()
            var falta = t1 - w1
            binding.textPlus.setText("Te faltan "+falta+" para completar tu meta, ¡vamos que sí se puede!")
            //Actualizar dato curioso
            binding.extraData.setText("Tu meta es equivalente a 35 viajes ida y vuelta a la luna, ¿Te gusta el espacio o qué?")

        }

        //Botón para cerrar sesión
        binding.returnButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a la configuración del perfil
        binding.settingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a ver los lugares
        binding.placesButton.setOnClickListener{
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
        }

        //Botón para el mapa
        binding.mapButton.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a los planes
        binding.plansButton.setOnClickListener{
            val intent = Intent(this, PlansActivity::class.java)
            startActivity(intent)
        }

        //Botón para programar un ejercicio grupal
        binding.groupButton.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

    }
}
