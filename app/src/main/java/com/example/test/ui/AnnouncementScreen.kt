package com.example.test.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.test.model.Announcement
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    viewModel: AnnouncementViewModel,
    userRole: String,
    userEmail: String
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("all") }
    val announcements by viewModel.announcements
    val loading by viewModel.loading

    val filters = listOf(
        "all" to "All",
        "trio" to "Trio Only",
        "general" to "General"
    )

    LaunchedEffect(selectedFilter) {
        viewModel.fetchAnnouncements(if (selectedFilter == "all") null else selectedFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Announcements & Diary",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (userRole == "principal" || userRole == "trio" || userRole == "hm") {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Announcement",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == "principal" || userRole == "trio" || userRole == "hm") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Announcement",
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
            // Filter Chips
            if (userRole == "principal" || userRole == "hm") {
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
            } else if (announcements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No announcements yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Check back later for updates",
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
                    items(announcements) { announcement ->
                        AnnouncementItem(announcement, userRole)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAnnouncementDialog(
            userRole = userRole,
            onDismiss = { showAddDialog = false },
            onPost = { title, message, target ->
                val announcement = Announcement(
                    title = title,
                    message = message,
                    target = target,
                    createdBy = userEmail
                )
                viewModel.postAnnouncement(announcement) {
                    showAddDialog = false
                    viewModel.fetchAnnouncements(if (selectedFilter == "all") null else selectedFilter)
                }
            }
        )
    }
}

@Composable
fun AnnouncementItem(announcement: Announcement, userRole: String) {
    val date = Date(announcement.timestamp)
    val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (announcement.target) {
                "trio" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "By: ${announcement.createdBy}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    color = when (announcement.target) {
                        "trio" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = announcement.target.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (announcement.target) {
                            "trio" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Message
            Text(
                text = announcement.message,
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
                    text = format.format(date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (userRole == "principal" || userRole == "hm") {
                    Text(
                        text = "ID: ${announcement.announceId.take(6)}",
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
fun AddAnnouncementDialog(
    userRole: String,
    onDismiss: () -> Unit,
    onPost: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var target by remember { mutableStateOf(if (userRole == "trio") "trio" else "all") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (userRole == "trio") "New Diary Entry" else "New Announcement",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    shape = MaterialTheme.shapes.medium
                )

                if (userRole == "principal" || userRole == "hm") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Target Audience",
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
                                        selected = target == "all",
                                        onClick = { target = "all" }
                                    )
                                    Text(
                                        text = "All Users",
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = target == "trio",
                                        onClick = { target = "trio" }
                                    )
                                    Text(
                                        text = "Trio Only",
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onPost(title, message, target)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && message.isNotBlank()
            ) {
                Text("Post Announcement")
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
