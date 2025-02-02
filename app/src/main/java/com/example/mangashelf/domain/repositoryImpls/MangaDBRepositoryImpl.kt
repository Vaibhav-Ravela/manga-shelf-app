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
                isFavorite = it.isFavorite,
                isRead = it.isRead
            )
        }

    override suspend fun updateAllMangaItems(newMangaList: MutableList<MangaItem>) : List<MangaItem> {
        mangaDao.apply {
            val oldMangaItemsFavoriteAndReadStatus = HashMap<String, Pair<Boolean, Boolean>>()
            mangaDao.getAllMangaItems().forEach {
                oldMangaItemsFavoriteAndReadStatus[it.id] = Pair(it.isFavorite, it.isRead)
            }
            deleteAllMangaItems()
            val mangaEntityList = mutableListOf<MangaEntity>()
            for (mangaItem in newMangaList) {
                mangaItem.let {
                    it.isFavorite = oldMangaItemsFavoriteAndReadStatus[it.id]?.first ?: false
                    it.isRead = oldMangaItemsFavoriteAndReadStatus[it.id]?.second ?: false
                    mangaEntityList.add((MangaEntity(
                        id = it.id,
                        image = it.image,
                        score = it.score,
                        popularity = it.popularity,
                        title = it.title,
                        publishedChapterDate = it.publishedChapterDate,
                        category = it.category,
                        isFavorite = it.isFavorite,
                        isRead = it.isRead
                    )))
                }
            }
            insertAllMangaItems(mangaEntityList)
            return newMangaList
        }
    }

    override suspend fun updateMangaItemFavorite(id: String, isFavorite: Boolean) {
        mangaDao.updateMangaItemFavorite(id, isFavorite)
    }

    override suspend fun updateMangaItemRead(id: String, isRead: Boolean) {
        mangaDao.updateMangaItemRead(id, isRead)
    }
}