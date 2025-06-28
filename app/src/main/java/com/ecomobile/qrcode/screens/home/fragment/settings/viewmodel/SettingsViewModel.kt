package com.ecomobile.qrcode.screens.home.fragment.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.ecomobile.qrcode.screens.home.fragment.settings.repository.SettingsRepository

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {

    val sound = repository.sound
    val vibrate = repository.vibrate

    fun updateUiState() {
        repository.updateUiState()
    }

    fun setSound(value: Boolean) {
        repository.setSound(value)
    }

    fun setVibrate(value: Boolean) {
        repository.setVibrate(value)
    }

}