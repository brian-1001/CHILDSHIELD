package com.example.childshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.childshield.navigation.AppNavHost
import com.example.childshield.ui.theme.ChildShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge display for a modern Android look
        enableEdgeToEdge()
        
        setContent {
            ChildShieldTheme {
                // Surface provides the default background color from your theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AppNavHost is the entry point for all navigation in ChildShield
                    AppNavHost()
                }
            }
        }
    }
}
