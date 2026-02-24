package com.example.test.ui

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuAction(
    val label: String,
    val icon: ImageVector,
    val action: () -> Unit
)
