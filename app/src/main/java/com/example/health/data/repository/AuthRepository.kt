package com.example.health.data.repository

import com.example.health.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) {
    val currentUser get() = auth.currentUser

    suspend fun register(name: String, email: String, password: String, mobile: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Registration failed")
            
            val user = User(
                uid = uid,
                name = name,
                email = email,
                mobile = mobile
            )
            
            db.getReference("users").child(uid).setValue(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<com.google.firebase.auth.FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserProfile(uid: String): User? {
        return try {
            val snapshot = db.getReference("users").child(uid).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
