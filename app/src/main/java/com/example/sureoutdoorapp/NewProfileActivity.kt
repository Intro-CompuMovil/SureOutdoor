package com.example.sureoutdoorapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sureoutdoorapp.databinding.NewProfileBinding
import com.google.android.material.shadow.ShadowRenderer
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.*

//import com.example.sureoutdoorapp.data

class NewProfileActivity : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    //@SuppressLint("WrongViewCast")
    companion object {
        private const val GALLERY_REQUEST_CODE = 1
        private const val LOAD_REQUEST_CODE = 2
        private const val CAMERA_REQUEST_CODE = 3
        private const val CAPTURE_REQUEST_CODE = 4
    }

    private lateinit var binding : NewProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewProfileBinding.inflate(layoutInflater)
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
        var walked = 0

        //Poner la información de la base aquí
        database.collection("users").document(email).get().addOnSuccessListener {
            name = it.getString("name").toString()
            lastName = it.getString("lastname").toString()
            age = ((it.getLong("age")?.toInt() ?: Int) as Int)
            target = ((it.getLong("target")?.toInt() ?: Int) as Int)
            walked = ((it.getLong("walk")?.toInt() ?: Int) as Int)

            //Mostrar la información
            binding.nameEj.setText(name)
            binding.lastNameEj.setText(lastName)
            binding.ageEj.setText(age.toString())
            binding.emailEj.setText(email)
            binding.newGoal.setText(target.toString())
        }
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            // Cargar la imagen con Glide utilizando la URL
            Glide.with(this)
                .load(imageUrl)
                .into(binding.personImage)
        }


        //Botón para cerrar sesión

        binding.returnButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Botón para guardar los cambios

        binding.editProfileButton.setOnClickListener{
            if(binding.nameEj.text.isNotEmpty() && binding.lastNameEj.text.isNotEmpty() &&
                binding.ageEj.text.isNotEmpty() && binding.newGoal.text.isNotEmpty() && binding.emailEj.text.isNotEmpty()){
                //Verificar correo válido
                var verif = isEmailValid(binding.emailEj.text.toString())
                if(verif){
                    //Se actualiza la información en la base
                    val data = hashMapOf("name" to binding.nameEj.text.toString(),
                        "lastname" to binding.lastNameEj.text.toString(),
                        "age" to binding.ageEj.text.toString().toInt(),
                        "walk" to walked,
                        "target" to binding.newGoal.text.toString().toInt(),
                        "email" to binding.emailEj.text.toString())
                    database.collection("users").document(binding.emailEj.text.toString()).set(data).addOnSuccessListener {
                        //Datos actualizados correctamente
                        Toast.makeText(applicationContext, "Cambios guardados", Toast.LENGTH_LONG).show()
                    }
                    //Llamar a Configuración
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.putExtra("email", binding.emailEj.text.toString())
                    startActivity(intent)
                }
            }else{
                Toast.makeText(applicationContext, "Todos los campos deben estar completos", Toast.LENGTH_LONG).show()
            }
        }

        //Botón para seleccionar la imagen del perfil

        binding.personImage.setOnClickListener{
            //Preguntar si desea tomar una foto con la cámara o sacar una de la galería
            val options = arrayOf<CharSequence>("Tomar foto con cámara", "Elegir desde la galería")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")
            builder.setItems(options) { dialog, item ->
                when {
                        options[item] == "Tomar foto con cámara" -> {
                            //Hacer todo el proceso de la cámara
                            //Pedir permiso de la cámara
                            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                                //No tiene el permiso, pedirlo
                                //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                            }else{
                                //Ya tiene el permiso
                                takePicture()
                            }
                        }
                        options[item] == "Elegir desde la galería" -> {
                            //Hacer todo el proceso de la galería
                            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                                //No tiene el permiso, pedirlo
                                //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST_CODE)
                            }else{
                                //Ya tiene el permiso
                                openGallery()
                            }
                        }
                    }
                }
                builder.show()

        }
    }
    private fun isEmailValid (email: String ): Boolean{
        if(!email.contains("@")|| !email.contains(".")){
            binding.emailEj.error = "Debe ser válido"
            return false
        }
        return true
    }

    fun takePicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takePictureIntent, CAPTURE_REQUEST_CODE)
        }catch (e: ActivityNotFoundException){
            e.message?.let { Log.e("PERMISSION_APP", it)}
        }
    }
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, LOAD_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            GALLERY_REQUEST_CODE -> {
                //If request is cancelled, the result arrays are empty
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //Permiso concedido
                    openGallery()
                }else{
                    //Permiso denegado
                    //Funciones limitadas
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
            CAMERA_REQUEST_CODE -> {
                //If request is cancelled, the result arrays are empty
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //Permiso concedido
                    takePicture()
                }else{
                    //Permiso denegado
                    //Funciones limitadas
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            LOAD_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK){
                    try{
                        val imageUri: Uri? = data?.data
                        val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        binding.personImage.setImageBitmap(selectedImage)
                        saveImage(selectedImage)
                    }catch (e: FileNotFoundException){
                        e.printStackTrace()
                    }
                }
            }
            CAPTURE_REQUEST_CODE -> {
                if(resultCode == RESULT_OK){
                    val extras: Bundle? = data?.extras
                    val imageBitmap = extras?.get("data") as? Bitmap
                    binding.personImage.setImageBitmap(imageBitmap)
                    //Guardar imagen para pasarla a otra actividad
                    saveImage(imageBitmap!!)

                }
            }
        }
    }

    private fun saveImage(image: Bitmap){
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            image,
            "title",
            "description"
        )
        //SharedData.image = image
        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("users/${binding.emailEj.text}/images")
        val fileName = "${binding.emailEj.text}.jpg"
        val imageRef = imagesRef.child(fileName)

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val imageURL = downloadUri.toString()
                // Guardar la URL de la imagen en la base de datos del usuario, si corresponde
                saveImageURLToUser(binding.emailEj.text.toString(), imageURL)
                //SharedData.image = image
                Toast.makeText(this, "Imagen guardada en Firebase Storage", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener la URL de descarga: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveImageURLToUser(userId: String, imageURL: String) {
        //val db = FirebaseFirestore.getInstance()
        val userRef = database.collection("images").document(userId)
        val data = hashMapOf(
            "imageURL" to imageURL
        )
        userRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                // La URL de la imagen se ha guardado exitosamente en Firestore
                Toast.makeText(this, "URL de imagen guardada en Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Ocurrió un error al guardar la URL de la imagen en Firestore
                Toast.makeText(this, "Error al guardar la URL de la imagen en Firestore: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}