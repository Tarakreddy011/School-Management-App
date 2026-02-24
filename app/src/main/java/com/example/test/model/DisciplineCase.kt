package com.example.test.model

data class DisciplineCase(
    val caseId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val level: String = "Minor", // Minor, Major
    val description: String = "",
    val createdBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
