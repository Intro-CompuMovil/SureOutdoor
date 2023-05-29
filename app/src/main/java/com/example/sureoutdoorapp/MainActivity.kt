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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity(), SensorEventListener {

    //Base de datos
    lateinit var database: FirebaseFirestore
    //private val auth = FirebaseAuth.getInstance()

    //Sensor de pasos
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var currentUserUid: String? = null
    //private var stepCount: Float = 0.0F

    //var steps: Int = 0

    //var walk: Int = 0

    //var target: Int = 0

    private var email: String = ""

    //Para el permiso
    companion object{

        private const val ACTIVITY_REQUEST_CODE = 1
    }

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

        database = FirebaseFirestore.getInstance()

        //Inicializa los pasos
        UserDataManager.steps = 0

        /*
        database.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
         */

        //Permiso para el acceso a la actividad física
        phisicalActivity()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        Log.i("Usuario", currentUserUid.toString())

        //Creo variables para recibir lo de la base
        var name = ""
        var walk = 0
        var target = 0

        //Actualizar información con lo que viene de la base
        email = intent.getStringExtra("email").toString()
        Log.i("Correo del personaje", email)
        //Log.i("Correo es:", email)
        database.collection("users").get().addOnSuccessListener {snapshot->
            for(documento in snapshot){
                if(documento.id == email){
                    name = documento.getString("name").toString()
                    Log.i("Adentro", "Entramos")
                    walk = ((documento.getLong("walk")?.toInt() ?: Int) as Int)
                    //walk = documento.get("walk").toString().toInt()
                    target = ((documento.getLong("target")?.toInt() ?: Int) as Int)
                    //Falta foto
                    //Log.i("nombre", name)
                }
            }
            //Actualizar info con nombre
            binding.infoS.text = "¡Hola $name! Esta es la cantidad de pasos que llevas hoy..."

            //Actualizar cantidad de pasos completados
            binding.textPaso.setText("$walk pasos completados")
            //Actualizar cantidad de pasos faltantes
            var falta = target - walk
            binding.textPlus.setText("Te faltan $falta para completar tu meta, ¡vamos que sí se puede!")
            //Actualizar dato curioso
            binding.extraData.setText("Tu meta es equivalente a 35 viajes ida y vuelta a la luna, ¿Te gusta el espacio o qué?")

        }

        //Botón para cerrar sesión
        binding.returnButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            //Parar lectura del sensor
            sensorManager.unregisterListener(this)
            //Email vuelve a ser vacío
            email = ""
            UserDataManager.steps = 0
            //resetSteps()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Botón para ir a la configuración del perfil
        binding.settingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        //Botón para ir a ver los lugares
        binding.placesButton.setOnClickListener{
            val intent = Intent(this, PlacesActivity::class.java)
            intent.putExtra("email", email)
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

    override fun onStart(){
        super.onStart()
        UserDataManager.steps = 0
    }


    override fun onRestart(){
        super.onRestart()

        //stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

        //Creo variables para recibir lo de la base
        var name = ""
        var walk = 0
        var target = 0
        //Actualizar información con lo que viene de la base
        email = intent.getStringExtra("email").toString()
        //Log.i("Correo es:", email)
        database.collection("users").get().addOnSuccessListener {snapshot->
            for(documento in snapshot){
                if(documento.id == email){
                    name = documento.getString("name").toString()
                    Log.i("Adentro", "Entramos")
                    walk = ((documento.getLong("walk")?.toInt() ?: Int) as Int)
                    target = ((documento.getLong("target")?.toInt() ?: Int) as Int)
                    //Falta foto
                    Log.i("nombre", name)
                }
            }
            //Actualizar info con nombre
            binding.infoS.text = "¡Hola $name! Esta es la cantidad de pasos que llevas hoy..."

            //Actualizar cantidad de pasos completados
            binding.textPaso.setText("$walk pasos completados")
            //Actualizar cantidad de pasos faltantes
            var falta = target - walk
            binding.textPlus.setText("Te faltan $falta para completar tu meta, ¡vamos que sí se puede!")
            //Actualizar dato curioso
            binding.extraData.setText("Tu meta es equivalente a 35 viajes ida y vuelta a la luna, ¿Te gusta el espacio o qué?")

        }
    }

    /*
    override fun onStart(){
        super.onStart()
        //resetSteps()
    }
     */

    override fun onResume() {
        super.onResume()

        //stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

        //Creo variables para recibir lo de la base
        var name = ""
        var walk = 0
        var target = 0
        //Actualizar información con lo que viene de la base
        email = intent.getStringExtra("email").toString()
        //Log.i("Correo es:", email)
        database.collection("users").get().addOnSuccessListener {snapshot->
            for(documento in snapshot){
                if(documento.id == email){
                    name = documento.getString("name").toString()
                    Log.i("Adentro", "Entramos")
                    walk = ((documento.getLong("walk")?.toInt() ?: Int) as Int)
                    target = ((documento.getLong("target")?.toInt() ?: Int) as Int)
                    //Falta foto
                    Log.i("nombre", name)
                }
            }
            //Actualizar info con nombre
            binding.infoS.text = "¡Hola $name! Esta es la cantidad de pasos que llevas hoy..."

            //Actualizar cantidad de pasos completados
            binding.textPaso.setText("$walk pasos completados")
            //Actualizar cantidad de pasos faltantes
            var falta = target - walk
            binding.textPlus.setText("Te faltan $falta para completar tu meta, ¡vamos que sí se puede!")
            //Actualizar dato curioso
            binding.extraData.setText("Tu meta es equivalente a 35 viajes ida y vuelta a la luna, ¿Te gusta el espacio o qué?")

        }
    }


    /*
    override fun onPause() {
        //super.onPause()
        //sensorManager.unregisterListener(stepListener)
    }
     */

    override fun onSensorChanged(event: SensorEvent?) {

        //Número de pasos contados
        //steps = event!!.values[0]
        var s = (event?.values?.get(0) ?: return)
        var steps = s.toInt()

        var walk = 0
        var target = 0
        //var target = 0.0F

        //val change = steps.toString()

        //Obtener datos de la base
        database.collection("users").get().addOnSuccessListener {
            for(documento in it){
                if(documento.id == email){
                    walk = ((documento.getLong("walk")?.toInt() ?: Int) as Int)
                    target = ((documento.getLong("target")?.toInt() ?: Int) as Int)
                }
            }
            //nueva cantidad de pasos
            val currentSteps = UserDataManager.steps
            val newWalk = currentSteps + steps
            //Actualizar número de pasos
            UserDataManager.steps = newWalk

            //Actualizar base
            val newData = hashMapOf("walk" to newWalk)
            database.collection("users").document(email).set(newData, SetOptions.merge()).addOnSuccessListener{
                //Toast.makeText(applicationContext, "Pasos act", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener{
                    //Toast.makeText(applicationContext, "Error al actualizar la información", Toast.LENGTH_SHORT).show()
                }
            //Actualizar info en pantalla
            binding.textPaso.setText("$newWalk pasos completados")
            var falta = target - newWalk
            binding.textPlus.setText("Te faltan $falta para completar tu meta, ¡vamos que sí se puede!")
        }


        /*
        //Número de pasos contados
        //steps = event!!.values[0]
        Log.i("valor", event!!.values[0].toString())
        //var walk = 0.0F
        //val change = steps.toString()
        //Traer dato actual de la base
        database.collection("users").get().addOnSuccessListener{
            for(documento in it){
                if(documento.id == email){
                    //Obtengo el dato de pasos actuales
                    walk = ((documento.getLong("walk")?.toInt() ?: Int) as Int).toFloat()
                }
            }
        }
        //Contar nuevos pasos
        var newWalk = walk + steps
        //Actualizar base
        val newData = hashMapOf("walk" to newWalk)
        database.collection("users").document(email).set(newData, SetOptions.merge()).addOnSuccessListener{
            //Toast.makeText(applicationContext, "Pasos act", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener{
                //Toast.makeText(applicationContext, "Error al actualizar la información", Toast.LENGTH_SHORT).show()
            }
        //Actualizar info en pantalla
        binding.textPaso.setText("$steps pasos completados")
        //var falta = target - steps
        binding.textPlus.setText("Te faltan $falta para completar tu meta, ¡vamos que sí se puede!")
         */
    }

    /*
    fun resetSteps() {
        steps = 0.0F
    }
     */


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
