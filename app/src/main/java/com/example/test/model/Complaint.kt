package com.example.test.model

data class Complaint(
    val complaintId: String = "",
    val message: String = "",
    val className: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
