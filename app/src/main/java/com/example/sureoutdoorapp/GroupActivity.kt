package com.example.sureoutdoorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sureoutdoorapp.databinding.GroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//import androidx.appcompat.widget.Toolbar

class GroupActivity : AppCompatActivity() {

    lateinit var database: FirebaseFirestore

    private lateinit var binding: GroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.group)
        binding = GroupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        //Recibe el correo del usuario actual
        var email = intent.getStringExtra("email").toString()

        //Inicializa la base
        database = FirebaseFirestore.getInstance()

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

        //Botón para ir a ver los ejercicios grupales

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

        //Botón para enviar la convocatoria a la plataforma

        val sendAllButton = findViewById<Button>(R.id.sendAllButton)

        sendAllButton.setOnClickListener{

            //Verificar que todos los campos estén llenos
            if(binding.planName.text.isNotEmpty() && binding.placeName.text.isNotEmpty() && binding.dateEle.text.isNotEmpty() &&
                    binding.timeEle.text.isNotEmpty()){
                //Se puede proceder a guardar
                val data = hashMapOf("name" to binding.planName.text.toString(),
                    "place" to binding.placeName.text.toString(),
                    "date" to binding.dateEle.text.toString(),
                    "time" to binding.timeEle.text.toString(),
                    "person" to email)
                database.collection("plans").document(binding.planName.text.toString()).set(data).addOnSuccessListener {
                    //Se pudo guardar satisfactoriamente
                    Toast.makeText(applicationContext, "Convocatoria compartida a todos", Toast.LENGTH_LONG).show()
                }
                    .addOnFailureListener{
                        Toast.makeText(applicationContext, "Error al crear el plan, intente de nuevo", Toast.LENGTH_LONG).show()
                    }
            }else{
                Toast.makeText(applicationContext, "Llene todos los campos para poder crear el plan", Toast.LENGTH_LONG).show()
            }

        }

    }
}