package com.example.test.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeAndRatingScreen(
    viewModel: StudentViewModel,
    userRole: String
) {
    var className by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val students by viewModel.students
    val loading by viewModel.loading

    val classes = listOf("All") + listOf("Play Class", "LKG", "UKG") + (1..10).map { it.toString() }

    LaunchedEffect(className, searchQuery) {
        if (className == "All") {
            viewModel.fetchAllStudents()
        } else {
            viewModel.fetchStudents(className, searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fees & Ratings",
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
            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        title = "Fees Pending",
                        value = students.count { it.feeStatus != "Paid" }.toString(),
                        icon = Icons.Default.Warning
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                selected = className == cls,
                                onClick = { className = cls },
                                label = { Text(cls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search student by name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role Notice
            if (userRole != "principal" && userRole != "hm" && userRole != "incharge") {
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
                            text = "View-only mode. Contact administrator for changes.",
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
            } else if (students.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
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
                            text = if (className != "All") "No students in class $className"
                            else "Try selecting a specific class",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(students) { student ->
                        FeeAndRatingItem(
                            student = student,
                            canEdit = userRole == "hm" || userRole == "principal" || userRole == "incharge",
                            onRatingChange = { viewModel.updateStudentRating(student.studentId, it) },
                            onFeeStatusChange = { viewModel.updateFeeStatus(student.studentId, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FeeAndRatingItem(
    student: Student,
    canEdit: Boolean,
    onRatingChange: (Float) -> Unit,
    onFeeStatusChange: (String) -> Unit
) {
    var currentRating by remember(student.rating) { mutableStateOf(student.rating) }
    var currentFeeStatus by remember(student.feeStatus) { mutableStateOf(student.feeStatus) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Student Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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

                // Fee Status
                FeeStatusChip(
                    status = currentFeeStatus,
                    canEdit = canEdit,
                    onClick = {
                        if (canEdit) {
                            val newStatus = if (currentFeeStatus == "Paid") "Pending" else "Paid"
                            currentFeeStatus = newStatus
                            onFeeStatusChange(newStatus)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student Rating",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "%.1f/5.0".format(currentRating),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (canEdit) {
                        Slider(
                            value = currentRating,
                            onValueChange = { currentRating = it },
                            onValueChangeFinished = { onRatingChange(currentRating) },
                            valueRange = 0f..5f,
                            steps = 10,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0", style = MaterialTheme.typography.labelSmall)
                            Text("2.5", style = MaterialTheme.typography.labelSmall)
                            Text("5.0", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        // Display rating stars for view-only
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (currentRating >= index + 1) Icons.Default.Star
                                    else if (currentRating > index) Icons.Default.StarHalf
                                    else Icons.Default.StarOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Additional Info
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Email: ${student.email}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "ID: ${student.studentId.take(6)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun FeeStatusChip(status: String, canEdit: Boolean, onClick: () -> Unit) {
    val (containerColor, contentColor) = when (status) {
        "Paid" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small,
        onClick = if (canEdit) onClick else null,
        modifier = if (canEdit) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (status == "Paid") Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = contentColor
            )
            Text(
                text = status,
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            if (canEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(12.dp),
                    tint = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}
