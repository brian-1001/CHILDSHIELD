package com.example.childshield.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.childshield.data.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val authViewModel = AuthViewModel(navController, context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "App Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            HorizontalDivider()

            // Account Section
            Text(text = "Account", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                contentPadding = PaddingValues(15.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Logout from ChildShield")
            }

            HorizontalDivider()

            // Info Section
            Text(text = "About", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(text = "ChildShield v1.0", fontSize = 14.sp)
            Text(text = "Helping reunite families through technology.", fontSize = 14.sp)
        }
    }
}
