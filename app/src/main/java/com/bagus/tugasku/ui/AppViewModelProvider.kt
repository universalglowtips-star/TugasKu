package com.bagus.tugasku.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bagus.tugasku.TugasKuApp
import com.bagus.tugasku.ui.detail.TaskDetailViewModel
import com.bagus.tugasku.ui.form.TaskFormViewModel
import com.bagus.tugasku.ui.list.TaskListViewModel

/**
 * Factory tunggal untuk semua ViewModel. Dependensi (repository) diambil dari
 * [com.bagus.tugasku.di.AppContainer] milik Application.
 *
 * ViewModel yang berada di dalam NavBackStackEntry otomatis menerima argumen
 * navigasi lewat SavedStateHandle (createSavedStateHandle).
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskListViewModel(tugasKuApp().container.taskRepository)
        }
        initializer {
            TaskFormViewModel(createSavedStateHandle(), tugasKuApp().container.taskRepository)
        }
        initializer {
            TaskDetailViewModel(createSavedStateHandle(), tugasKuApp().container.taskRepository)
        }
    }
}

private fun CreationExtras.tugasKuApp(): TugasKuApp =
    checkNotNull(this[APPLICATION_KEY]) as TugasKuApp
