package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.test.model.Marks
import com.google.firebase.firestore.FirebaseFirestore

class MarksViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _allMarks = mutableStateOf<List<Marks>>(emptyList())
    val allMarks: State<List<Marks>> = _allMarks

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    init {
        startMarksListener()
    }

    private fun startMarksListener() {
        _loading.value = true
        db.collection("marks").addSnapshotListener { snapshot, error ->
            if (error != null) {
                _loading.value = false
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _allMarks.value = snapshot.toObjects(Marks::class.java)
            }
            _loading.value = false
        }
    }

    fun fetchAllMarks() {
        // Data is now handled by the listener automatically
    }

    fun updateMarks(studentId: String, className: String, subject: String, marks: Marks) {
        val docId = "${studentId}_${subject}"
        val data = hashMapOf(
            "studentId" to studentId,
            "className" to className,
            "subject" to subject,
            "slipTest" to marks.slipTest,
            "fa1" to marks.fa1,
            "fa2" to marks.fa2,
            "fa3" to marks.fa3,
            "fa4" to marks.fa4,
            "sa1" to marks.sa1,
            "sa2" to marks.sa2
        )
        db.collection("marks").document(docId).set(data)
    }
}
