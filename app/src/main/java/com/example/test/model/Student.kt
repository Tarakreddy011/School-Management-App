package com.example.test.model

data class Student(
    val studentId: String = "",
    val name: String = "",
    val dob: String = "",
    val className: String = "",
    val rollNo: String = "",
    val parentPhone: String = "",
    val feeStatus: String = "Pending", // Pending, Paid
    val rating: Float = 0f,
    val role: String = "student",
    val email: String = "",
    val password: String = ""
)
