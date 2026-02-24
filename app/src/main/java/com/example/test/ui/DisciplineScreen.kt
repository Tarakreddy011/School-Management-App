package com.example.test.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.test.model.Complaint
import com.example.test.model.DisciplineCase
import com.example.test.model.Student
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisciplineScreen(
    viewModel: DisciplineViewModel,
    studentViewModel: StudentViewModel,
    userRole: String,
    userId: String,
    userEmail: String,
    userClass: String?
) {
    var activeTab by remember { mutableStateOf(0) }
    var showLogCaseDialog by remember { mutableStateOf(false) }
    var showComplaintDialog by remember { mutableStateOf(false) }

    val disciplineCases by viewModel.disciplineCases
    val complaints by viewModel.complaints
    val loading by viewModel.loading

    val tabs = listOf("Discipline Cases", "Complaints")

    LaunchedEffect(activeTab) {
        if (activeTab == 0) viewModel.fetchDisciplineCases()
        else viewModel.fetchComplaints()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discipline & Complaints",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            when {
                activeTab == 0 && (userRole == "teacher" || userRole == "hm" || userRole == "principal") -> {
                    FloatingActionButton(
                        onClick = { showLogCaseDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Log Discipline Case",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                activeTab == 1 && userRole == "student" -> {
                    FloatingActionButton(
                        onClick = { showComplaintDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Submit Complaint",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Security
                                    else -> Icons.Default.Report
                                },
                                contentDescription = title
                            )
                        }
                    )
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
                when (activeTab) {
                    0 -> DisciplineTabContent(
                        disciplineCases = disciplineCases,
                        userRole = userRole,
                        userId = userId,
                        showLogCaseDialog = { showLogCaseDialog = true }
                    )
                    1 -> ComplaintsTabContent(
                        complaints = complaints,
                        userRole = userRole,
                        userClass = userClass,
                        showComplaintDialog = { showComplaintDialog = true }
                    )
                }
            }
        }
    }

    if (showLogCaseDialog) {
        LogDisciplineDialog(
            studentViewModel = studentViewModel,
            onDismiss = { showLogCaseDialog = false },
            onLog = { case ->
                viewModel.logDisciplineCase(case) {
                    showLogCaseDialog = false
                    viewModel.fetchDisciplineCases()
                }
            },
            createdBy = userEmail
        )
    }

    if (showComplaintDialog) {
        SubmitComplaintDialog(
            onDismiss = { showComplaintDialog = false },
            onPost = { message ->
                viewModel.submitComplaint(Complaint(message = message, className = userClass ?: "Unknown")) {
                    showComplaintDialog = false
                }
            }
        )
    }
}

@Composable
fun DisciplineTabContent(
    disciplineCases: List<DisciplineCase>,
    userRole: String,
    userId: String,
    showLogCaseDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Action Button for authorized users
        if (userRole == "teacher" || userRole == "hm" || userRole == "principal") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
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
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Discipline Management",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Log disciplinary actions for student behavior tracking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = showLogCaseDialog,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Log Case")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Privacy notice for students
        if (userRole == "student") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
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
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "You can only view your own discipline cases",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filter cases based on user role
        val filteredCases = if (userRole == "student") {
            disciplineCases.filter { it.studentId == userId }
        } else {
            disciplineCases
        }

        if (filteredCases.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No discipline cases",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (userRole == "student") "You have no discipline cases"
                        else "No cases have been logged yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredCases) { case ->
                    DisciplineItem(case)
                }
            }
        }
    }
}

@Composable
fun ComplaintsTabContent(
    complaints: List<Complaint>,
    userRole: String,
    userClass: String?,
    showComplaintDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Student Actions
        if (userRole == "student") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Anonymous Complaint System",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complaints are submitted anonymously and can only be viewed by the Principal and Head Master. Your identity will remain confidential.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = showComplaintDialog,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Complaint")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Admin View
        if (userRole == "principal" || userRole == "hm") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
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
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Administrative View",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Review anonymous complaints from students",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Total: ${complaints.size}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filter complaints for incharges
        val filteredComplaints = if (userRole == "incharge" && userClass != null) {
            complaints.filter { it.className == userClass }
        } else if (userRole == "principal" || userRole == "hm") {
            complaints
        } else {
            emptyList()
        }

        if (filteredComplaints.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No complaints",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = when {
                            userRole == "student" -> "You have not submitted any complaints"
                            userRole == "incharge" -> "No complaints for your class"
                            else -> "No complaints have been submitted yet"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredComplaints) { complaint ->
                    ComplaintItem(complaint)
                }
            }
        }
    }
}

@Composable
fun DisciplineItem(case: DisciplineCase) {
    val date = Date(case.timestamp)
    val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (case.level) {
                "Major" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = case.studentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ID: ${case.studentId.take(6)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = when (case.level) {
                        "Major" -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = case.level,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (case.level) {
                            "Major" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = case.description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Logged by: ${case.createdBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = format.format(date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ComplaintItem(complaint: Complaint) {
    val date = Date(complaint.timestamp)
    val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Anonymous Complaint",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Class: ${complaint.className}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Anonymous",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Message
            Text(
                text = complaint.message,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Text(
                text = format.format(date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LogDisciplineDialog(
    studentViewModel: StudentViewModel,
    onDismiss: () -> Unit,
    onLog: (DisciplineCase) -> Unit,
    createdBy: String
) {
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Minor") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Discipline Case", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Level:", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = level == "Minor", onClick = { level = "Minor" })
                    Text("Minor", modifier = Modifier.clickable { level = "Minor" })
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = level == "Major", onClick = { level = "Major" })
                    Text("Major", modifier = Modifier.clickable { level = "Major" })
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onLog(DisciplineCase(studentId = studentId, studentName = studentName, level = level, description = description, createdBy = createdBy)) }) {
                Text("Log Case")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SubmitComplaintDialog(onDismiss: () -> Unit, onPost: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit Anonymous Complaint", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Your message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
        },
        confirmButton = {
            Button(onClick = { if (message.isNotBlank()) onPost(message) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
