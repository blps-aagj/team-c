package com.blps.aagj.cookbook.domain

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthenticationManager {
    fun isUserLoggedIn(): Boolean
    fun getUid(): String?
}

class AuthenticationManagerImpl : AuthenticationManager {
    override fun isUserLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        return user != null
    }

    override fun getUid(): String? {
        return Firebase.auth.currentUser?.uid
    }
}
