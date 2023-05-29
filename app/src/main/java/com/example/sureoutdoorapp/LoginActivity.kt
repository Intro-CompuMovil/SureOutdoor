package com.example.sureoutdoorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.sureoutdoorapp.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    private lateinit var mAuth: FirebaseAuth

    //lateinit var loadingGif : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.login)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        //Inicializar Firebase
        mAuth = FirebaseAuth.getInstance()

        //correo
        //var email = binding.emailLogin.text.toString()
        //Log.i("correo", binding.emailLogin.text.toString())

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

        //Botón para restablecer contraseña
        binding.resetPassword.setOnClickListener{
            if(binding.emailLogin.text.toString().isNotEmpty()){
                //Se puede resetear
                resetPassword(binding.emailLogin.text.toString())
            }else{
                Toast.makeText(applicationContext, "Debe ingresar el correo para poder reestablecer la contraseña", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun resetPassword(em: String){
        mAuth.setLanguageCode("es")
        mAuth.sendPasswordResetEmail(em).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(applicationContext, "Revise su correo, se le envío link de reestablecimiento", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, "Error al enviar el correo de reestablecimiento, intente nuevamente", Toast.LENGTH_LONG).show()
            }
        }
    }
}