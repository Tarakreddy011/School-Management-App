package com.example.test.ui

import androidx.compose.foundation.text.KeyboardOptions


import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.test.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherManagementScreen(
    principalViewModel: PrincipalViewModel,
    teacherViewModel: TeacherViewModel,
    userRole: String
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("All") }
    val teachers by teacherViewModel.teachers
    val loading by teacherViewModel.loading

    val roles = listOf("All", "hm", "teacher", "incharge", "trio")

    LaunchedEffect(selectedRole, searchQuery) {
        teacherViewModel.fetchTeachers()
    }

    val hasHM = teachers.any { it.role == "hm" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Staff Management",
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
                                contentDescription = "Add Staff",
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
                        contentDescription = "Add Staff",
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
                        title = "Total Staff",
                        value = teachers.size.toString(),
                        icon = Icons.Default.Group
                    )
                    StatItem(
                        title = "Teachers",
                        value = teachers.count { it.role == "teacher" }.toString(),
                        icon = Icons.Default.Person
                    )
                    StatItem(
                        title = "HM",
                        value = if (hasHM) "1" else "0",
                        icon = Icons.Default.AdminPanelSettings
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

                    // Role Filter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roles.forEach { role ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                label = { Text(role.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by name or subject") },
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
            } else {
                // Filter teachers
                val filteredTeachers = teachers.filter { teacher ->
                    val matchesRole = selectedRole == "All" || teacher.role == selectedRole
                    val matchesSearch = teacher.name.contains(searchQuery, ignoreCase = true) ||
                            teacher.subject.contains(searchQuery, ignoreCase = true)
                    matchesRole && matchesSearch
                }

                if (filteredTeachers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No staff members found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (selectedRole != "All") "No ${selectedRole}s found"
                                else "Add staff members to get started",
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
                        items(filteredTeachers) { teacher ->
                            TeacherCard(
                                teacher = teacher,
                                canDelete = userRole == "principal",
                                onDelete = {
                                    teacherViewModel.deleteTeacher(teacher.teacherId) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Staff deleted", Toast.LENGTH_SHORT).show()
                                            teacherViewModel.fetchTeachers()
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

    if (showAddDialog) {
        AddStaffDialog(
            hasHM = hasHM,
            onDismiss = { showAddDialog = false },
            onAdd = { staff ->
                principalViewModel.addStaff(staff) { success, addedUser ->
                    if (success && addedUser != null) {
                        Toast.makeText(context, "Staff added successfully", Toast.LENGTH_LONG).show()
                        showAddDialog = false
                        teacherViewModel.fetchTeachers()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to add staff. Email might already exist.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }
}

@Composable
fun TeacherCard(
    teacher: com.example.test.model.Teacher,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (teacher.role) {
                "hm" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                "teacher" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                "incharge" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
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
                        text = teacher.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoleChip(teacher.role)
                        if (teacher.role == "teacher") {
                            Text(
                                text = teacher.subject,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                if (canDelete) {
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

            Spacer(modifier = Modifier.height(12.dp))

            // Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Contact Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Email: ${teacher.email}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Phone: ${teacher.phone ?: "Not provided"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Pass: ${teacher.password}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Teacher-specific details
                    if (teacher.role == "teacher") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Assigned Classes:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = teacher.classesAssigned.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleChip(role: String) {
    val (containerColor, contentColor) = when (role) {
        "hm" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "teacher" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "incharge" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = role.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// AddStaffDialog remains similar but with improved UI
// You can keep the existing logic but add Material3 styling

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffDialog(hasHM: Boolean, onDismiss: () -> Unit, onAdd: (User) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(if (hasHM) "teacher" else "hm") }

    // Teacher specific fields
    var subject by remember { mutableStateOf("") }
    var assignedClasses by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Staff Member", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                )

                // Role Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Select Role",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (!hasHM) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = role == "hm",
                                        onClick = { role = "hm" }
                                    )
                                    Text(
                                        "Head Master",
                                        modifier = Modifier.clickable { role = "hm" }
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = role == "teacher",
                                    onClick = { role = "teacher" }
                                )
                                Text(
                                    "Teacher",
                                    modifier = Modifier.clickable { role = "teacher" }
                                )
                            }
                        }
                    }
                }

                // Teacher-specific fields
                if (role == "teacher") {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject (e.g., Maths, Science)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = assignedClasses,
                        onValueChange = { assignedClasses = it },
                        label = { Text("Assigned Classes (comma-separated, e.g., 6,7,8,9,10)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        val classesList = assignedClasses.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        onAdd(User(
                            name = name,
                            email = email,
                            password = password,
                            phone = phone,
                            role = role,
                            subject = if (role == "teacher") subject else "",
                            classesAssigned = if (role == "teacher") classesList else emptyList()
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
            ) {
                Text("Create Staff Account")
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