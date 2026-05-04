package com.example.childshield.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.childshield.data.AuthViewModel
import com.example.childshield.navigation.Route

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confpassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val myauth = AuthViewModel(navController, context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "REGISTER",
            fontSize = 40.sp,
            color = Color.Blue,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name", color = Color.Blue) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Blue) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Blue) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Blue) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.Blue) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Blue) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confpassword,
            onValueChange = { confpassword = it },
            label = { Text("Confirm Password", color = Color.Blue) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Blue) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                myauth.signup(name, email, password, confpassword)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("REGISTER", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = { navController.navigate(Route.Login.path) }) {
            Text("Already have an account? Login here", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterScreen(rememberNavController())
}
