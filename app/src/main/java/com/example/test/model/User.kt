package com.example.test.model

data class User(
    val userId: String = "",
    val name: String = "",
    val role: String = "", // principal, hm, teacher, student
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val className: String? = null,
    val subject: String = "",
    val classesAssigned: List<String> = emptyList(),
    val feeStatus: String? = null,
    val rating: Float = 0f
)
