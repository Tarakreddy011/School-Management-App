package com.example.test.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _userState = mutableStateOf<User?>(null)
    val userState: State<User?> = _userState

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        checkUser()
    }

    private fun checkUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
        }
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                val user = auth.currentUser
                if (user != null) {
                    fetchUserData(user.uid, onSuccess)
                } else {
                    _loading.value = false
                    _error.value = "Login failed. Please try again."
                }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = when {
                    e.message?.contains("invalid-email") == true -> "Invalid email format"
                    e.message?.contains("user-not-found") == true -> "User not found"
                    e.message?.contains("wrong-password") == true -> "Incorrect password"
                    else -> "Login failed: ${e.message}"
                }
            }
        }
    }

    private fun fetchUserData(uid: String, onSuccess: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                // 1. First check the 'users' collection (Staff/Principal/HM)
                val userDoc = db.collection("users").document(uid).get().await()

                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)?.copy(
                        userId = uid,
                        role = userDoc.getString("role")?.trim()?.lowercase() ?: ""
                    )
                    _userState.value = user
                    _loading.value = false
                    user?.let { onSuccess?.invoke(it.role) }
                } else {
                    // 2. If not in 'users', check 'students' collection
                    val studDoc = db.collection("students").document(uid).get().await()
                    if (studDoc.exists()) {
                        val user = User(
                            userId = uid,
                            name = studDoc.getString("name") ?: "",
                            role = "student",
                            email = studDoc.getString("email") ?: "",
                            className = studDoc.getString("className"),
                            feeStatus = studDoc.getString("feeStatus") ?: "Pending",
                            rating = studDoc.getDouble("rating")?.toFloat() ?: 0f
                        )
                        _userState.value = user
                        _loading.value = false
                        onSuccess?.invoke("student")
                    } else {
                        // 3. Check 'teachers' collection for backward compatibility
                        val teacherDoc = db.collection("teachers").document(uid).get().await()
                        if (teacherDoc.exists()) {
                            val user = User(
                                userId = uid,
                                name = teacherDoc.getString("name") ?: "",
                                role = teacherDoc.getString("role")?.trim()?.lowercase() ?: "teacher",
                                email = teacherDoc.getString("email") ?: "",
                                subject = teacherDoc.getString("subject") ?: "",
                                classesAssigned = (teacherDoc.get("classesAssigned") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                phone = teacherDoc.getString("phone") ?: ""
                            )
                            _userState.value = user
                            _loading.value = false
                            onSuccess?.invoke(user.role)
                        } else {
                            _loading.value = false
                            _error.value = "User profile not found in database. Please contact administrator."
                        }
                    }
                }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error fetching user data: ${e.message}"
            }
        }
    }

    fun logout() {
        auth.signOut()
        _userState.value = null
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
