package com.example.childshield.screens.reports

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.childshield.data.ReportViewModel
import com.example.childshield.models.ChildModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosterScreen(navController: NavHostController, id: String) {
    val context = LocalContext.current
    val reportViewModel = ReportViewModel(navController, context)
    var child by remember { mutableStateOf<ChildModel?>(null) }

    LaunchedEffect(id) {
        reportViewModel.getReportById(id) { child = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Missing Poster Preview") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "MISSING CHILD ALERT")
                            putExtra(Intent.EXTRA_TEXT, """
                                🚨 MISSING CHILD ALERT 🚨
                                Name: ${child?.name}
                                Age: ${child?.age}
                                Last Seen: ${child?.lastSeenLocation}
                                On: ${child?.dateMissing} at ${child?.timeMissing}
                                Features: ${child?.hairColor} hair, ${child?.distinguishingFeatures}
                                CONTACT: ${child?.emergencyContact}
                                Please help us find ${child?.name}!
                            """.trimIndent())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Poster"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "share")
                    }
                }
            )
        }
    ) { paddingValues ->
        child?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // THE POSTER
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(4.dp, Color.Red, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "MISSING",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier.size(250.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = data.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            data.name.uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PosterDetailRow("AGE", data.age.toString())
                        PosterDetailRow("GENDER", data.gender)
                        PosterDetailRow("LAST SEEN", data.lastSeenLocation)
                        PosterDetailRow("DATE", data.dateMissing)
                        PosterDetailRow("TIME", data.timeMissing)
                        PosterDetailRow("HAIR", data.hairColor)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Red)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "CONTACT: ${data.emergencyContact}",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "You can take a screenshot of this poster to share it on social media or print it.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PosterDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text("$label: ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(value, fontSize = 16.sp)
    }
}
