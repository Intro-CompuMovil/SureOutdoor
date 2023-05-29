package com.example.sureoutdoorapp

import com.google.firebase.auth.FirebaseAuth

object SessionManager {
    private var loggedInUserEmail: String = null.toString()
    private var name: String = ""
    private var lastname: String = ""
    private var age: String = ""
    private var walk: String = ""
    private var target: String = ""
    //Falta foto

    fun isLoggedIn(): Boolean {
        return loggedInUserEmail != null
    }

    fun loginUser(email: String) {
        loggedInUserEmail = email
    }

    fun logoutUser() {
        loggedInUserEmail = null.toString()
    }

    fun getLoggedInUserEmail(): String? {
        return loggedInUserEmail
    }
}

// Iniciar sesión de usuario
fun signInWithEmailAndPassword(email: String, password: String) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso
                SessionManager.loginUser(email)
                // Obtener datos adicionales del usuario desde Firestore si es necesario
            } else {
                // Error en el inicio de sesión
            }
        }
}

// Verificar el estado de inicio de sesión
fun checkLoginStatus() {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    if (firebaseUser != null && SessionManager.isLoggedIn()) {
        // Usuario autenticado y con sesión iniciada
        val userEmail = firebaseUser.email
        // Obtener datos adicionales del usuario desde Firestore si es necesario
    } else {
        // Usuario no autenticado o sin sesión iniciada
    }
}

// Cerrar sesión de usuario
fun signOut() {
    FirebaseAuth.getInstance().signOut()
    SessionManager.logoutUser()
}
