package com.ecomobile.qrcode.screens.result.viewmodel

import androidx.lifecycle.ViewModel
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.screens.result.repository.ResultRepository
import com.google.mlkit.vision.barcode.common.Barcode

class ResultViewModel(
    private val repository: ResultRepository,
): ViewModel() {

    val barcodeContentString = repository.barcodeContentString
    val isNeedToUpdateSizeText = repository.isNeedToUpdateSizeText
    val openGraphResult = repository.openGraphResult

    fun convertBarcodeContentToString(barcode: Barcode) = repository.convertBarcodeContentToString(barcode)
//
    fun convertSavedBarcodeContentToString(savedBarcode: SavedBarcode) = repository.convertSavedBarcodeContentToString(savedBarcode)

    fun parseWebPreview() = repository.parseWebPreview()
}