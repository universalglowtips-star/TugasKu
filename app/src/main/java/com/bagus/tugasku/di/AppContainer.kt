package com.bagus.tugasku.di

import android.content.Context
import com.bagus.tugasku.data.local.AppDatabase
import com.bagus.tugasku.data.repository.OfflineTaskRepository
import com.bagus.tugasku.data.repository.TaskRepository

/**
 * Manual dependency injection (cukup untuk skala praktikum ini).
 * Untuk aplikasi yang lebih besar, ganti dengan Hilt/Koin.
 */
class AppContainer(private val context: Context) {

    private val database: AppDatabase by lazy { AppDatabase.build(context) }

    val taskRepository: TaskRepository by lazy { OfflineTaskRepository(database.taskDao()) }
}
