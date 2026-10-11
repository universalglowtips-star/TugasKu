package com.bagus.tugasku.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskStatus
import com.bagus.tugasku.ui.AppViewModelProvider
import com.bagus.tugasku.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    onNavigateBack: () -> Unit,
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val currentOnBack by rememberUpdatedState(onNavigateBack)
    LaunchedEffect(viewModel) {
        viewModel.deleted.collect { currentOnBack() }
    }

    val task: Task? = (state as? TaskDetailUiState.Success)?.task

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detail Tugas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (task != null) {
                        IconButton(onClick = { onEditTask(task.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit tugas")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus tugas")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val current = state) {
            TaskDetailUiState.Loading -> Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            TaskDetailUiState.NotFound -> Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text("Tugas tidak ditemukan.") }

            is TaskDetailUiState.Success -> TaskDetailBody(
                task = current.task,
                onStatusChange = viewModel::onStatusChange,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showDeleteDialog && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus tugas?") },
            text = { Text("Tugas \"${task.judul}\" akan dihapus permanen dan tidak bisa dikembalikan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.delete()
                    },
                ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun TaskDetailBody(
    task: Task,
    onStatusChange: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysLeft = DateUtils.daysUntil(task.deadline)
    val deadlineText = buildString {
        append(DateUtils.formatDate(task.deadline))
        if (task.status != TaskStatus.SELESAI) append(" · ").append(DateUtils.deadlineLabel(daysLeft))
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = task.mataKuliah,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(text = task.judul, style = MaterialTheme.typography.headlineSmall)
        }

        InfoRow(label = "Deadline", value = deadlineText)
        InfoRow(label = "Prioritas", value = task.prioritas.label)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Deskripsi", style = MaterialTheme.typography.labelLarge)
            Text(
                text = task.deskripsi.ifBlank { "Tidak ada deskripsi." },
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Status", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskStatus.entries.forEach { status ->
                    FilterChip(
                        selected = task.status == status,
                        onClick = { onStatusChange(status) },
                        label = { Text(status.label) },
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
