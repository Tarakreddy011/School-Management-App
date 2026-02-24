package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.model.Complaint
import com.example.test.model.DisciplineCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DisciplineViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _disciplineCases = mutableStateOf<List<DisciplineCase>>(emptyList())
    val disciplineCases: State<List<DisciplineCase>> = _disciplineCases

    private val _complaints = mutableStateOf<List<Complaint>>(emptyList())
    val complaints: State<List<Complaint>> = _complaints

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchDisciplineCases() {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = db.collection("discipline")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().await()
                _disciplineCases.value = result.toObjects(DisciplineCase::class.java)
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to load discipline cases: ${e.message}"
            }
        }
    }

    fun logDisciplineCase(case: DisciplineCase, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val docRef = db.collection("discipline").document()
                val caseWithId = case.copy(
                    caseId = docRef.id,
                    timestamp = System.currentTimeMillis()
                )
                docRef.set(caseWithId).await()
                onComplete(true)
            } catch (e: Exception) {
                _error.value = "Failed to log discipline case: ${e.message}"
                onComplete(false)
            }
        }
    }

    fun fetchComplaints() {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = db.collection("complaints")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().await()
                _complaints.value = result.toObjects(Complaint::class.java)
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to load complaints: ${e.message}"
            }
        }
    }

    fun submitComplaint(complaint: Complaint, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val docRef = db.collection("complaints").document()
                val complaintWithId = complaint.copy(
                    complaintId = docRef.id,
                    timestamp = System.currentTimeMillis()
                )
                docRef.set(complaintWithId).await()
                onComplete(true)
            } catch (e: Exception) {
                _error.value = "Failed to submit complaint: ${e.message}"
                onComplete(false)
            }
        }
    }
}