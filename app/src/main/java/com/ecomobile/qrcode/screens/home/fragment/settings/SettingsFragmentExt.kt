package com.ecomobile.qrcode.screens.home.fragment.settings

import com.ecomobile.base.extension.click

fun SettingsFragment.listener() {
    binding.apply {
        notificationBar.click {

        }
        sound.click {
            viewModel.setSound(!tgSound.isCheck)
        }
        vibrate.click {
            viewModel.setVibrate(!tgVibrate.isCheck)
        }
    }
}
fun SettingsFragment.observerData() {
    viewModel.sound.observe(this) {
        binding.tgSound.setChecked(it)
    }
    viewModel.vibrate.observe(this) {
        binding.tgVibrate.setChecked(it)
    }
}