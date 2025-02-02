package com.example.mangashelf.data.local.repositories

import com.example.mangashelf.domain.models.MangaItem

interface MangaDBRepository {
    suspend fun getAllMangaItems(): List<MangaItem>
    suspend fun updateAllMangaItems(newMangaList: MutableList<MangaItem>) : List<MangaItem>
    suspend fun updateMangaItemFavorite(id: String, isFavorite: Boolean)
    suspend fun updateMangaItemRead(id: String, isRead: Boolean)
}