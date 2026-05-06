package com.example.childshield.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import com.example.childshield.models.ChildModel
import com.example.childshield.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class ReportViewModel(
    var navController: NavHostController,
    var context: Context
) {

    private var cloudinaryUrl = "https://api.cloudinary.com/v1_1/ds8pkkux9/image/upload"
    private val uploadPreset = "chromeproducts"

    private val databaseReference by lazy {
        FirebaseDatabase.getInstance().getReference().child("Reports")
    }

    fun uploadReport(
        imageUri: Uri?,
        name: String,
        age: String,
        location: String,
        description: String,
        status: String
    ) {
        if (name.isBlank() || age.isBlank() || location.isBlank() || description.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = databaseReference.push()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = if (imageUri != null) {
                    uploadToCloudinary(context, imageUri)
                } else {
                    ""
                }

                val reportData = ChildModel(
                    id = ref.key ?: "",
                    name = name,
                    age = age.toIntOrNull() ?: 0,
                    lastSeenLocation = location,
                    description = description,
                    status = status,
                    imageUrl = imageUrl,
                    reporterId = userId
                )

                ref.setValue(reportData).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Report saved successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate(Route.ReportList.path)
                    } else {
                        Toast.makeText(context, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
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
                "report_image.jpg",
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

    fun allReports(
        report: MutableState<ChildModel>,
        reports: SnapshotStateList<ChildModel>
    ): SnapshotStateList<ChildModel> {

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reports.clear()
                for (snap in snapshot.children) {
                    val retrievedReport = snap.getValue(ChildModel::class.java)
                    if (retrievedReport != null) {
                        report.value = retrievedReport
                        reports.add(retrievedReport)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Database error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

        return reports
    }

    fun deleteReport(reportId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        getReportById(reportId) { report ->
            if (report?.reporterId == userId) {
                databaseReference.child(reportId).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Report deleted", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Error deleting report", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Unauthorized: Only the reporter can delete this", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getReportById(reportId: String, onResult: (ChildModel?) -> Unit) {
        databaseReference.child(reportId).get().addOnSuccessListener { snapshot ->
            val child = snapshot.getValue(ChildModel::class.java)
            onResult(child)
        }.addOnFailureListener {
            onResult(null)
        }
    }

    fun updateReport(
        reportId: String,
        name: String,
        age: String,
        location: String,
        description: String,
        status: String,
        imageUri: Uri?,
        currentImageUrl: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        getReportById(reportId) { report ->
            if (report?.reporterId != userId) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Unauthorized: Only the reporter can update this", Toast.LENGTH_SHORT).show()
                }
                return@getReportById
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val imageUrl = if (imageUri != null) {
                        uploadToCloudinary(context, imageUri)
                    } else {
                        currentImageUrl
                    }

                    val updateData = mapOf(
                        "name" to name,
                        "age" to (age.toIntOrNull() ?: 0),
                        "lastSeenLocation" to location,
                        "description" to description,
                        "status" to status,
                        "imageUrl" to imageUrl
                    )

                    databaseReference.child(reportId).updateChildren(updateData).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Report updated", Toast.LENGTH_SHORT).show()
                            navController.navigate(Route.ReportList.path)
                        } else {
                            Toast.makeText(context, "Error updating report", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
