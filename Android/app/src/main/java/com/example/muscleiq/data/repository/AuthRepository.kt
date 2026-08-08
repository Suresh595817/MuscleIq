package com.example.muscleiq.data.repository

import com.example.muscleiq.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Login failed")
            
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                val fallbackName = authResult.user?.displayName ?: "Athlete"
                val fallbackUser = User(id = userId, name = fallbackName, email = email)
                Result.success(fallbackUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Registration failed")
            
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            authResult.user?.updateProfile(profileUpdates)?.await()
            
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
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                // Fallback to FirebaseAuth profile if not in Firestore (e.g. registered on web)
                val fallbackName = auth.currentUser?.displayName ?: "Athlete"
                val fallbackUser = User(id = userId, name = fallbackName, email = auth.currentUser?.email ?: "")
                Result.success(fallbackUser)
            }
        } catch (e: Exception) {
            val fallbackName = auth.currentUser?.displayName ?: "Athlete"
            val fallbackUser = User(id = userId, name = fallbackName, email = auth.currentUser?.email ?: "")
            Result.success(fallbackUser)
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

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendOtp(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL("http://127.0.0.1:5000/api/auth/send-otp")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = "{\"email\": \"$email\"}"
            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success(Unit)
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                Result.failure(Exception(errorStream))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(email: String, code: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL("http://127.0.0.1:5000/api/auth/verify-otp")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = "{\"email\": \"$email\", \"code\": \"$code\"}"
            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success(Unit)
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                Result.failure(Exception(errorStream))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
