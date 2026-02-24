package com.example.test.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.test.model.Syllabus
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusScreen(
    viewModel: SyllabusViewModel,
    userRole: String,
    userEmail: String,
    initialClass: String?
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var className by remember { mutableStateOf(initialClass ?: "5") }
    var selectedSubject by remember { mutableStateOf("All") }
    val syllabusList by viewModel.syllabusList
    val loading by viewModel.loading

    val classes = listOf("Play Class", "LKG", "UKG") + (1..10).map { it.toString() }
    val subjects = listOf("All") + listOf("Telugu", "Hindi", "English", "Maths", "Science",
        "Social", "Computer", "PT", "Art")

    LaunchedEffect(className, selectedSubject) {
        viewModel.fetchSyllabus(className)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Syllabus Tracker",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (userRole == "teacher" || userRole == "hm" || userRole == "principal") {
                        IconButton(onClick = { showUpdateDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Update Syllabus",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == "teacher" || userRole == "hm" || userRole == "principal") {
                FloatingActionButton(
                    onClick = { showUpdateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Update Syllabus",
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
            // Filters Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Class Filter
                    Text(
                        text = "Class",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        classes.forEach { cls ->
                            FilterChip(
                                selected = className == cls,
                                onClick = { className = cls },
                                label = { Text(cls) }
                            )
                        }
                    }

                    // Subject Filter (for non-teachers)
                    if (userRole != "teacher") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Subject",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            subjects.forEach { subject ->
                                FilterChip(
                                    selected = selectedSubject == subject,
                                    onClick = { selectedSubject = subject },
                                    label = { Text(subject) }
                                )
                            }
                        }
                    }
                }
            }

            // Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (userRole) {
                        "teacher" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        "student" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
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
                            "teacher" -> Icons.Default.Edit
                            "student" -> Icons.Default.School
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (userRole) {
                            "teacher" -> MaterialTheme.colorScheme.secondary
                            "student" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (userRole) {
                                "teacher" -> "Update daily syllabus for your assigned classes"
                                "student" -> "Track daily syllabus for Class $className"
                                else -> "Syllabus Tracking System"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = when (userRole) {
                                "teacher" -> "Keep students updated with daily topics"
                                "student" -> "Stay updated with your class curriculum"
                                else -> "Monitor syllabus progress across classes"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${syllabusList.size} entries",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
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
                // Filter syllabus based on selected subject
                val filteredSyllabus = if (selectedSubject == "All") {
                    syllabusList
                } else {
                    syllabusList.filter { it.subject == selectedSubject }
                }

                if (filteredSyllabus.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No syllabus entries",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when {
                                    userRole == "teacher" -> "Update syllabus for Class $className"
                                    selectedSubject != "All" -> "No entries for $selectedSubject"
                                    else -> "No syllabus available for Class $className"
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
                        items(filteredSyllabus) { syllabus ->
                            SyllabusItem(syllabus)
                        }
                    }
                }
            }
        }
    }

    if (showUpdateDialog) {
        UpdateSyllabusDialog(
            onDismiss = { showUpdateDialog = false },
            onUpdate = { subject, topic ->
                val newSyllabus = Syllabus(
                    className = className,
                    subject = subject,
                    topic = topic,
                    updatedBy = userEmail
                )
                viewModel.updateSyllabus(newSyllabus) {
                    showUpdateDialog = false
                    viewModel.fetchSyllabus(className)
                }
            }
        )
    }
}

@Composable
fun SyllabusItem(syllabus: Syllabus) {
    val date = Date(syllabus.timestamp)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                        text = syllabus.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Class: ${syllabus.className}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = format.format(date),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Topic
            Text(
                text = "Topic:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = syllabus.topic,
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
                    text = "Updated by: ${syllabus.updatedBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (syllabus.syllabusId.isNotEmpty()) {
                    Text(
                        text = "ID: ${syllabus.syllabusId.take(6)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSyllabusDialog(onDismiss: () -> Unit, onUpdate: (String, String) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Update Daily Syllabus",
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
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    placeholder = { Text("e.g., Maths, Science, English") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic Covered") },
                    placeholder = { Text("e.g., Chapter 3: Algebraic Expressions") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && topic.isNotBlank()) {
                        onUpdate(subject, topic)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = subject.isNotBlank() && topic.isNotBlank()
            ) {
                Text("Update Syllabus")
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