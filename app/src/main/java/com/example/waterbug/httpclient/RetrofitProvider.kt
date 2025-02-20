package com.example.waterbug.httpclient

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitProvider {

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var baseUrl: String = ""

    fun getRetrofitInstance(newBaseUrl: String): Retrofit {
        if (retrofit == null || baseUrl != newBaseUrl) {
            synchronized(this) {
                if (retrofit == null || baseUrl != newBaseUrl) {
                    baseUrl = newBaseUrl

                    // Create a logging interceptor
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }

                    val client = OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor) // Add the interceptor
                        .build()

                    retrofit = Retrofit.Builder()
                        .baseUrl(newBaseUrl)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .client(client)
                        .build()
                }
            }
        }
        return retrofit!!
    }
}