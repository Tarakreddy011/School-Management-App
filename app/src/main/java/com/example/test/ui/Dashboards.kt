package com.example.test.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.model.User
import androidx.compose.ui.text.style.TextAlign

@Composable
fun UnifiedDashboardScreen(
    user: User?,
    onNavigateToStudents: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToLeaves: () -> Unit,
    onNavigateToDiscipline: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onLogout: () -> Unit
) {
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    when (user.role) {
        "principal" -> PrincipalDashboardView(user, onNavigateToStudents, onNavigateToTeachers,
            onNavigateToMarksEntry, onNavigateToFees, onNavigateToAnnouncements,
            onNavigateToLeaves, onNavigateToDiscipline, onNavigateToSyllabus,
            onNavigateToSettings, onNavigateToAnalytics, onLogout)
        "hm" -> HMDashboardView(user, onNavigateToStudents, onNavigateToTeachers,
            onNavigateToMarksEntry, onNavigateToFees, onNavigateToAnnouncements,
            onNavigateToLeaves, onNavigateToDiscipline, onNavigateToSyllabus, onLogout)
        "teacher" -> TeacherDashboardView(user, onNavigateToMarksEntry, onNavigateToSyllabus,
            onNavigateToAnnouncements, onNavigateToDiscipline, onNavigateToLeaves, onLogout)
        "student" -> StudentDashboardView(user, onNavigateToMarksEntry, onNavigateToLeaves,
            onNavigateToSyllabus, onNavigateToAnnouncements, onNavigateToDiscipline, onLogout)
        "incharge" -> InchargeDashboardView(user, onNavigateToStudents, onNavigateToMarksEntry,
            onNavigateToFees, onNavigateToLeaves, onNavigateToAnnouncements,
            onNavigateToDiscipline, onNavigateToSyllabus, onLogout)
        "trio" -> TrioDashboardView(user, onNavigateToMarksEntry, onNavigateToAnnouncements,
            onNavigateToSyllabus, onLogout)
        else -> DefaultDashboardView(user, onLogout)
    }
}

@Composable
fun PrincipalDashboardView(
    user: User,
    onNavigateToStudents: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToLeaves: () -> Unit,
    onNavigateToDiscipline: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "Principal Dashboard",
        subtitle = "School Control Center",
        user = user,
        menuItems = listOf(
            MenuAction("Students", Icons.Default.Person, onNavigateToStudents),
            MenuAction("Teachers", Icons.Default.Group, onNavigateToTeachers),
            MenuAction("Marks", Icons.Default.Assessment, onNavigateToMarksEntry),
            MenuAction("Fees & Ratings", Icons.Default.AttachMoney, onNavigateToFees),
            MenuAction("Announcements", Icons.Default.Notifications, onNavigateToAnnouncements),
            MenuAction("Discipline", Icons.Default.Security, onNavigateToDiscipline),
            MenuAction("Complaints", Icons.Default.Report, onNavigateToDiscipline),
            MenuAction("Leaves", Icons.Default.CalendarToday, onNavigateToLeaves),
            MenuAction("Syllabus", Icons.Default.Book, onNavigateToSyllabus),
            MenuAction("Analytics", Icons.Default.Analytics, onNavigateToAnalytics),
            MenuAction("Settings", Icons.Default.Settings, onNavigateToSettings)
        ),
        onLogout = onLogout
    )
}

@Composable
fun HMDashboardView(
    user: User,
    onNavigateToStudents: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToLeaves: () -> Unit,
    onNavigateToDiscipline: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "Head Master Dashboard",
        subtitle = "Academic Management",
        user = user,
        menuItems = listOf(
            MenuAction("Students", Icons.Default.Person, onNavigateToStudents),
            MenuAction("Staff Assignment", Icons.Default.Assignment, onNavigateToTeachers),
            MenuAction("Marks Entry", Icons.Default.Edit, onNavigateToMarksEntry),
            MenuAction("Fees & Ratings", Icons.Default.AttachMoney, onNavigateToFees),
            MenuAction("Announcements", Icons.Default.Notifications, onNavigateToAnnouncements),
            MenuAction("Discipline", Icons.Default.Security, onNavigateToDiscipline),
            MenuAction("Complaints", Icons.Default.Report, onNavigateToDiscipline),
            MenuAction("Leaves", Icons.Default.CalendarToday, onNavigateToLeaves),
            MenuAction("Syllabus", Icons.Default.Book, onNavigateToSyllabus)
        ),
        onLogout = onLogout
    )
}

