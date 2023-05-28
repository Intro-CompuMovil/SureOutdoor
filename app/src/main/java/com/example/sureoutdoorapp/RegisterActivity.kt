package com.example.sureoutdoorapp

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sureoutdoorapp.databinding.LoginBinding
import com.example.sureoutdoorapp.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class RegisterActivity : AppCompatActivity() {

    //Pa la foto
    companion object {
        private const val GALLERY_REQUEST_CODE = 1
        private const val LOAD_REQUEST_CODE = 2
        private const val CAMERA_REQUEST_CODE = 3
        private const val CAPTURE_REQUEST_CODE = 4
    }

    private lateinit var binding: RegisterBinding

    private val database = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.register)
        binding = RegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        //Botón para la imagen
        binding.personImage.setOnClickListener{
            photoDumb()
        }

        //Botón para guardar el registro e ir a la pantalla principal
        binding.startButton.setOnClickListener{

            //Crea el usuario
            if(binding.personName.text.isNotEmpty() && binding.lastPersonName.text.isNotEmpty() &&
                binding.personEmail.text.isNotEmpty() && binding.passwordPerson.text.isNotEmpty() && binding.personAge.text.isNotEmpty() &&
                    binding.personTarget.text.isNotEmpty()){
                var verif = isEmailValid(binding.personEmail.text.toString())
                var passif = isPasswordValid(binding.passwordPerson.text.toString())
                if(verif && passif){
                    auth.createUserWithEmailAndPassword(binding.personEmail.text.toString(), binding.passwordPerson.text.toString())
                        .addOnCompleteListener{
                            if(it.isSuccessful){
                                val user = auth.currentUser
                                if(user != null){
                                    //val userId = user.uid
                                    //Guardar los otros datos
                                    val data = hashMapOf("name" to binding.personName.text.toString(),
                                        "lastname" to binding.lastPersonName.text.toString(),
                                        "age" to binding.personAge.text.toString().toInt(),
                                        "walk" to "0".toInt(),
                                        "target" to binding.personTarget.text.toString().toInt(),
                                    "email" to binding.personEmail.text.toString())

                                    database.collection("users").document(user.email.toString()).set(data).addOnSuccessListener{
                                        //Datos guardados correctamente
                                        Toast.makeText(applicationContext, "Se guardaron los datos correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                //Se pudo crear, carga la pantalla principal
                                val intent = Intent(this, MainActivity::class.java)
                                if (user != null) {
                                    intent.putExtra("email", user.email.toString())
                                }
                                startActivity(intent)
                            }else{
                                Toast.makeText(applicationContext, "Error al crear el usuario, intente nuevamente", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }

    }

    private fun isEmailValid (email: String ): Boolean{
        if(!email.contains("@")|| !email.contains(".")){
            binding.personEmail.error = "Debe ser válido"
            return false
        }
        return true
    }
    private fun isPasswordValid (password: String): Boolean{
        var tam = password.length
        if(tam != 8){
            binding.passwordPerson.error = "Debe tener mínimo 8 caracteres"
            return false
        }
        return true
    }
    fun photoDumb(){
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
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), RegisterActivity.CAMERA_REQUEST_CODE)
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
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RegisterActivity.GALLERY_REQUEST_CODE)
                        }else{
                            //Ya tiene el permiso
                            openGallery()
                        }
                    }
                }
            }
            builder.show()
    }
    fun takePicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takePictureIntent, RegisterActivity.CAPTURE_REQUEST_CODE)
        }catch (e: ActivityNotFoundException){
            e.message?.let { Log.e("PERMISSION_APP", it)}
        }
    }
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RegisterActivity.LOAD_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            RegisterActivity.GALLERY_REQUEST_CODE -> {
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
            RegisterActivity.CAMERA_REQUEST_CODE -> {
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
            RegisterActivity.LOAD_REQUEST_CODE -> {
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
            RegisterActivity.CAPTURE_REQUEST_CODE -> {
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
        SharedData.image = image
        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("users/${binding.personEmail.text}/images")
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val imageRef = imagesRef.child(fileName)

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val imageURL = downloadUri.toString()
                // Guardar la URL de la imagen en la base de datos del usuario, si corresponde
                saveImageURLToUser(binding.personEmail.text.toString(), imageURL)
                SharedData.image = image
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