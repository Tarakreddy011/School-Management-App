package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.test.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _students = mutableStateOf<List<Student>>(emptyList())
    val students: State<List<Student>> = _students

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun fetchStudents(className: String, searchQuery: String = "") {
        _loading.value = true
        db.collection("students").get()
            .addOnSuccessListener { result ->
                val allStudents = result.toObjects(Student::class.java)
                _students.value = allStudents.filter { student ->
                    (className == "All" || student.className == className) &&
                    (searchQuery.isEmpty() || student.name.contains(searchQuery, ignoreCase = true))
                }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    fun fetchAllStudents() {
        fetchStudents("All")
    }

    fun addStudent(student: Student, onComplete: (Boolean, Student?) -> Unit) {
        _loading.value = true
        
        // 1. Generate Credentials
        val generatedEmail = "${student.name.lowercase().replace(" ", "")}${student.className.lowercase()}@gmail.com"
        val dobParts = student.dob.split("/")
        val generatedPassword = if (dobParts.size == 3) "${dobParts[0]}${dobParts[2]}" else "123456"

        // 2. Create Auth Account
        auth.createUserWithEmailAndPassword(generatedEmail, generatedPassword)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                
                val finalStudent = student.copy(
                    studentId = uid,
                    email = generatedEmail,
                    password = generatedPassword
                )

                // 3. Save to Firestore using the UID
                db.collection("students").document(uid).set(finalStudent)
                    .addOnSuccessListener {
                        _loading.value = false
                        onComplete(true, finalStudent)
                    }
                    .addOnFailureListener {
                        _loading.value = false
                        onComplete(false, null)
                    }
            }
            .addOnFailureListener {
                _loading.value = false
                onComplete(false, null)
            }
    }

    fun deleteStudent(studentId: String, onComplete: (Boolean) -> Unit) {
        db.collection("students").document(studentId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateStudentRating(studentId: String, rating: Float) {
        db.collection("students").document(studentId).update("rating", rating)
    }

    fun updateFeeStatus(studentId: String, status: String) {
        db.collection("students").document(studentId).update("feeStatus", status)
    }
}
