package com.bagus.tugasku.data.repository

import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskDao
import com.bagus.tugasku.data.local.TaskStatus
import kotlinx.coroutines.flow.Flow

/**
 * Satu-satunya pintu akses data bagi ViewModel. UI tidak pernah menyentuh DAO/Room.
 * Dibuat sebagai interface agar mudah diganti fake saat unit test, atau ditambah
 * sumber data remote di masa depan tanpa mengubah ViewModel/UI.
 */
interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeTask(id: Long): Flow<Task?>
    suspend fun getTask(id: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun updateStatus(id: Long, status: TaskStatus)
    suspend fun deleteTask(id: Long)
}

class OfflineTaskRepository(private val dao: TaskDao) : TaskRepository {
    override fun observeTasks(): Flow<List<Task>> = dao.observeAll()
    override fun observeTask(id: Long): Flow<Task?> = dao.observeById(id)
    override suspend fun getTask(id: Long): Task? = dao.getById(id)
    override suspend fun addTask(task: Task): Long = dao.insert(task)
    override suspend fun updateTask(task: Task) = dao.update(task)
    override suspend fun updateStatus(id: Long, status: TaskStatus) = dao.updateStatus(id, status)
    override suspend fun deleteTask(id: Long) = dao.deleteById(id)
}
