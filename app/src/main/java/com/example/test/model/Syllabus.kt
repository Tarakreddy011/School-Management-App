package com.example.test.model

data class Syllabus(
    val syllabusId: String = "",
    val className: String = "",
    val subject: String = "",
    val topic: String = "",
    val updatedBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
