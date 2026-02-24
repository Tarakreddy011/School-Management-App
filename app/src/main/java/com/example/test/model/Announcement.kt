package com.example.test.model

data class Announcement(
    val announceId: String = "",
    val title: String = "",
    val message: String = "",
    val target: String = "all", // all, trio, class_5, etc.
    val createdBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
