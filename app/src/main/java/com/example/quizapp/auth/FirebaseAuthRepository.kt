package com.example.quizapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthRepository (
    private val auth: FirebaseAuth
) {

    fun getCurrentUser () : FirebaseUser? {
        return auth.currentUser
    }
}