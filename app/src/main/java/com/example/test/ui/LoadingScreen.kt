package com.example.test.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(message: String = "Loading Sadhana School...") {
    // Animated values for different effects
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dotAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animated dots for loading text
    val dots = remember { mutableStateListOf("", ".", "..", "...") }
    var dotIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            dotIndex = (dotIndex + 1) % dots.size
            delay(400)
        }
    }

    // Gradient colors
    val gradientColors = listOf(
        Color(0xFF1976D2),  // Primary blue
        Color(0xFF2196F3),  // Light blue
        Color(0xFF64B5F6),  // Lighter blue
        Color(0xFF1976D2)   // Back to primary
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.3f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background decorative elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    // Draw multiple rotating elements in background
                    repeat(6) { i ->
                        rotate(degrees = rotationAngle + i * 60) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = 300f,
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                    }
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Outer ring with rotation
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                // Rotating ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .rotate(rotationAngle)
                )

                // Middle ring with shimmer effect
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = shimmerColors,
                                start = Offset(-100f, -100f),
                                end = Offset(100f, 100f)
                            )
                        )
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner circle with school icon
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "School Icon",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text container with fade effect
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "Sadhana School",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.scale(pulseScale)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message + dots[dotIndex],
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading progress indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(
                                    alpha = 0.3f + (0.7f * (dotAnimation * (index + 1) / 3))
                                )
                            )
                            .scale(0.8f + (0.4f * (dotAnimation * (index + 1) / 3)))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Additional information (subtle)
            Text(
                text = "Please wait while we prepare your experience",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        // Floating particles effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    repeat(20) { i ->
                        val particleAlpha = 0.05f + (i % 5) * 0.05f
                        val particleSize = 20f + (i % 3) * 10f
                        val offset = rotationAngle * (i + 1) / 20

                        drawCircle(
                            color = Color.White.copy(alpha = particleAlpha),
                            radius = particleSize,
                            center = Offset(
                                size.width * (0.2f + (i % 8) * 0.1f) + offset * 0.5f,
                                size.height * (0.1f + ((i + 3) % 7) * 0.1f) + offset * 0.3f
                            )
                        )
                    }
                }
        )
    }
}