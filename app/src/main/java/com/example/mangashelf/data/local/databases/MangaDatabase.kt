package com.example.mangashelf.data.local.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mangashelf.data.local.daos.MangaDao
import com.example.mangashelf.data.local.entities.MangaEntity

@Database(entities = [MangaEntity::class], version = 1)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun getMangaDao(): MangaDao
}