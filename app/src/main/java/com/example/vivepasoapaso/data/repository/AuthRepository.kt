package com.example.vivepasoapaso.data.repository

import com.example.vivepasoapaso.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            // Crear usuario en Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Error creando usuario")

            // Actualizar perfil con nombre
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Crear documento en Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name
            )

            db.collection("users").document(firebaseUser.uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Usuario no encontrado")

            // Obtener datos del usuario desde Firestore
            val userDocument = db.collection("users").document(firebaseUser.uid).get().await()
            val user = userDocument.toObject(User::class.java) ?: throw Exception("Datos de usuario no encontrados")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Google sign in falló")

            // Verificar si el usuario ya existe en Firestore
            val userDocument = db.collection("users").document(firebaseUser.uid).get().await()

            val user = if (userDocument.exists()) {
                userDocument.toObject(User::class.java) ?: throw Exception("Error en datos de usuario")
            } else {
                // Crear nuevo usuario en Firestore
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: "Usuario"
                )
                db.collection("users").document(firebaseUser.uid).set(newUser).await()
                newUser
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithFacebook(token: String): Result<User> {
        return try {
            val credential = FacebookAuthProvider.getCredential(token)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Facebook sign in falló")

            // Verificar si el usuario ya existe en Firestore
            val userDocument = db.collection("users").document(firebaseUser.uid).get().await()

            val user = if (userDocument.exists()) {
                userDocument.toObject(User::class.java) ?: throw Exception("Error en datos de usuario")
            } else {
                // Crear nuevo usuario en Firestore
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: "Usuario"
                )
                db.collection("users").document(firebaseUser.uid).set(newUser).await()
                newUser
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithApple(idToken: String, nonce: String): Result<User> {
        return try {
            // Para Apple Sign-In, necesitaríamos implementar el flujo completo
            // Esto es un placeholder para la implementación futura
            throw Exception("Apple Sign-In no está completamente implementado. " +
                    "Debe manejarse en la UI con una Activity.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        val currentUser = auth.currentUser ?: return null
        return try {
            val userDocument = db.collection("users").document(currentUser.uid).get().await()
            userDocument.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, language: String): Result<User> {
        return try {
            val updates = mapOf(
                "name" to name,
                "language" to language,
                "updated_at" to Timestamp.now()
            )

            db.collection("users").document(userId).update(updates).await()

            // Obtener usuario actualizado
            val userDocument = db.collection("users").document(userId).get().await()
            val updatedUser = userDocument.toObject(User::class.java) ?: throw Exception("Usuario no encontrado")

            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}