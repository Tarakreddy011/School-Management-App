package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.model.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AnnouncementViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _announcements = mutableStateOf<List<Announcement>>(emptyList())
    val announcements: State<List<Announcement>> = _announcements

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchAnnouncements(target: String? = null) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                var query: Query = db.collection("announcements")
                    .orderBy("timestamp", Query.Direction.DESCENDING)

                if (target != null && target != "all") {
                    query = query.whereEqualTo("target", target)
                }

                val result = query.get().await()
                _announcements.value = result.toObjects(Announcement::class.java)
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to load announcements: ${e.message}"
            }
        }
    }

    fun postAnnouncement(announcement: Announcement, onComplete: (Boolean) -> Unit) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val docRef = db.collection("announcements").document()
                val announcementWithId = announcement.copy(
                    announceId = docRef.id,
                    timestamp = System.currentTimeMillis()
                )
                docRef.set(announcementWithId).await()
                _loading.value = false
                onComplete(true)
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to post announcement: ${e.message}"
                onComplete(false)
            }
        }
    }
}