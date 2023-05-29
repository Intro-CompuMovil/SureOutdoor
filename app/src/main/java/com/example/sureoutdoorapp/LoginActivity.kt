package com.example.sureoutdoorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.sureoutdoorapp.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    //lateinit var loadingGif : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.login)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        //loadingGif = findViewById(R.id.sports_gif)
        //loadingGif.visibility = View.VISIBLE

        //Cargar pantalla aquí

        //loadingGif.visibility = View.GONE

        //Botón para iniciar sesión
        binding.loginButton.setOnClickListener{

            //Verificar que los datos son correctos
            if(binding.emailLogin.text.isNotEmpty() && binding.passwordLogin.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailLogin.text.toString(), binding.passwordLogin.text.toString())
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            //Información correcta, se carga la pantalla principal
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("email", binding.emailLogin.text.toString())
                            startActivity(intent)
                        }else{
                            Toast.makeText(applicationContext, "Error al iniciar sesión, verifique credenciales", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        //Botón para registrarse
        binding.registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}