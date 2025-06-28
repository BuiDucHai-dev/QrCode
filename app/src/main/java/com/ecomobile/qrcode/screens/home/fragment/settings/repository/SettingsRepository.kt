package com.ecomobile.qrcode.screens.home.fragment.settings.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ecomobile.base.storage_data.AppStates

class SettingsRepository(
    val context: Context
) {

    private val _sound = MutableLiveData(true)
    val sound: MutableLiveData<Boolean> get() = _sound

    private val _vibrate = MutableLiveData(true)
    val vibrate: MutableLiveData<Boolean> get() = _vibrate

    fun updateUiState() {
        _sound.postValue(AppStates.scanWithSound)
        _vibrate.postValue(AppStates.scanWithVibrate)
    }

    fun setSound(value: Boolean) {
        AppStates.scanWithSound = value
        _sound.postValue(value)
    }

    fun setVibrate(value: Boolean) {
        AppStates.scanWithVibrate = value
        _vibrate.postValue(value)
    }
}