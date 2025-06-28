package com.ecomobile.retrofit.service

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBookService {

    @GET(" ")
    suspend fun fetchBook(
        @Query("q") query: String,
        @Query("key") key: String
    )
}