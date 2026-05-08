package com.example.childshield.screens.dashboard

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import com.example.childshield.data.ReportViewModel
import com.example.childshield.models.ChildModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.text.font.FontFamily
import com.example.childshield.data.AuthViewModel
import com.example.childshield.data.User
import com.example.childshield.navigation.Route
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController){
    val context = LocalContext.current
    val myauth = AuthViewModel(navController, context)
    val reportViewModel = ReportViewModel(navController, context)
    var searchQuery by remember { mutableStateOf("") }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CHILD SHIELD", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Red,
                ),
                actions = {
                    IconButton(onClick = { Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.Notifications, contentDescription = "notifications", tint = Color.Gray)
                    }
                    IconButton(onClick = { navController.navigate(Route.Settings.path) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "settings icon",
                        )
                    }
                    IconButton(onClick = { myauth.logout() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "logout icon",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:999")
                    context.startActivity(intent)
                },
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Emergency")
            }
        },

        bottomBar = {
            BottomAppBar(
                containerColor = Color.Gray,
                contentColor = Color.Blue
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home icon"
                            )
                        },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Route.Profile.path) },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Person icon"
                            )
                        },
                        label = { Text("Profile") }
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Route.Settings.path) },
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings icon"
                            )
                        },
                        label = { Text("Settings") }
                    )
                }
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Stats Overview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Total", "128", Color.Blue)
                StatCard("Resolved", "86", Color(0xFF4CAF50)) // Green
                StatCard("Active", "42", Color.Red)
            }

            // Quick Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search by name or location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray
                )
            )

            var user by remember { mutableStateOf<User?>(null) }
            val reports = remember { mutableStateListOf<ChildModel>() }
            val emptyReportState = remember { mutableStateOf(ChildModel()) }

            LaunchedEffect(Unit) {
                myauth.getUserDetails { fetchedUser ->
                    user = fetchedUser
                }
                reportViewModel.allReports(emptyReportState, reports)
            }

            // Welcoming Message after Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.size(60.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(2.dp, Color.Red)
                ) {
                    if (user?.imageUrl?.isNotEmpty() == true) {
                        AsyncImage(
                            model = user?.imageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile",
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            tint = Color.Gray
                        )
                    }
                }
                
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = "Hello, ${user?.name ?: "User"}!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Together, we can keep our children safe.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val uniformColor = Color(0xFF0D47A1) // Deep Professional Blue

                // Card 1: Add Report (Centered with reduced width)
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(120.dp)
                        .clickable {
                            navController.navigate(Route.AddReport.path)
                        },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = uniformColor,
                        contentColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "icon",
                            modifier = Modifier.size(28.dp)
                        )
                        Text("REPORT A CASE", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Urgent Help Needed", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                // Row for Secondary Actions (View All & Profile)
                Row(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card 2: Report List
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable {
                                navController.navigate(Route.ReportList.path)
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = uniformColor,
                            contentColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "icon",
                                modifier = Modifier.size(28.dp)
                            )
                            Text("VIEW CASES", fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text("Identify children", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    // Card 3: My Profile / My Reports
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable {
                                navController.navigate(Route.Profile.path)
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = uniformColor,
                            contentColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "icon",
                                modifier = Modifier.size(28.dp)
                            )
                            Text("MY PROFILE", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Track entries", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }

                // New Section Header with Dropdown
                var isReportsVisible by remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isReportsVisible = !isReportsVisible }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Reported Children",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Icon(
                        imageVector = if (isReportsVisible) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "toggle reports",
                        tint = Color.Red
                    )
                }

                // Recently Reported Children Cards (Animated)
                AnimatedVisibility(visible = isReportsVisible) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (reports.isEmpty()) {
                            Text("No reports found", modifier = Modifier.padding(vertical = 8.dp))
                        } else {
                            reports.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                        it.lastSeenLocation.contains(searchQuery, ignoreCase = true)
                            }.take(5).forEach { child ->
                                val isOwner = child.reporterId == currentUserId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clickable {
                                            if (isOwner) {
                                                navController.navigate(Route.UpdateReport.path + "/${child.id}")
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "You can only edit your own reports",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (child.status == "Found") Color(0xFFE8F5E9) else Color(0xFFFAFAFA),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Column 1: Child's Photo
                                        Box(modifier = Modifier.size(120.dp)) {
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxSize(),
                                                elevation = CardDefaults.cardElevation(4.dp)
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
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(25.dp),
                                                        tint = Color.Gray
                                                    )
                                                }
                                            }

                                            if (child.status != "Found") {
                                                Surface(
                                                    color = Color.Red,
                                                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                                                    modifier = Modifier.align(Alignment.TopStart)
                                                ) {
                                                    Text(
                                                        "🚨 URGENT",
                                                        color = Color.White,
                                                        modifier = Modifier.padding(
                                                            horizontal = 4.dp,
                                                            vertical = 2.dp
                                                        ),
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Column 2: Child's details
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = child.name.uppercase(),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (child.status == "Found") Color(0xFF2E7D32) else Color.Red
                                            )
                                            Text(
                                                text = "AGE: ${child.age} YEARS",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "📍 ${child.lastSeenLocation}",
                                                fontSize = 13.sp,
                                                color = Color.DarkGray
                                            )
                                            if (child.status == "Found") {
                                                Text(
                                                    "REUNITED ✅",
                                                    color = Color(0xFF2E7D32),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Safety Tips Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable {
                            Toast.makeText(context, "Tip: Keep recent photos of your children.", Toast.LENGTH_LONG).show()
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 Safety Tip", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Text("Always teach children their full name, your phone number, and address.", fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun DashboardPreview(){
    DashboardScreen(rememberNavController())
}
