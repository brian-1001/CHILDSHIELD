package com.example.childshield.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.childshield.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun signup(name: String, email: String, pass: String, confpass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = mAuth.currentUser
                val userId = currentUser?.uid
                val userData = User(name, email, userId ?: "")
                val reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userId!!)

                reference.setValue(userData).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(Route.Login.path)
                    } else {
                        Toast.makeText(context, task2.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email: String, pass: String) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()

        if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(trimmedEmail, trimmedPass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Route.Dashboard.path)
            } else {
                val exception = task.exception
                val message = exception?.message ?: ""
                
                val errorMessage = when {
                    message.contains("invalid-credential") || message.contains("incorrect") -> 
                        "Incorrect email or password. Please check your typing."
                    message.contains("malformed") -> 
                        "The email address is not formatted correctly."
                    message.contains("user-not-found") -> 
                        "Account not found. Please register first."
                    else -> "Authentication failed. Ensure you have an active account."
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getUserDetails(callback: (User?) -> Unit) {
        val currentUser = mAuth.currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            val reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userId)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    callback(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "User details error: ${error.message}", Toast.LENGTH_LONG).show()
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }

    fun getCurrentUserName(callback: (String) -> Unit) {
        val currentUser = mAuth.currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            val reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userId)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    callback(user?.name ?: "No name")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback("Error")
                }
            })
        } else {
            callback("Not Logged In")
        }
    }

    fun getUserData(onResult: (User?) -> Unit) {
        val userid = mAuth.currentUser?.uid

        if (userid != null) {
            val reference = FirebaseDatabase.getInstance()
                .getReference("Users/$userid")

            reference.get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                onResult(user)
            }.addOnFailureListener {
                onResult(null)
            }
        } else {
            onResult(null)
        }
    }

    fun logout() {
        mAuth.signOut()
        navController.navigate(Route.Login.path)
    }

    fun signout() {
        logout()
    }

    private var cloudinaryUrl = "https://api.cloudinary.com/v1_1/ds8pkkux9/image/upload"
    private val uploadPreset = "chromeproducts"

    fun uploadProfileImage(imageUri: Uri?) {
        if (imageUri == null) return

        val userid = mAuth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = uploadToCloudinary(context, imageUri)

                val reference = FirebaseDatabase.getInstance()
                    .getReference("Users/$userid")

                reference.child("imageUrl").setValue(imageUrl).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadToCloudinary(context: Context, uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes() ?: throw Exception("Image read failed")

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "profile.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url(cloudinaryUrl)
            .post(requestBody)
            .build()

        val response = OkHttpClient().newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Upload failed")

        val responseBody = response.body?.string()
        val secureUrl = Regex("\"secure_url\"\\s*:\\s*\"(.*?)\"")
            .find(responseBody ?: "")?.groupValues?.get(1)

        return secureUrl ?: throw Exception("Failed to get image URL")
    }
}

data class User(
    var name: String = "",
    var email: String = "",
    var userid: String = "",
    var imageUrl: String = ""
)
