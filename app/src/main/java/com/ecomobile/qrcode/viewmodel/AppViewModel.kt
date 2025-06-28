package com.ecomobile.qrcode.viewmodel

import androidx.lifecycle.ViewModel
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.repository.BarcodeRepository
import com.google.mlkit.vision.barcode.common.Barcode

class AppViewModel(
    private val barcodeRepository: BarcodeRepository
): ViewModel() {

    val barcode = barcodeRepository.barcode
    val savedBarcode = barcodeRepository.savedBarcode

    fun postBarcode(barcode: Barcode) = barcodeRepository.postBarcode(barcode)

    fun postSavedBarcode(barcode: SavedBarcode) = barcodeRepository.postSavedBarcode(barcode)
}