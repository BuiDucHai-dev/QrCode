package com.ecomobile.qrcode.database.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ecomobile.qrcode.database.model.SavedBarcode
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedBarcodeDAO {

    @Query("SELECT * FROM eco_qr_code_saved_code WHERE category = :category")
    fun getSavedBarcodeByCategory(category: Int): Flow<MutableList<SavedBarcode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCode(savedCode: SavedBarcode): Long

    @Query("DELETE FROM eco_qr_code_saved_code WHERE category = :category")
    suspend fun deleteSavedCodeListByCategory(category: Int)

    @Query("SELECT * FROM eco_qr_code_saved_code WHERE id = :savedBarcodeId")
    suspend fun getSavedCodeById(savedBarcodeId: Long): SavedBarcode?

}