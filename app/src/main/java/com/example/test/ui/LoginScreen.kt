package com.example.test.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loading by viewModel.loading
    val error by viewModel.error

    // Background Blue Color (Professional School Blue)
    val schoolBlue = Color(0xFF1976D2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(schoolBlue)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section with Icon and Greeting
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "School Icon",
                    modifier = Modifier.size(70.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hello!",
                    fontSize = 38.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome to Sadhana School",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // White Rounded Container for Login Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = schoolBlue
                    )

                    // Scrollable content area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = schoolBlue) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = schoolBlue) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = schoolBlue)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Text(
                            text = "Forgot Password?",
                            modifier = Modifier.align(Alignment.End),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        if (error != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = error!!,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Login Button (fixed at bottom)
                    Button(
                        onClick = { viewModel.login(email, password, onLoginSuccess) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = schoolBlue),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}