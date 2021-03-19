package com.blps.aagj.cookbook.domain

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthenticationManager {
    fun isUserLoggedIn(): Boolean
    fun getUid(): String?
    fun signOut(): Boolean
}

class AuthenticationManagerImpl : AuthenticationManager {
    override fun isUserLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        return user != null
    }

    override fun getUid(): String? {
        return Firebase.auth.currentUser?.uid
    }

    @SuppressLint("LogNotTimber")
    override fun signOut(): Boolean {
        Firebase.auth.signOut()
        Log.d("user signed out ", "Firebase.auth.currentUser" + Firebase.auth.currentUser)
        return isUserLoggedIn()
    }
}
