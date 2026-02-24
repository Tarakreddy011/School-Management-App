package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.test.model.Syllabus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SyllabusViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _syllabusList = mutableStateOf<List<Syllabus>>(emptyList())
    val syllabusList: State<List<Syllabus>> = _syllabusList

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun fetchSyllabus(className: String) {
        _loading.value = true
        db.collection("syllabus")
            .whereEqualTo("className", className)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                _syllabusList.value = result.toObjects(Syllabus::class.java)
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    fun updateSyllabus(syllabus: Syllabus, onComplete: (Boolean) -> Unit) {
        _loading.value = true
        val docRef = db.collection("syllabus").document()
        val syllabusWithId = syllabus.copy(syllabusId = docRef.id)
        docRef.set(syllabusWithId)
            .addOnSuccessListener {
                _loading.value = false
                onComplete(true)
            }
            .addOnFailureListener {
                _loading.value = false
                onComplete(false)
            }
    }
}
