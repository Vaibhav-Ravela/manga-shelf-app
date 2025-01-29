package com.example.mangashelf.domain.repositoryImpls

import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.data.remote.aPIServices.JsonKeeperAPIService
import com.example.mangashelf.data.remote.repositories.JsonKeeperRepository
import com.example.mangashelf.domain.models.MangaItem
import javax.inject.Inject

class JsonKeeperRepositoryImpl @Inject constructor(
    private val jsonKeeperAPIService: JsonKeeperAPIService,
    private val mangaDBRepository: MangaDBRepository
) : JsonKeeperRepository {
    override suspend fun getMangaList(): List<MangaItem> {
        val remoteMangaList = jsonKeeperAPIService.getMangaList()

        mangaDBRepository.updateAllMangaItems(remoteMangaList)
        return remoteMangaList
    }
}