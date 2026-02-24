package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.model.Leave
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaveViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _leaves = mutableStateOf<List<Leave>>(emptyList())
    val leaves: State<List<Leave>> = _leaves

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchLeavesForStudent(studentId: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = db.collection("leaves")
                    .whereEqualTo("studentId", studentId)
                    .orderBy("dateApplied", Query.Direction.DESCENDING)
                    .get().await()
                _leaves.value = result.toObjects(Leave::class.java)
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to load leaves: ${e.message}"
            }
        }
    }

    fun fetchLeavesForIncharge(className: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = db.collection("leaves")
                    .whereEqualTo("className", className)
                    .orderBy("dateApplied", Query.Direction.DESCENDING)
                    .get().await()
                _leaves.value = result.toObjects(Leave::class.java)
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to load leaves: ${e.message}"
            }
        }
    }

    fun fetchAllLeaves() {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = db.collection("leaves")
                    .orderBy("dateApplied", Query.Direction.DESCENDING)
                    .get().await()
                _leaves.value = result.toObjects(Leave::class.java)
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to load leaves: ${e.message}"
            }
        }
    }

    fun applyLeave(leave: Leave, onComplete: (Boolean) -> Unit) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val docRef = db.collection("leaves").document()
                val leaveWithId = leave.copy(
                    leaveId = docRef.id,
                    dateApplied = System.currentTimeMillis(),
                    status = "Pending"
                )
                docRef.set(leaveWithId).await()
                _loading.value = false
                onComplete(true)
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to apply for leave: ${e.message}"
                onComplete(false)
            }
        }
    }

    fun updateLeaveStatus(leaveId: String, status: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("leaves").document(leaveId)
                    .update("status", status).await()
                onComplete(true)
            } catch (e: Exception) {
                _error.value = "Failed to update leave status: ${e.message}"
                onComplete(false)
            }
        }
    }
}