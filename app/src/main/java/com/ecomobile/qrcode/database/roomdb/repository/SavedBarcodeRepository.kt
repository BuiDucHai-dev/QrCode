package com.ecomobile.qrcode.database.roomdb.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.database.model.SavedCodeCategory
import com.ecomobile.qrcode.database.roomdb.dao.SavedBarcodeDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedBarcodeRepository(
    private val dao: SavedBarcodeDAO
) {

    private val _scannedSavedCodeList = MutableLiveData<List<SavedBarcode>>()
    val scannedSavedCodeList: LiveData<List<SavedBarcode>> = _scannedSavedCodeList

    private val _generatedSavedCodeList = MutableLiveData<List<SavedBarcode>>()
    val generatedSavedCodeList: LiveData<List<SavedBarcode>> = _generatedSavedCodeList

    fun getSavedBarcodeByCategory(category: Int) = CoroutineScope(Dispatchers.IO).launch {
        dao.getSavedBarcodeByCategory(category).collect {
            when (category) {
                SavedCodeCategory.SCANNED -> {
                    _scannedSavedCodeList.postValue(it)
                }
                SavedCodeCategory.GENERATED -> {
                    _generatedSavedCodeList.postValue(it)
                }
            }
        }
    }

    suspend fun insertSavedCode(savedCode: SavedBarcode) = dao.insertSavedCode(savedCode)

    suspend fun deleteSavedCodeListByCategory(category: Int) = dao.deleteSavedCodeListByCategory(category)

    suspend fun getSavedCodeById(savedBarcodeId: Long) = dao.getSavedCodeById(savedBarcodeId)
}