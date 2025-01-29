package com.example.mangashelf.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Manga Items")
data class MangaEntity(
    @PrimaryKey val id: String,
    val image: String,
    val score: Float,
    val popularity: Long,
    val title: String,
    val publishedChapterDate: Long,
    val category: String,
    val isFavorite: Boolean
)
