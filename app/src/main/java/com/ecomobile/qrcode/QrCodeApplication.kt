package com.ecomobile.qrcode

import android.util.Log
import com.ecomobile.base.BaseApplication
import com.ecomobile.retrofit.RetrofitClient
import com.ecomobile.retrofit.decode.Encryption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QrCodeApplication: BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            val result = RetrofitClient
                .getEbayService()
                .fetchProductByKeyword(
                    "Dyson TP07",
                    Encryption().decrypt("uTKiq75C8wOqd5Di8YoixX0kkcgLLigi2BRjklFjZMXrEEws5MIdOh6o2EtcpIx+")
                )
            Log.e("HAI", "onCreate: $result")
        }
    }
}