@Composable
fun TeacherDashboardView(
    user: User,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToDiscipline: () -> Unit,
    onNavigateToLeaves: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "Teacher Dashboard",
        subtitle = "${user.subject} • Classes: ${user.classesAssigned.joinToString(", ")}",
        user = user,
        menuItems = listOf(
            MenuAction("Enter Marks", Icons.Default.Edit, onNavigateToMarksEntry),
            MenuAction("Update Syllabus", Icons.Default.Book, onNavigateToSyllabus),
            MenuAction("Announcements", Icons.Default.Notifications, onNavigateToAnnouncements),
            MenuAction("Discipline", Icons.Default.Security, onNavigateToDiscipline),
            MenuAction("Leave Status", Icons.Default.CalendarToday, onNavigateToLeaves)
        ),
        onLogout = onLogout
    )
}

@Composable
fun StudentDashboardView(
    user: User,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToLeaves: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToDiscipline: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "Student Portal",
        subtitle = "Class ${user.className ?: "Not Assigned"} • Welcome, ${user.name}",
        user = user,
        menuItems = listOf(
            MenuAction("My Report Card", Icons.Default.Assessment, onNavigateToMarksEntry),
            MenuAction("Apply Leave", Icons.Default.EventNote, onNavigateToLeaves),
            MenuAction("Syllabus", Icons.Default.Book, onNavigateToSyllabus),
            MenuAction("Announcements", Icons.Default.Notifications, onNavigateToAnnouncements),
            MenuAction("Complaints", Icons.Default.SupportAgent, onNavigateToDiscipline)
        ),
        onLogout = onLogout
    )
}

@Composable
fun InchargeDashboardView(
    user: User,
    onNavigateToStudents: () -> Unit,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToLeaves: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToDiscipline: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "Incharge Dashboard",
        subtitle = "Class Supervisor",
        user = user,
        menuItems = listOf(
            MenuAction("Students", Icons.Default.Person, onNavigateToStudents),
            MenuAction("Marks Entry", Icons.Default.Edit, onNavigateToMarksEntry),
            MenuAction("Fees & Ratings", Icons.Default.AttachMoney, onNavigateToFees),
            MenuAction("Leave Approval", Icons.Default.CalendarToday, onNavigateToLeaves),
            MenuAction("Announcements", Icons.Default.Notifications, onNavigateToAnnouncements),
            MenuAction("Discipline", Icons.Default.Security, onNavigateToDiscipline),
            MenuAction("Syllabus", Icons.Default.Book, onNavigateToSyllabus)
        ),
        onLogout = onLogout
    )
}

@Composable
fun TrioDashboardView(
    user: User,
    onNavigateToMarksEntry: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "Trio Teacher Dashboard",
        subtitle = "Play, LKG, UKG Classes",
        user = user,
        menuItems = listOf(
            MenuAction("Enter Marks", Icons.Default.Edit, onNavigateToMarksEntry),
            MenuAction("Daily Diary", Icons.Default.NoteAdd, onNavigateToAnnouncements),
            MenuAction("Syllabus", Icons.Default.Book, onNavigateToSyllabus)
        ),
        onLogout = onLogout
    )
}

@Composable
fun DefaultDashboardView(
    user: User,
    onLogout: () -> Unit
) {
    DashboardTemplate(
        title = "${user.role.uppercase()} Dashboard",
        subtitle = "Welcome, ${user.name}",
        user = user,
        menuItems = emptyList(),
        onLogout = onLogout
    )
}

@Composable
fun DashboardTemplate(
    title: String,
    subtitle: String,
    user: User,
    menuItems: List<MenuAction>,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onLogout,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (user.role) {
                        "principal" -> Icons.Default.School
                        "hm" -> Icons.Default.AdminPanelSettings
                        "teacher" -> Icons.Default.Person
                        "student" -> Icons.Default.School
                        else -> Icons.Default.AccountCircle
                    },
                    contentDescription = "Role",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.role.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (user.className != null) {
                        Text(
                            text = "Class: ${user.className}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Grid
        if (menuItems.isNotEmpty()) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuItems) { item ->
                    ActionCard(item.label, item.icon) { item.action() }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No permissions assigned",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Contact administrator for access",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
