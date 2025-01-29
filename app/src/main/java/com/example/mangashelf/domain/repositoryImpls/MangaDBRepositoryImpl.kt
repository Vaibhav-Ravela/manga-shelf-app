package com.example.mangashelf.domain.repositoryImpls

import com.example.mangashelf.data.local.daos.MangaDao
import com.example.mangashelf.data.local.entities.MangaEntity
import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.domain.models.MangaItem
import javax.inject.Inject

class MangaDBRepositoryImpl @Inject constructor(private val mangaDao: MangaDao) :
    MangaDBRepository {
    override suspend fun getAllMangaItems(): List<MangaItem> =
        mangaDao.getAllMangaItems().map {
            MangaItem(
                id = it.id,
                image = it.image,
                score = it.score,
                popularity = it.popularity,
                title = it.title,
                publishedChapterDate = it.publishedChapterDate,
                category = it.category,
                isFavorite = it.isFavorite
            )
        }

    override suspend fun updateAllMangaItems(newMangaList: List<MangaItem>) {
        mangaDao.apply {
            val oldMangaItemsFavoriteStatus = HashMap<String, Boolean>()
            mangaDao.getAllMangaItems().forEach {
                oldMangaItemsFavoriteStatus[it.id] = it.isFavorite
            }
            deleteAllMangaItems()
            insertAllMangaItems(newMangaList.map {
                MangaEntity(
                    id = it.id,
                    image = it.image,
                    score = it.score,
                    popularity = it.popularity,
                    title = it.title,
                    publishedChapterDate = it.publishedChapterDate,
                    category = it.category,
                    isFavorite = oldMangaItemsFavoriteStatus[it.id] ?: false
                )
            })
        }
    }

    override suspend fun updateMangaItemFavorite(id: String, isFavorite: Boolean) {
        mangaDao.updateMangaItemFavorite(id, isFavorite)
    }
}