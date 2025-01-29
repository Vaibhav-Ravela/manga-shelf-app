package com.example.mangashelf.data.remote.repositories

import com.example.mangashelf.domain.models.MangaItem

interface JsonKeeperRepository {
    suspend fun getMangaList(): List<MangaItem>
}