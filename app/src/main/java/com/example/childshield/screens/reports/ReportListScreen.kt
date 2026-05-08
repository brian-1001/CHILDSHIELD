package com.example.childshield.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.childshield.data.ReportViewModel
import com.example.childshield.models.ChildModel
import com.example.childshield.navigation.Route
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val reportViewModel = ReportViewModel(navController, context)
    val reports = remember { mutableStateListOf<ChildModel>() }
    val emptyReportState = remember { mutableStateOf(ChildModel()) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        reportViewModel.allReports(emptyReportState, reports)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reported Cases", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back", tint = Color.White)
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
                .background(Color(0xFF3F51B5))
        ) {
            val myReports = reports.filter { it.reporterId == currentUserId }

            if (myReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You haven't reported any cases yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myReports) { child ->
                        ReportCard(child, navController, reportViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(child: ChildModel, navController: NavHostController, reportViewModel: ReportViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Route.UpdateReport.path + "/${child.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Child Image
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(100.dp)
            ) {
                if (child.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = child.imageUrl,
                        contentDescription = "Child Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "No Photo",
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.name.uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (child.status == "Found") Color(0xFF2E7D32) else Color.Red
                )
                Text(text = "Age: ${child.age} yrs", fontSize = 14.sp)
                Text(text = "Location: ${child.lastSeenLocation}", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Status: ${child.status}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (child.status == "Found") Color(0xFF2E7D32) else Color.Red
                )
            }

            // Delete Icon (Only if reporter or for demo)
            IconButton(onClick = { reportViewModel.deleteReport(child.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "delete", tint = Color.Gray)
            }
        }
    }
}
