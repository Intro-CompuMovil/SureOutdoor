package com.example.sureoutdoorapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sureoutdoorapp.R.id.*
import com.example.sureoutdoorapp.databinding.MainBinding
import com.example.sureoutdoorapp.databinding.RegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity(), SensorEventListener {

    //Base de datos
    lateinit var database: FirebaseFirestore
    //private val auth = FirebaseAuth.getInstance()

    //Sensor de pasos
    private var sensorManager: SensorManager? = null
    private var walking = false
    private var rein = 0F

    //Email del usuario actual
    private var email: String = ""
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth

    //Para el permiso
    companion object {

        private const val ACTIVITY_REQUEST_CODE = 1
    }

    //Binding de la clase
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

        //Inicializa la base
        database = FirebaseFirestore.getInstance()

        //Inicializa la autenticación
        auth = FirebaseAuth.getInstance()


        //Permiso para el acceso a la actividad física
        phisicalActivity()

        //Inicializa el sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //Correo actual
        email = intent.getStringExtra("email").toString()

        //Botón para cerrar sesión
        binding.returnButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //Parar lectura del sensor
            sensorManager!!.unregisterListener(this)
            walking = false
            rein = 0F
            //Email vuelve a ser vacío
            email = ""
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Botón para ir a la configuración del perfil
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        //Botón para ir a ver los lugares
        binding.placesButton.setOnClickListener {
            val intent = Intent(this, PlacesActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        //Botón para el mapa
        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        //Botón para ir a los planes
        binding.plansButton.setOnClickListener {
            val intent = Intent(this, PlansActivity::class.java)
            startActivity(intent)
        }

        //Botón para programar un ejercicio grupal
        binding.groupButton.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager!!.unregisterListener(this)
        walking = false
        rein = 0F
    }

    override fun onResume() {
        super.onResume()

        user = auth.currentUser
        if (user != null) {
            Log.i("Adentro del onResume", "Entramos")
            //Hay un usuario
            walking = true
            val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        Log.i("Valor de Rein inicial", rein.toString())
        if (rein == 0F) {
            //Implica que acaba de entrar un nuevo usuario
            if (event != null) {
                event.values[0] = 0.0F
                //Cambio de Rein
                rein = 1F
                Log.i("Valor de Rein al cambio", rein.toString())
            }
        }

        var walk = 0
        var name = ""
        var target = 0
        var contador = 0
        var previousSteps = 0
        //var currentSteps = 0

        if(rein == 1F){
            //Ya ha ingresado antes este usuario
            //Traer datos
            database.collection("users").document(email).get().addOnSuccessListener {
                name = it.getString("name").toString()
                Log.i("Valor nombre", name)
                target = ((it.getLong("target")?.toInt() ?: Int) as Int)
                Log.i("Valor objetivo", target.toString())
                walk = ((it.getLong("walk")?.toInt() ?: Int) as Int)
                Log.i("Valor de pasos", walk.toString())
                if(walking){
                    val totalSteps = event?.values?.get(0)?.toInt() ?: 0
                    Log.i("Total de pasos", totalSteps.toString())
                    //Nueva cantidad de pasos
                    //Pasos que vienen de la base
                    if(contador == 0){
                        previousSteps = walk
                        contador = 1
                    }
                    val currentSteps = previousSteps + totalSteps - previousSteps
                    previousSteps = totalSteps

                    Log.i("Aquí varía el sensor", currentSteps.toString())
                    //Actualizar base
                    val newData = hashMapOf("walk" to currentSteps)
                    database.collection("users").document(email).set(newData, SetOptions.merge())
                        .addOnSuccessListener {
                            //Toast.makeText(applicationContext, "Pasos act", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            //Toast.makeText(applicationContext, "Error al actualizar la información", Toast.LENGTH_SHORT).show()
                        }
                    //Actualizar info en pantalla
                    binding.infoS.text = "¡Hola $name! Esta es la cantidad de pasos que llevas hoy..."
                    binding.textPaso.setText("${currentSteps} pasos completados")
                    var falta = target - currentSteps
                    Log.i("Faltan tantos", falta.toString())
                    Log.i("Target", target.toString())
                    binding.textPlus.setText("Te faltan $falta para completar tu meta, ¡vamos que sí se puede!")
                    binding.extraData.setText("Tu meta es equivalente a 35 viajes ida y vuelta a la luna, ¿Te gusta el espacio o qué?")
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //Nada
    }

    fun phisicalActivity(): Boolean{
        var yap = false
        //Aquí se pide y se gestiona el acceso al permiso
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            //No tiene el permiso, pedirlo
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), MainActivity.ACTIVITY_REQUEST_CODE)
        }else{
            //Ya tiene el permiso
            yap = true
        }
        return yap
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var sepudo = false
        when(requestCode){
            MainActivity.ACTIVITY_REQUEST_CODE -> {
                //If request is cancelled, the result arrays are empty
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //Permiso concedido
                    Toast.makeText(this, "Excelente servicio", Toast.LENGTH_SHORT).show()
                }else{
                    //Permiso denegado
                    //Funciones limitadas
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
