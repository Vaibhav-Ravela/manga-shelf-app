package com.example.mangashelf.di

import android.content.Context
import androidx.room.Room
import com.example.mangashelf.data.local.daos.MangaDao
import com.example.mangashelf.data.local.databases.MangaDatabase
import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.domain.repositoryImpls.MangaDBRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {
    @Provides
    @Singleton
    fun provideMangaDatabase(@ApplicationContext context: Context): MangaDatabase =
        Room.databaseBuilder(
            context,
            MangaDatabase::class.java,
            "manga database"
        ).build()

    @Provides
    @Singleton
    fun provideMangaDao(mangaDatabase: MangaDatabase): MangaDao = mangaDatabase.getMangaDao()

    @Provides
    @Singleton
    fun provideMangaDBRepository(mangaDBRepository: MangaDBRepositoryImpl): MangaDBRepository = mangaDBRepository
}