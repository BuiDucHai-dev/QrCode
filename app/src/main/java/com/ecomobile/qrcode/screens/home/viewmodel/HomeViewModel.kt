package com.ecomobile.qrcode.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import com.ecomobile.qrcode.screens.home.model.NavItem
import com.ecomobile.qrcode.screens.home.repository.HomeRepository

class HomeViewModel(
    private val repository: HomeRepository
): ViewModel() {

    val navItem = repository.navItem

    fun changeTab(navItem: NavItem) {
        repository.changeTab(navItem)
    }
}