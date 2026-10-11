package com.bagus.tugasku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tingkat prioritas tugas. Disimpan di Room sebagai TEXT (nama enum). */
enum class Priority(val label: String) {
    RENDAH("Rendah"),
    SEDANG("Sedang"),
    TINGGI("Tinggi"),
}

/** Status pengerjaan tugas. Disimpan di Room sebagai TEXT (nama enum). */
enum class TaskStatus(val label: String) {
    BELUM("Belum"),
    PROSES("Proses"),
    SELESAI("Selesai"),
}

/**
 * Model data minimal sesuai studi kasus:
 * id, mataKuliah, judul, deskripsi, deadline, prioritas, status.
 *
 * [deadline] disimpan sebagai epoch millis pada tengah malam UTC dari tanggal
 * yang dipilih (nilai yang dikembalikan Material3 DatePicker), sehingga
 * pemformatan dan perhitungan sisa hari konsisten di semua zona waktu.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val mataKuliah: String,
    val judul: String,
    val deskripsi: String = "",
    val deadline: Long,
    val prioritas: Priority = Priority.SEDANG,
    val status: TaskStatus = TaskStatus.BELUM,
)
