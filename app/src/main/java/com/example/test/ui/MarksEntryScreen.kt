package com.example.test.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.model.Marks
import com.example.test.model.Student
import com.example.test.model.User

@Composable
fun MarksEntryScreen(
    studentViewModel: StudentViewModel,
    marksViewModel: MarksViewModel,
    currentUser: User?
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("All") }
    val students by studentViewModel.students
    val allMarks by marksViewModel.allMarks
    val loading by studentViewModel.loading

    val isStudent = currentUser?.role == "student"
    val classes = listOf("All", "Play Class", "LKG", "UKG") + (1..10).map { it.toString() }
    val expandedClasses = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(selectedClass) {
        if (selectedClass == "All") {
            studentViewModel.fetchAllStudents()
        } else {
            studentViewModel.fetchStudents(selectedClass, "")
        }
    }

    LaunchedEffect(Unit) {
        marksViewModel.fetchAllMarks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isStudent) "My Report Card" else "Marks Management",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (!isStudent && currentUser?.role != "trio") {
                // Class Filter for Teachers/HM/Principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Class Filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            classes.forEach { className ->
                                FilterChip(
                                    selected = selectedClass == className,
                                    onClick = { selectedClass = className },
                                    label = { Text(className) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!isStudent && currentUser?.role != "trio") {
                // Search Bar for non-students
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search student by name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (currentUser?.role == "teacher") {
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Subject: ${currentUser.subject} • Assigned Classes: ${currentUser.classesAssigned.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Determine which students to show based on user role
                val filteredStudents = students.filter { student ->
                    when (currentUser?.role) {
                        "student" -> student.studentId == currentUser.userId
                        "teacher" -> {
                            val matchesSearch = student.name.contains(searchQuery, ignoreCase = true)
                            val isAssigned = currentUser.classesAssigned.contains(student.className)
                            matchesSearch && isAssigned
                        }
                        "trio" -> {
                            val isTrioClass = student.className in listOf("Play Class", "LKG", "UKG")
                            isTrioClass && student.name.contains(searchQuery, ignoreCase = true)
                        }
                        else -> student.name.contains(searchQuery, ignoreCase = true)
                    }
                }

                if (filteredStudents.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.School,
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
                                text = if (searchQuery.isNotEmpty()) "Try a different search term"
                                else "No students in selected class",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(filteredStudents, key = { it.studentId }) { student ->
                            StudentMarksReportCard(
                                student = student,
                                marks = allMarks.filter { it.studentId == student.studentId },
                                currentUser = currentUser,
                                onUpdate = { sub, mks ->
                                    marksViewModel.updateMarks(student.studentId, student.className, sub, mks)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentMarksReportCard(
    student: Student,
    marks: List<Marks>,
    currentUser: User?,
    onUpdate: (String, Marks) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedSubjectToEdit by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Student Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Roll No: ${student.rollNo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Class: ${student.className}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (currentUser?.role == "teacher" || currentUser?.role == "hm") {
                    Button(
                        onClick = {
                            selectedSubjectToEdit = currentUser.subject
                            showEditDialog = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (currentUser.role == "hm") "Update Marks" else "Update ${currentUser.subject}",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Marks Table
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        MaterialTheme.shapes.small
                    )
            ) {
                Column {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        TableCell("Subject", 120.dp, FontWeight.Bold)
                        TableCell("ST", 50.dp, FontWeight.Bold)
                        TableCell("FA1", 50.dp, FontWeight.Bold)
                        TableCell("FA2", 50.dp, FontWeight.Bold)
                        TableCell("FA3", 50.dp, FontWeight.Bold)
                        TableCell("FA4", 50.dp, FontWeight.Bold)
                        TableCell("SA1", 50.dp, FontWeight.Bold)
                        TableCell("SA2", 50.dp, FontWeight.Bold)
                    }

                    if (marks.isEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "No marks available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        marks.forEach { mark ->
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(mark.subject, 120.dp, FontWeight.Medium)
                                TableCell(mark.slipTest.toString(), 50.dp)
                                TableCell(mark.fa1.toString(), 50.dp)
                                TableCell(mark.fa2.toString(), 50.dp)
                                TableCell(mark.fa3.toString(), 50.dp)
                                TableCell(mark.fa4.toString(), 50.dp)
                                TableCell(mark.sa1.toString(), 50.dp)
                                TableCell(mark.sa2.toString(), 50.dp)
                            }
                            Divider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }

            // Summary Row
            if (marks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.shapes.small
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Subjects: ${marks.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    val totalMarks = marks.sumOf { it.fa1 + it.fa2 + it.fa3 + it.fa4 + it.sa1 + it.sa2 }
                    val average = if (marks.isNotEmpty()) totalMarks / (marks.size * 6) else 0
                    Text(
                        text = "Average: ${"%.1f".format(average.toFloat())}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        val currentMarks = marks.find { it.subject == selectedSubjectToEdit }
            ?: Marks(subject = selectedSubjectToEdit)
        EditMarksDialog(
            subject = selectedSubjectToEdit,
            studentName = student.name,
            initialMarks = currentMarks,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onUpdate(selectedSubjectToEdit, updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditMarksDialog(
    subject: String,
    studentName: String,
    initialMarks: Marks,
    onDismiss: () -> Unit,
    onSave: (Marks) -> Unit
) {
    var marks by remember { mutableStateOf(initialMarks) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Update Marks", fontWeight = FontWeight.Bold)
                Text(
                    text = "$subject • $studentName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MarksInputField("Slip Test (20)", marks.slipTest, 20) { marks = marks.copy(slipTest = it) }
                MarksInputField("FA1 (10)", marks.fa1, 10) { marks = marks.copy(fa1 = it) }
                MarksInputField("FA2 (10)", marks.fa2, 10) { marks = marks.copy(fa2 = it) }
                MarksInputField("FA3 (10)", marks.fa3, 10) { marks = marks.copy(fa3 = it) }
                MarksInputField("FA4 (10)", marks.fa4, 10) { marks = marks.copy(fa4 = it) }
                MarksInputField("SA1 (40)", marks.sa1, 40) { marks = marks.copy(sa1 = it) }
                MarksInputField("SA2 (40)", marks.sa2, 40) { marks = marks.copy(sa2 = it) }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(marks) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Marks")
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

@Composable
fun MarksInputField(
    label: String,
    value: Int,
    maxValue: Int = 100,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Max: $maxValue",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedTextField(
            value = if (value == 0) "" else value.toString(),
            onValueChange = {
                val newValue = it.toIntOrNull() ?: 0
                onValueChange(newValue.coerceIn(0, maxValue))
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            isError = value > maxValue,
            supportingText = {
                if (value > maxValue) {
                    Text("Value cannot exceed $maxValue")
                }
            }
        )
    }
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    weight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontWeight = weight,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}