package com.example.mangashelf.di

import com.example.mangashelf.data.remote.aPIServices.JsonKeeperAPIService
import com.example.mangashelf.data.remote.interceptors.RetryInterceptor
import com.example.mangashelf.data.remote.repositories.JsonKeeperRepository
import com.example.mangashelf.domain.repositoryImpls.JsonKeeperRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor = RetryInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClientBuilder(interceptor: Interceptor): OkHttpClient.Builder =
        OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClientBuilder: OkHttpClient.Builder): Retrofit.Builder =
        Retrofit.Builder().baseUrl("https://www.jsonkeeper.com").client(okHttpClientBuilder.build())
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