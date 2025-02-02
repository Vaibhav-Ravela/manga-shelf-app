package com.example.mangashelf.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mangashelf.data.local.entities.MangaEntity

@Dao
interface MangaDao {
    @Insert
    fun insertAllMangaItems(mangaList: List<MangaEntity>)

    @Query("SELECT * FROM `Manga Items`")
    fun getAllMangaItems(): List<MangaEntity>

    @Query("DELETE FROM `Manga Items`")
    fun deleteAllMangaItems()

    @Query("UPDATE `Manga Items` SET isFavorite = :isFavorite WHERE id = :id")
    fun updateMangaItemFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE `Manga Items` SET isRead = :isRead WHERE id = :id")
    fun updateMangaItemRead(id: String, isRead: Boolean)
}