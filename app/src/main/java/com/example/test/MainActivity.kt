package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.test.ui.*
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()
    private val teacherViewModel: TeacherViewModel by viewModels()
    private val marksViewModel: MarksViewModel by viewModels()
    private val announcementViewModel: AnnouncementViewModel by viewModels()
    private val leaveViewModel: LeaveViewModel by viewModels()
    private val disciplineViewModel: DisciplineViewModel by viewModels()
    private val syllabusViewModel: SyllabusViewModel by viewModels()
    private val principalViewModel: PrincipalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                val navController = rememberNavController()
                val userState = authViewModel.userState.value

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (userState == null && authViewModel.loading.value) {
                        LoadingScreen()
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = "login",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("login") {
                                LoginScreen(viewModel = authViewModel) { role ->
                                    val dest = when (role) {
                                        "principal" -> "principal_dashboard"
                                        "hm" -> "hm_dashboard"
                                        "teacher" -> "teacher_dashboard"
                                        "student" -> "student_dashboard"
                                        else -> "dashboard/$role"
                                    }
                                    navController.navigate(dest) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                            
                            composable("principal_dashboard") {
                                PrincipalDashboard(
                                    viewModel = principalViewModel,
                                    onNavigate = { route -> navController.navigate(route) },
                                    onLogout = {
                                        authViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("hm_dashboard") {
                                HMDashboard(
                                    viewModel = principalViewModel,
                                    onNavigate = { route -> navController.navigate(route) },
                                    onLogout = {
                                        authViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("teacher_dashboard") {
                                userState?.let { user ->
                                    TeacherDashboard(
                                        user = user,
                                        onNavigate = { route -> navController.navigate(route) },
                                        onLogout = {
                                            authViewModel.logout()
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }

                            composable("student_dashboard") {
                                userState?.let { user ->
                                    StudentDashboard(
                                        user = user,
                                        onNavigate = { route -> navController.navigate(route) },
                                        onLogout = {
                                            authViewModel.logout()
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }

                            composable(
                                route = "dashboard/{role}",
                                arguments = listOf(navArgument("role") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val role = backStackEntry.arguments?.getString("role") ?: ""
                                UnifiedDashboardScreen(
                                    user = userState,
                                    onNavigateToStudents = { navController.navigate("students") },
                                    onNavigateToTeachers = { navController.navigate("teachers") },
                                    onNavigateToMarksEntry = { navController.navigate("marks_entry") },
                                    onNavigateToFees = { navController.navigate("fees") },
                                    onNavigateToAnnouncements = { navController.navigate("announcements") },
                                    onNavigateToLeaves = { navController.navigate("leaves") },
                                    onNavigateToDiscipline = { navController.navigate("discipline") },
                                    onNavigateToSyllabus = { navController.navigate("syllabus") },
                                    onNavigateToSettings = { navController.navigate("settings") },
                                    onNavigateToAnalytics = { navController.navigate("analytics") },
                                    onLogout = {
                                        authViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("students") { StudentManagementScreen(studentViewModel, userState?.role ?: "") }
                            composable("teachers") { TeacherManagementScreen(principalViewModel, teacherViewModel, userState?.role ?: "") }
                            composable("marks_entry") { MarksEntryScreen(studentViewModel, marksViewModel, userState) }
                            composable("fees") { FeeAndRatingScreen(studentViewModel, userState?.role ?: "") }
                            composable("announcements") { AnnouncementScreen(announcementViewModel, userState?.role ?: "", userState?.email ?: "Unknown") }
                            composable("leaves") { LeaveScreen(leaveViewModel, userState?.role ?: "", userState?.userId ?: "", userState?.name ?: "Student", userState?.className) }
                            composable("discipline") { DisciplineScreen(disciplineViewModel, studentViewModel, userState?.role ?: "", userState?.userId ?: "", userState?.email ?: "Unknown", userState?.className ?: "General") }
                            composable("syllabus") { SyllabusScreen(syllabusViewModel, userState?.role ?: "", userState?.email ?: "Unknown", userState?.className ?: "5") }
                        }
                    }

                    LaunchedEffect(userState) {
                        if (userState != null) {
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute == "login" || currentRoute == null) {
                                val dest = when (userState.role) {
                                    "principal" -> "principal_dashboard"
                                    "hm" -> "hm_dashboard"
                                    "teacher" -> "teacher_dashboard"
                                    "student" -> "student_dashboard"
                                    else -> "dashboard/${userState.role}"
                                }
                                navController.navigate(dest) {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
