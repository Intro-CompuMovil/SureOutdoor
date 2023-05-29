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
import com.example.sureoutdoorapp.databinding.NewProfileBinding
import com.google.android.material.shadow.ShadowRenderer
import com.google.firebase.auth.FirebaseAuth
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
//import com.example.sureoutdoorapp.data

class NewProfileActivity : AppCompatActivity() {
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

        //Botón para cerrar sesión

        binding.returnButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Botón para guardar los cambios

        binding.editProfileButton.setOnClickListener{
            SharedData.name = binding.nameEj.text.toString()
            SharedData.lastName = binding.lastNameEj.text.toString()
            SharedData.email = binding.emailEj.text.toString()
            SharedData.age = binding.ageEj.text.toString()

            Toast.makeText(applicationContext, "Cambios guardados", Toast.LENGTH_LONG).show()
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
        SharedData.image = image
        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
    }
}