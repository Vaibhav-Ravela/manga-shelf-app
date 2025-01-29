package com.example.mangashelf.domain.models

data class MangaItem(
    val id: String,
    val image: String,
    val score: Float,
    val popularity: Long,
    val title: String,
    val publishedChapterDate: Long,
    val category: String,
    var isFavorite: Boolean = false
)
