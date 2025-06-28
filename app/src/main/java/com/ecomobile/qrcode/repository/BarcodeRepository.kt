package com.ecomobile.qrcode.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ecomobile.base.extension.runDelay
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.google.mlkit.vision.barcode.common.Barcode

class BarcodeRepository(context: Context) {

    private val _barcode: MutableLiveData<Barcode?> = MutableLiveData()
    val barcode: MutableLiveData<Barcode?> get() = _barcode

    private val _savedBarcode: MutableLiveData<SavedBarcode?> = MutableLiveData()
    val savedBarcode: MutableLiveData<SavedBarcode?> get() = _savedBarcode

    fun postBarcode(barcode: Barcode) {
        _barcode.postValue(barcode)
        runDelay(500) {
            _barcode.postValue(null)
        }
    }

    fun postSavedBarcode(barcode: SavedBarcode) {
        _savedBarcode.postValue(barcode)
        runDelay(500) {
            _savedBarcode.postValue(null)
        }
    }

}