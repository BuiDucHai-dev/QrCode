package com.ecomobile.qrcode.database.roomdb

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.database.roomdb.dao.SavedBarcodeDAO

@Database(entities = [SavedBarcode::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun savedBarcodeDao(): SavedBarcodeDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ECO_QR_CODE_DATABASE"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigrationFrom()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}