package com.example.test.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.model.Leave
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveScreen(
    viewModel: LeaveViewModel,
    userRole: String,
    userId: String,
    userName: String,
    userClass: String?
) {
    val context = LocalContext.current
    var showApplyDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("all") }
    val leaves by viewModel.leaves
    val loading by viewModel.loading

    val filters = listOf(
        "all" to "All",
        "pending" to "Pending",
        "accepted" to "Accepted",
        "declined" to "Declined"
    )

    LaunchedEffect(userRole, userId, userClass, selectedFilter) {
        when (userRole) {
            "student" -> viewModel.fetchLeavesForStudent(userId)
            "hm", "principal" -> viewModel.fetchAllLeaves()
            "incharge" -> {
                if (userClass != null) viewModel.fetchLeavesForIncharge(userClass)
                else viewModel.fetchAllLeaves()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Leave Management",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (userRole == "student") {
                        IconButton(onClick = { showApplyDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Apply Leave",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == "student") {
                FloatingActionButton(
                    onClick = { showApplyDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Apply Leave",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Role-specific header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (userRole) {
                        "student" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        "incharge" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (userRole) {
                            "student" -> Icons.Default.Person
                            "incharge" -> Icons.Default.SupervisorAccount
                            else -> Icons.Default.AdminPanelSettings
                        },
                        contentDescription = null,
                        tint = when (userRole) {
                            "student" -> MaterialTheme.colorScheme.secondary
                            "incharge" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (userRole) {
                                "student" -> "My Leave Applications"
                                "incharge" -> "Class Supervisor: $userClass"
                                else -> "Administrative Control"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = when (userRole) {
                                "student" -> "Track your personal leave requests"
                                "incharge" -> "Review and approve leave requests"
                                else -> "Manage all student leaves"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (userRole != "student") {
                        Text(
                            text = "Total: ${leaves.size}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Filter chips for non-students
            if (userRole != "student") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { (value, label) ->
                            FilterChip(
                                selected = selectedFilter == value,
                                onClick = { selectedFilter = value },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Filter leaves based on selected filter
                val filteredLeaves = leaves.filter { leave ->
                    when (selectedFilter) {
                        "pending" -> leave.status == "Pending"
                        "accepted" -> leave.status == "Accepted"
                        "declined" -> leave.status == "Declined"
                        else -> true
                    }
                }

                if (filteredLeaves.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No leave applications",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when {
                                    userRole == "student" && selectedFilter == "all" -> "You haven't applied for any leaves yet"
                                    userRole == "student" -> "No leaves with selected status"
                                    selectedFilter != "all" -> "No leaves with status: ${selectedFilter.replaceFirstChar { it.uppercase() }}"
                                    else -> "No leave applications to show"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredLeaves) { leave ->
                            LeaveItem(
                                leave = leave,
                                userRole = userRole,
                                onStatusChange = { status ->
                                    viewModel.updateLeaveStatus(leave.leaveId, status) { success ->
                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                "Leave $status successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showApplyDialog) {
        ApplyLeaveDialog(
            onDismiss = { showApplyDialog = false },
            onApply = { type, reason ->
                val leave = Leave(
                    studentId = userId,
                    studentName = userName,
                    className = userClass ?: "Unknown",
                    type = type,
                    reason = reason
                )
                viewModel.applyLeave(leave) { success ->
                    if (success) {
                        Toast.makeText(
                            context,
                            "Leave Application Submitted",
                            Toast.LENGTH_LONG
                        ).show()
                        showApplyDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun LeaveItem(leave: Leave, userRole: String, onStatusChange: (String) -> Unit) {
    val date = Date(leave.dateApplied)
    val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val canManage = userRole == "hm" || userRole == "principal" || userRole == "incharge"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = leave.studentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Class: ${leave.className}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Type: ${leave.type}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                StatusChip(leave.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reason
            Text(
                text = leave.reason,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = format.format(date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (userRole == "principal" || userRole == "hm") {
                    Text(
                        text = "ID: ${leave.leaveId.take(6)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            // Management Buttons
            if (canManage && leave.status == "Pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStatusChange("Accepted") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accept", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { onStatusChange("Declined") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Decline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (containerColor, contentColor) = when (status) {
        "Accepted" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "Declined" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ApplyLeaveDialog(onDismiss: () -> Unit, onApply: (String, String) -> Unit) {
    var type by remember { mutableStateOf("Sick") }
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "New Leave Application",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Leave Type
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Type of Leave",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = type == "Sick",
                                    onClick = { type = "Sick" }
                                )
                                Text(
                                    "Sick Leave",
                                    modifier = Modifier.clickable { type = "Sick" }
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = type == "Holiday",
                                    onClick = { type = "Holiday" }
                                )
                                Text(
                                    "Holiday",
                                    modifier = Modifier.clickable { type = "Holiday" }
                                )
                            }
                        }
                    }
                }

                // Reason
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Leave") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isNotBlank()) onApply(type, reason)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = reason.isNotBlank()
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    )
}