package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.test.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrincipalViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _summary = mutableStateOf(PrincipalSummary())
    val summary: State<PrincipalSummary> = _summary

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun fetchSummary() {
        _loading.value = true
        
        db.collection("students").get().addOnSuccessListener { students ->
            val totalStudents = students.size()
            val pendingFees = students.count { it.getString("feeStatus") != "Paid" }
            
            db.collection("teachers").get().addOnSuccessListener { teachers ->
                val totalTeachers = teachers.size()
                
                db.collection("leaves").whereEqualTo("status", "Pending").get().addOnSuccessListener { leaves ->
                    val pendingLeaves = leaves.size()
                    
                    db.collection("complaints").get().addOnSuccessListener { complaints ->
                        val totalComplaints = complaints.size()
                        
                        _summary.value = PrincipalSummary(
                            totalStudents = totalStudents,
                            totalTeachers = totalTeachers,
                            pendingFees = pendingFees,
                            pendingLeaves = pendingLeaves,
                            totalComplaints = totalComplaints
                        )
                        _loading.value = false
                    }
                }
            }
        }
    }

    fun addStaff(user: User, onComplete: (Boolean, User?) -> Unit) {
        _loading.value = true
        
        // Use user-provided email and password directly as requested
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                
                val finalUser = user.copy(
                    userId = uid
                )

                // Save to 'users' collection in Firestore
                db.collection("users").document(uid).set(finalUser)
                    .addOnSuccessListener {
                        // Forward compatibility
                        if (user.role == "teacher" || user.role == "hm") {
                            db.collection("teachers").document(uid).set(hashMapOf(
                                "teacherId" to uid,
                                "name" to user.name,
                                "role" to user.role,
                                "email" to user.email,
                                "password" to user.password
                            ))
                        }
                        
                        _loading.value = false
                        onComplete(true, finalUser)
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
}

data class PrincipalSummary(
    val totalStudents: Int = 0,
    val totalTeachers: Int = 0,
    val pendingFees: Int = 0,
    val pendingLeaves: Int = 0,
    val totalComplaints: Int = 0
)
