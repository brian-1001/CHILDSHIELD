package com.example.childshield.screens.reports

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.childshield.data.ReportViewModel
import com.example.childshield.ui.theme.AlertRed
import com.example.childshield.ui.theme.SecurityBlue
import com.google.android.gms.location.LocationServices
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportScreen(navController: NavHostController) {
    val context = LocalContext.current
    val reportViewModel = ReportViewModel(navController, context)

    // Form States
    var currentStep by remember { mutableIntStateOf(1) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Select Gender") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    var dateMissing by remember { mutableStateOf("") }
    var timeMissing by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    var hairColor by remember { mutableStateOf("") }
    var distinguishingFeatures by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dateMissing = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            timeMissing = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchLocation(context) { lat, long ->
                latitude = lat
                longitude = long
                Toast.makeText(context, "Location Tagged!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Missing Child", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (currentStep > 1) currentStep-- else navController.popBackStack() 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AlertRed)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Immediate Guidance Card
            EmergencyGuidanceCard()

            // 2. Progress Stepper
            StepIndicator(currentStep)

            Spacer(modifier = Modifier.height(8.dp))

            when (currentStep) {
                1 -> StepOneIdentity(name, {name = it}, age, {age = it}, gender, {gender = it}, imageUri) { imageLauncher.launch("image/*") }
                2 -> StepTwoDisappearance(
                    dateMissing, { datePickerDialog.show() },
                    timeMissing, { timePickerDialog.show() },
                    location, { location = it },
                    latitude != null
                ) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fetchLocation(context) { lat, long ->
                            latitude = lat
                            longitude = long
                            Toast.makeText(context, "Location Tagged!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    }
                }
                3 -> StepThreeDetails(
                    hairColor, {hairColor = it},
                    distinguishingFeatures, {distinguishingFeatures = it},
                    description, {description = it},
                    emergencyContact, {emergencyContact = it}
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("BACK")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            if (validateStep(currentStep, name, age, gender, dateMissing, timeMissing, location)) {
                                currentStep++
                            } else {
                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            reportViewModel.uploadReport(
                                imageUri, name, age, gender, hairColor, distinguishingFeatures,
                                dateMissing, timeMissing, location, latitude, longitude,
                                description, emergencyContact, "Missing"
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (currentStep == 3) "SUBMIT REPORT" else "NEXT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmergencyGuidanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.1f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = AlertRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("IMMEDIATE ACTIONS", fontWeight = FontWeight.Bold, color = AlertRed)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("• Call Emergency Services (999) immediately.", fontSize = 13.sp)
            Text("• Do not leave the last seen location.", fontSize = 13.sp)
            Text("• Check nearby water bodies, parks, or shops.", fontSize = 13.sp)
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepCircle(1, currentStep >= 1)
        HorizontalDivider(modifier = Modifier.width(40.dp).padding(horizontal = 4.dp), color = if(currentStep >= 2) AlertRed else Color.Gray)
        StepCircle(2, currentStep >= 2)
        HorizontalDivider(modifier = Modifier.width(40.dp).padding(horizontal = 4.dp), color = if(currentStep >= 3) AlertRed else Color.Gray)
        StepCircle(3, currentStep >= 3)
    }
}

@Composable
fun StepCircle(step: Int, isActive: Boolean) {
    Surface(
        shape = androidx.compose.foundation.shape.CircleShape,
        color = if (isActive) AlertRed else Color.Gray,
        modifier = Modifier.size(30.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = step.toString(), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOneIdentity(name: String, onNameChange: (String) -> Unit, age: String, onAgeChange: (String) -> Unit, gender: String, onGenderChange: (String) -> Unit, imageUri: Uri?, onPickImage: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Step 1: Child Identity", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = age, onValueChange = onAgeChange, label = { Text("Approximate Age") }, modifier = Modifier.fillMaxWidth())
        
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gender") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                listOf("Male", "Female", "Other").forEach { selection ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = selection,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ) 
                        },
                        onClick = { 
                            onGenderChange(selection)
                            expanded = false 
                        },
                        modifier = Modifier.background(Color.White)
                    )
                }
            }
        }

        Button(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (imageUri == null) "Upload Child's Photo" else "Photo Selected ✅")
        }
    }
}

@Composable
fun StepTwoDisappearance(date: String, onDateClick: () -> Unit, time: String, onTimeClick: () -> Unit, location: String, onLocationChange: (String) -> Unit, isLocationTagged: Boolean, onTagLocation: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Step 2: Disappearance Info", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date Missing") },
            trailingIcon = { IconButton(onClick = onDateClick) { Icon(Icons.Default.CalendarMonth, contentDescription = null) } },
            modifier = Modifier.fillMaxWidth().clickable { onDateClick() }
        )

        OutlinedTextField(
            value = time,
            onValueChange = {},
            readOnly = true,
            label = { Text("Time Last Seen") },
            trailingIcon = { IconButton(onClick = onTimeClick) { Icon(Icons.Default.Timer, contentDescription = null) } },
            modifier = Modifier.fillMaxWidth().clickable { onTimeClick() }
        )

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Last Seen Location (Address/Area)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onTagLocation,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (isLocationTagged) Color(0xFF4CAF50) else SecurityBlue)
        ) {
            Icon(if (isLocationTagged) Icons.Default.CheckCircle else Icons.Default.MyLocation, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isLocationTagged) "GPS Location Tagged" else "Tag Current GPS Location")
        }
    }
}

@Composable
fun StepThreeDetails(hair: String, onHairChange: (String) -> Unit, features: String, onFeaturesChange: (String) -> Unit, desc: String, onDescChange: (String) -> Unit, contact: String, onContactChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Step 3: Physical Details & Contact", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        OutlinedTextField(value = hair, onValueChange = onHairChange, label = { Text("Hair Color") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = features, onValueChange = onFeaturesChange, label = { Text("Distinguishing Features (Scars, birthmarks)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = onDescChange, label = { Text("Clothing Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        
        OutlinedTextField(
            value = contact,
            onValueChange = onContactChange,
            label = { Text("Emergency Contact Number") },
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("+") }
        )
    }
}

fun validateStep(step: Int, name: String, age: String, gender: String, date: String, time: String, location: String): Boolean {
    return when (step) {
        1 -> name.isNotBlank() && age.isNotBlank() && gender != "Select Gender"
        2 -> date.isNotBlank() && time.isNotBlank() && location.isNotBlank()
        else -> true
    }
}

@SuppressLint("MissingPermission")
fun fetchLocation(context: Context, onLocationFound: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationFound(it.latitude, it.longitude)
        } ?: run {
            Toast.makeText(context, "Unable to get location. Try turning on GPS.", Toast.LENGTH_LONG).show()
        }
    }
}
