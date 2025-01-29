package com.example.mangashelf.data.local.repositories

import com.example.mangashelf.domain.models.MangaItem

interface MangaDBRepository {
    suspend fun getAllMangaItems(): List<MangaItem>
    suspend fun updateAllMangaItems(newMangaList: List<MangaItem>)
    suspend fun updateMangaItemFavorite(id: String, isFavorite: Boolean)
}