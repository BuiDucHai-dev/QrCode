package com.ecomobile.qrcode.database.roomdb.viewmodel

import androidx.lifecycle.ViewModel
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.database.roomdb.repository.SavedBarcodeRepository

class RoomDatabaseViewModel(
    private val savedBarcodeRepository: SavedBarcodeRepository
): ViewModel() {

    val scannedSavedCodeList = savedBarcodeRepository.scannedSavedCodeList
    val generatedSavedCodeList = savedBarcodeRepository.generatedSavedCodeList

    fun getSavedBarcodeByCategory(category: Int) = savedBarcodeRepository.getSavedBarcodeByCategory(category)

    suspend fun insertSavedCode(savedCode: SavedBarcode) = savedBarcodeRepository.insertSavedCode(savedCode)

    suspend fun deleteSavedCodeListByCategory(category: Int) = savedBarcodeRepository.deleteSavedCodeListByCategory(category)

    suspend fun getSavedCodeById(savedBarcodeId: Long) = savedBarcodeRepository.getSavedCodeById(savedBarcodeId)
}