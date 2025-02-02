package com.example.mangashelf.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MangaItem(
    val id: String,
    val image: String,
    val score: Float,
    val popularity: Long,
    val title: String,
    val publishedChapterDate: Long,
    val category: String,
    var isFavorite: Boolean = false,
    var isRead: Boolean = false
) : Parcelable
