package com.bagus.tugasku.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskStatus
import com.bagus.tugasku.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TaskListUiState(
    /** Tugas yang sudah melewati filter status + pencarian. */
    val tasks: List<Task> = emptyList(),
    /** Total tugas tanpa filter, untuk membedakan "belum ada tugas" vs "tidak ada yang cocok". */
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
)

class TaskListViewModel(repository: TaskRepository) : ViewModel() {

    // Query & filter dipisah dari uiState agar TextField membaca nilai sinkron (tanpa jeda).
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _statusFilter = MutableStateFlow<TaskStatus?>(null)
    val statusFilter: StateFlow<TaskStatus?> = _statusFilter.asStateFlow()

    /**
     * Sumber kebenaran daftar = Room. Setiap insert/update/delete di database membuat
     * Flow DAO emit ulang -> combine dihitung ulang -> UI ter-update otomatis.
     */
    val uiState: StateFlow<TaskListUiState> =
        combine(repository.observeTasks(), _query, _statusFilter) { all, query, status ->
            val keyword = query.trim()
            val filtered = all.filter { task ->
                (status == null || task.status == status) &&
                    (keyword.isEmpty() ||
                        task.judul.contains(keyword, ignoreCase = true) ||
                        task.mataKuliah.contains(keyword, ignoreCase = true))
            }
            TaskListUiState(tasks = filtered, totalCount = all.size, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = TaskListUiState(),
        )

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onStatusFilterChange(status: TaskStatus?) {
        _statusFilter.value = status
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
