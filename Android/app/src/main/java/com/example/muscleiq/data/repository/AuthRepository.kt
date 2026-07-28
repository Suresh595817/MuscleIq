package com.example.muscleiq.data.repository

import com.example.muscleiq.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Login failed")
            
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Registration failed")
            
            val user = User(id = userId, name = name, email = email)
            firestore.collection("Users").document(userId).set(user).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    suspend fun getCurrentUserData(): Result<User> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }

    suspend fun updateName(newName: String): Result<Unit> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            firestore.collection("Users").document(userId).update("name", newName).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithCredential(credential: com.google.firebase.auth.AuthCredential): Result<User> {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Sign-in failed")
            val userId = firebaseUser.uid
            
            // Try to get existing user
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user: User
            if (userDoc.exists()) {
                user = userDoc.toObject(User::class.java) ?: throw Exception("User data parsing failed")
            } else {
                // Create new user entry for Phone/Google users
                val name = firebaseUser.displayName ?: firebaseUser.phoneNumber ?: "New Athlete"
                val email = firebaseUser.email ?: ""
                user = User(id = userId, name = name, email = email)
                firestore.collection("Users").document(userId).set(user).await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
