package com.ecomobile.qrcode.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eco_qr_code_saved_code")
data class SavedBarcode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id") val id: Long,
    @ColumnInfo(name="display_value") val displayValue: String,
    @ColumnInfo(name="raw_value") val rawValue: String,
    @ColumnInfo(name="type") val type: Int,
    @ColumnInfo(name="format") val format: Int,
    @ColumnInfo(name="path") val path: String = "",
    @ColumnInfo(name="important") var important: Boolean = false,
    @ColumnInfo(name="category") val category: Int = SavedCodeCategory.SCANNED
)

object SavedCodeCategory {
    const val SCANNED = 0
    const val GENERATED = 1
}