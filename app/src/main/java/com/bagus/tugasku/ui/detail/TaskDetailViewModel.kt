package com.bagus.tugasku.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskStatus
import com.bagus.tugasku.data.repository.TaskRepository
import com.bagus.tugasku.ui.navigation.TASK_ID_ARG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState
    data object NotFound : TaskDetailUiState
    data class Success(val task: Task) : TaskDetailUiState
}

class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle.get<Long>(TASK_ID_ARG))

    /** Mengamati satu tugas: perubahan status/edit langsung tercermin di layar detail. */
    val uiState: StateFlow<TaskDetailUiState> = repository.observeTask(taskId)
        .map { task -> if (task == null) TaskDetailUiState.NotFound else TaskDetailUiState.Success(task) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = TaskDetailUiState.Loading,
        )

    private val _deleted = Channel<Unit>(Channel.BUFFERED)
    val deleted: Flow<Unit> = _deleted.receiveAsFlow()

    fun onStatusChange(status: TaskStatus) {
        viewModelScope.launch { repository.updateStatus(taskId, status) }
    }

    fun delete() {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _deleted.send(Unit)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
