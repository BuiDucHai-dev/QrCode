package com.ecomobile.retrofit.ebay

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class EBayProductData(
    @SerializedName("findItemsByKeywordsResponse")
    val result: JsonArray
)
