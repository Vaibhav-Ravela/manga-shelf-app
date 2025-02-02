package com.example.mangashelf.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetryCount: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response
        var lastException: IOException? = null

        while (attempt < maxRetryCount) {
            try {
                response = chain.proceed(chain.request())
                if (!response.isSuccessful) {
                    throw IOException("Unexpected response code: ${response.code}")
                }
                return response
            } catch (e: IOException) {
                lastException = e
                attempt++
                if (attempt >= maxRetryCount) {
                    throw e
                }
            }
        }
        throw lastException ?: IOException("Unknown error during retry")
    }
}