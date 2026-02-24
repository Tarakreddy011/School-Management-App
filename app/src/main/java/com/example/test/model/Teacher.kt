package com.example.test.model

data class Teacher(
    val teacherId: String = "",
    val name: String = "",
    val role: String = "", // subject, trio, incharge, hm
    val subject: String = "",
    val classesAssigned: List<String> = emptyList(),
    val email: String = "",
    val password: String = ""
)
