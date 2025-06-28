package com.ecomobile.retrofit.service

import com.ecomobile.retrofit.ebay.EBayProductData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EbayService {
    @GET(
        "services/search/FindingService/v1?"
                + "OPERATION-NAME=findItemsByKeywords"
                + "&SERVICE-VERSION=1.0.0"
                + "&RESPONSE-DATA-FORMAT=JSON"
                + "&REST-PAYLOAD"
    )
    suspend fun fetchProductByKeyword(
        @Query("keywords") keywords: String,
        @Query("SECURITY-APPNAME") appName: String,
    ): Response<EBayProductData>

}