package com.bagus.tugasku.ui.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagus.tugasku.data.local.Priority
import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskStatus
import com.bagus.tugasku.data.repository.TaskRepository
import com.bagus.tugasku.ui.navigation.NO_TASK_ID
import com.bagus.tugasku.ui.navigation.TASK_ID_ARG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskFormUiState(
    val mataKuliah: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val deadline: Long? = null,
    val prioritas: Priority = Priority.SEDANG,
    val isEdit: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    /** Error validasi baru ditampilkan setelah pengguna menekan Simpan. */
    val showErrors: Boolean = false,
) {
    val mataKuliahError: Boolean get() = showErrors && mataKuliah.isBlank()
    val judulError: Boolean get() = showErrors && judul.isBlank()
    val deadlineError: Boolean get() = showErrors && deadline == null
    val isValid: Boolean get() = mataKuliah.isNotBlank() && judul.isNotBlank() && deadline != null
}

/**
 * Dipakai bersama oleh layar Tambah dan Edit. Mode ditentukan oleh argumen navigasi
 * `taskId` yang dibaca dari [SavedStateHandle].
 */
class TaskFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
) : ViewModel() {

    private val taskId: Long? =
        savedStateHandle.get<Long>(TASK_ID_ARG)?.takeIf { it != NO_TASK_ID }

    /** Tugas asli saat mode Edit; dipakai untuk mempertahankan status yang tersimpan. */
    private var original: Task? = null

    private val _uiState = MutableStateFlow(
        TaskFormUiState(isEdit = taskId != null, isLoading = taskId != null),
    )
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    // Event sekali-jalan (tutup layar) lewat Channel agar tidak terkirim ulang saat rotasi.
    private val _closeScreen = Channel<Unit>(Channel.BUFFERED)
    val closeScreen: Flow<Unit> = _closeScreen.receiveAsFlow()

    init {
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            val task = repository.getTask(id)
            if (task == null) {
                _closeScreen.send(Unit) // tugas sudah tidak ada
                return@launch
            }
            original = task
            _uiState.update {
                it.copy(
                    mataKuliah = task.mataKuliah,
                    judul = task.judul,
                    deskripsi = task.deskripsi,
                    deadline = task.deadline,
                    prioritas = task.prioritas,
                    isLoading = false,
                )
            }
        }
    }

    fun onMataKuliahChange(value: String) = _uiState.update { it.copy(mataKuliah = value) }
    fun onJudulChange(value: String) = _uiState.update { it.copy(judul = value) }
    fun onDeskripsiChange(value: String) = _uiState.update { it.copy(deskripsi = value) }
    fun onDeadlineChange(value: Long) = _uiState.update { it.copy(deadline = value) }
    fun onPrioritasChange(value: Priority) = _uiState.update { it.copy(prioritas = value) }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        if (!state.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val task = Task(
                id = taskId ?: 0L,
                mataKuliah = state.mataKuliah.trim(),
                judul = state.judul.trim(),
                deskripsi = state.deskripsi.trim(),
                deadline = checkNotNull(state.deadline),
                prioritas = state.prioritas,
                status = original?.status ?: TaskStatus.BELUM,
            )
            if (taskId == null) repository.addTask(task) else repository.updateTask(task)
            _closeScreen.send(Unit)
        }
    }
}
