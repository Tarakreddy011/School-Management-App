package com.example.test.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
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
import com.example.test.model.Student

import androidx.compose.foundation.horizontalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagementScreen(
    viewModel: StudentViewModel,
    userRole: String
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("All") }
    var selectedStudentForDetail by remember { mutableStateOf<Student?>(null) }

    val students by viewModel.students
    val loading by viewModel.loading

    val classes = listOf("All", "Play Class", "LKG", "UKG") + (1..10).map { it.toString() }

    LaunchedEffect(selectedClass, searchQuery) {
        if (selectedClass == "All") {
            viewModel.fetchAllStudents()
        } else {
            viewModel.fetchStudents(selectedClass, searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Management",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (userRole == "principal" || userRole == "hm") {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Student",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == "principal" || userRole == "hm") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Student",
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
            // Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatItem(
                        title = "Total Students",
                        value = students.size.toString(),
                        icon = Icons.Default.Person
                    )
                    StatItem(
                        title = "Fees Paid",
                        value = students.count { it.feeStatus == "Paid" }.toString(),
                        icon = Icons.Default.CheckCircle
                    )
                    StatItem(
                        title = "Avg Rating",
                        value = "%.1f".format(students.map { it.rating }.average()),
                        icon = Icons.Default.Star
                    )
                }
            }

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
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Class Filter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        classes.forEach { cls ->
                            FilterChip(
                                selected = selectedClass == cls,
                                onClick = { selectedClass = cls },
                                label = { Text(cls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by name or roll number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
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
            } else if (students.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No students found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (selectedClass != "All") "No students in class $selectedClass"
                            else "Add students to get started",
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
                    items(students) { student ->
                        StudentCard(
                            student = student,
                            canDelete = userRole == "principal",
                            onDelete = {
                                viewModel.deleteStudent(student.studentId) {
                                    Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show()
                                    viewModel.fetchAllStudents()
                                }
                            },
                            onClick = { selectedStudentForDetail = student }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStudentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { student ->
                viewModel.addStudent(student) { success, addedStudent ->
                    if (success && addedStudent != null) {
                        Toast.makeText(
                            context,
                            "Student added successfully\nEmail: ${addedStudent.email}\nPassword: ${addedStudent.password}",
                            Toast.LENGTH_LONG
                        ).show()
                        showAddDialog = false
                        viewModel.fetchAllStudents()
                    } else {
                        Toast.makeText(context, "Failed to add student", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    if (selectedStudentForDetail != null) {
        StudentDetailDialog(
            student = selectedStudentForDetail!!,
            canDelete = userRole == "principal",
            onDismiss = { selectedStudentForDetail = null },
            onDelete = {
                viewModel.deleteStudent(selectedStudentForDetail!!.studentId) {
                    selectedStudentForDetail = null
                    viewModel.fetchAllStudents()
                }
            }
        )
    }
}

@Composable
fun StudentCard(
    student: Student,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                        text = student.name,
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
                            text = "Roll: ${student.rollNo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Class: ${student.className}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Action buttons
                Row {
                    // Fee Status Chip
                    FeeStatusChip(
                        status = student.feeStatus,
                        canEdit = false,
                        onClick = {}
                    )

                    if (canDelete) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Student Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        title = "Rating",
                        value = "%.1f/5.0".format(student.rating),
                        icon = Icons.Default.Star
                    )
                    InfoItem(
                        title = "Email",
                        value = student.email.take(12) + "...",
                        icon = Icons.Default.Email
                    )
                    InfoItem(
                        title = "DOB",
                        value = student.dob,
                        icon = Icons.Default.Cake
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// AddStudentDialog and StudentDetailDialog remain similar but with improved UI
// You can keep the existing logic but add Material3 styling

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onAdd: (Student) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Student", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (DD/MM/YYYY)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                // Class Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Select Class",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (listOf("Play Class", "LKG", "UKG") + (1..10).map { it.toString() }).forEach { cls ->
                                FilterChip(
                                    selected = selectedClass == cls,
                                    onClick = { selectedClass = cls },
                                    label = { Text(cls) }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = rollNo,
                    onValueChange = { rollNo = it },
                    label = { Text("Roll Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Parent Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && dob.isNotBlank() && rollNo.isNotBlank()) {
                        onAdd(Student(
                            name = name,
                            dob = dob,
                            className = selectedClass,
                            rollNo = rollNo,
                            parentPhone = phone
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && dob.isNotBlank() && rollNo.isNotBlank()
            ) {
                Text("Add Student")
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