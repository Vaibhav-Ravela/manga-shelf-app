package com.example.mangashelf.di

import com.example.mangashelf.data.remote.aPIServices.JsonKeeperAPIService
import com.example.mangashelf.data.remote.repositories.JsonKeeperRepository
import com.example.mangashelf.domain.repositoryImpls.JsonKeeperRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {
    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl("https://www.jsonkeeper.com")
        .addConverterFactory(GsonConverterFactory.create())

    @Provides
    @Singleton
    fun provideJsonKeeperAPIService(retrofitBuilder: Retrofit.Builder): JsonKeeperAPIService =
        retrofitBuilder.build().create(JsonKeeperAPIService::class.java)

    @Provides
    @Singleton
    fun provideJsonKeeperRepository(jsonKeeperRepository: JsonKeeperRepositoryImpl): JsonKeeperRepository =
        jsonKeeperRepository
}