package com.ecomobile.qrcode.koin

import com.ecomobile.qrcode.database.roomdb.AppDatabase
import com.ecomobile.qrcode.database.roomdb.repository.SavedBarcodeRepository
import com.ecomobile.qrcode.database.roomdb.viewmodel.RoomDatabaseViewModel
import com.ecomobile.qrcode.repository.BarcodeRepository
import com.ecomobile.qrcode.screens.home.fragment.scan.repository.ScanRepository
import com.ecomobile.qrcode.screens.home.fragment.scan.viewmodel.ScanViewModel
import com.ecomobile.qrcode.screens.home.fragment.settings.repository.SettingsRepository
import com.ecomobile.qrcode.screens.home.fragment.settings.viewmodel.SettingsViewModel
import com.ecomobile.qrcode.screens.home.repository.HomeRepository
import com.ecomobile.qrcode.screens.home.viewmodel.HomeViewModel
import com.ecomobile.qrcode.screens.result.repository.ResultRepository
import com.ecomobile.qrcode.screens.result.viewmodel.ResultViewModel
import com.ecomobile.qrcode.viewmodel.AppViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().savedBarcodeDao() }
    single { BarcodeRepository(get()) }
    single { SavedBarcodeRepository(get()) }
    single { HomeRepository(get()) }
    single { ScanRepository(get()) }
    single { SettingsRepository(get()) }
    single { ResultRepository(get()) }
}

val viewModelModule = module {
    viewModel { RoomDatabaseViewModel(get()) }
    viewModel { AppViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { ResultViewModel(get()) }
}