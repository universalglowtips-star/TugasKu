package com.bagus.tugasku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        const val NAME = "tugasku.db"

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NAME)
                // Sengaja TANPA fallbackToDestructiveMigration(): data pengguna tidak boleh
                // hilang diam-diam. Saat skema berubah (version 2), tulis Migration eksplisit.
                .build()
    }
}
