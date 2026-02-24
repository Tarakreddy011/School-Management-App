package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.test.model.Teacher
import com.google.firebase.firestore.FirebaseFirestore

class TeacherViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _teachers = mutableStateOf<List<Teacher>>(emptyList())
    val teachers: State<List<Teacher>> = _teachers

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun fetchTeachers() {
        _loading.value = true
        db.collection("teachers").get()
            .addOnSuccessListener { result ->
                _teachers.value = result.toObjects(Teacher::class.java)
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    fun deleteTeacher(teacherId: String, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(teacherId).delete()
            .addOnSuccessListener {
                db.collection("teachers").document(teacherId).delete()
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateTeacherAssignment(teacherId: String, classes: List<String>, onComplete: (Boolean) -> Unit) {
        db.collection("teachers").document(teacherId)
            .update("classesAssigned", classes)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
