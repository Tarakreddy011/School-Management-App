package com.example.test.model

data class Leave(
    val leaveId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val className: String = "",
    val type: String = "Sick", // Sick, Holiday
    val reason: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val dateApplied: Long = System.currentTimeMillis()
)
