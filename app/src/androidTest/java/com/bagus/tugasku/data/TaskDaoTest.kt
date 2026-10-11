package com.bagus.tugasku.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bagus.tugasku.data.local.AppDatabase
import com.bagus.tugasku.data.local.Priority
import com.bagus.tugasku.data.local.Task
import com.bagus.tugasku.data.local.TaskDao
import com.bagus.tugasku.data.local.TaskStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Uji CRUD DAO memakai database in-memory (tidak menyentuh data aplikasi asli). */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.taskDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun sampleTask(judul: String, deadline: Long = 1_000L) = Task(
        mataKuliah = "Pemrograman Aplikasi Bergerak",
        judul = judul,
        deskripsi = "Deskripsi $judul",
        deadline = deadline,
        prioritas = Priority.TINGGI,
    )

    @Test
    fun insert_lalu_observeAll_mengembalikanTugas() = runBlocking<Unit> {
        val id = dao.insert(sampleTask("Laporan Praktikum 1"))

        val list = dao.observeAll().first()

        assertEquals(1, list.size)
        assertEquals(id, list.first().id)
        assertEquals("Laporan Praktikum 1", list.first().judul)
        assertEquals(TaskStatus.BELUM, list.first().status)
    }

    @Test
    fun update_mengubahDataTersimpan() = runBlocking<Unit> {
        val id = dao.insert(sampleTask("Judul Lama"))
        val saved = checkNotNull(dao.getById(id))

        dao.update(saved.copy(judul = "Judul Baru", prioritas = Priority.RENDAH))

        val updated = checkNotNull(dao.getById(id))
        assertEquals("Judul Baru", updated.judul)
        assertEquals(Priority.RENDAH, updated.prioritas)
    }

    @Test
    fun updateStatus_hanyaMengubahStatus() = runBlocking<Unit> {
        val id = dao.insert(sampleTask("Tugas Status"))

        dao.updateStatus(id, TaskStatus.SELESAI)

        val updated = checkNotNull(dao.getById(id))
        assertEquals(TaskStatus.SELESAI, updated.status)
        assertEquals("Tugas Status", updated.judul)
    }

    @Test
    fun deleteById_menghapusTugas() = runBlocking<Unit> {
        val id = dao.insert(sampleTask("Akan Dihapus"))
        assertNotNull(dao.getById(id))

        dao.deleteById(id)

        assertNull(dao.getById(id))
        assertEquals(0, dao.observeAll().first().size)
    }

    @Test
    fun observeAll_terurutBerdasarkanDeadlineTerdekat() = runBlocking<Unit> {
        dao.insert(sampleTask("Deadline Jauh", deadline = 3_000L))
        dao.insert(sampleTask("Deadline Dekat", deadline = 1_000L))
        dao.insert(sampleTask("Deadline Tengah", deadline = 2_000L))

        val judulUrut = dao.observeAll().first().map { it.judul }

        assertEquals(listOf("Deadline Dekat", "Deadline Tengah", "Deadline Jauh"), judulUrut)
    }
}
