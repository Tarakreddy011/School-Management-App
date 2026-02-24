package com.example.test.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HMDashboard(
    viewModel: PrincipalViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val summary by viewModel.summary
    val loading by viewModel.loading

    LaunchedEffect(Unit) {
        viewModel.fetchSummary()
    }

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
                    text = "HM Control Center",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Head Master Dashboard",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Overview Section
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Students",
                    value = summary.totalStudents.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Teachers",
                    value = summary.totalTeachers.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Fees Due",
                    value = summary.pendingFees.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Management Grid
        Text(
            text = "Management Modules",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val menuItems = listOf(
            MenuAction("Staff Assignment", Icons.Default.Face) { onNavigate("teachers") },
            MenuAction("Manage Students", Icons.Default.Person) { onNavigate("students") },
            MenuAction("Marks Entry", Icons.Default.Edit) { onNavigate("marks_entry") },
            MenuAction("Fees Records", Icons.Default.ShoppingCart) { onNavigate("fees") },
            MenuAction("Announcements", Icons.Default.Notifications) { onNavigate("announcements") },
            MenuAction("Syllabus Tracker", Icons.Default.List) { onNavigate("syllabus") },
            MenuAction("Leaves", Icons.Default.DateRange) { onNavigate("leaves") },
            MenuAction("Complaints", Icons.Default.Email) { onNavigate("discipline") }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                ActionCard(item) { item.action() }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
