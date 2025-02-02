package com.example.mangashelf.data.remote.aPIServices

import com.example.mangashelf.domain.models.MangaItem
import retrofit2.http.GET

interface JsonKeeperAPIService {
    @GET("/b/KEJO")
    suspend fun getMangaList(): MutableList<MangaItem>
}