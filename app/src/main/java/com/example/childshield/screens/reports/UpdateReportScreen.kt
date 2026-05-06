package com.example.childshield.screens.reports

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.childshield.data.ReportViewModel
import com.example.childshield.models.ChildModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateReportScreen(navController: NavHostController, id: String) {
    val context = LocalContext.current
    val reportViewModel = ReportViewModel(navController, context)

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Missing") }
    var currentImageUrl by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Automatically load data for the child
    LaunchedEffect(id) {
        reportViewModel.getReportById(id) { child ->
            child?.let {
                if (it.reporterId != currentUserId) {
                    Toast.makeText(context, "Unauthorized access", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                name = it.name
                age = it.age.toString()
                location = it.lastSeenLocation
                description = it.description
                status = it.status
                currentImageUrl = it.imageUrl
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Case Info", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back", tint = Color.White)
                    }
                },
                actions = {
                   IconButton(onClick = { reportViewModel.deleteReport(id) }) {
                       Icon(Icons.Default.Delete, contentDescription = "delete", tint = Color.White)
                   }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Editing details for $name", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Update Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Update Age") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Update Last Seen Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Update Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Current Status: ", fontWeight = FontWeight.Bold)
                RadioButton(selected = status == "Missing", onClick = { status = "Missing" })
                Text("Missing")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(selected = status == "Found", onClick = { status = "Found" })
                Text("Found")
            }

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(if (imageUri == null) "Change Photo (Optional)" else "New Photo Ready ✅")
            }

            Button(
                onClick = {
                    if (name.isBlank() || age.isBlank() || location.isBlank()) {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    } else {
                        reportViewModel.updateReport(id, name, age, location, description, status, imageUri, currentImageUrl)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = if(status == "Found") Color(0xFF4CAF50) else Color.Blue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if(status == "Found") "MARK AS REUNITED" else "SAVE CHANGES", fontWeight = FontWeight.Bold)
            }
        }
    }
}
