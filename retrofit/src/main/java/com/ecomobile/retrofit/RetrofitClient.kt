package com.ecomobile.retrofit

import com.ecomobile.retrofit.service.EbayService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val EBAY_BASE_URL = "https://svcs.ebay.com/"
    private const val GOOGLE_BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes/"
    private const val FOOD_FACT_BASE_URL = "https://world.openfoodfacts.net/api/v2/product/"

    private fun getClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(15000, TimeUnit.MILLISECONDS)
            .build()
    }

    fun getEbayService(): EbayService {
        return Retrofit.Builder()
            .baseUrl(EBAY_BASE_URL)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EbayService::class.java)
    }

}