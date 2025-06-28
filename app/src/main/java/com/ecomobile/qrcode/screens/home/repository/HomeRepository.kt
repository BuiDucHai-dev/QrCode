package com.ecomobile.qrcode.screens.home.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ecomobile.qrcode.screens.home.model.NavItem

class HomeRepository(context: Context) {

    private val _navItem = MutableLiveData(NavItem.SCAN)
    val navItem: LiveData<NavItem> get() = _navItem

    fun changeTab(navItem: NavItem) {
        _navItem.postValue(navItem)
    }
}