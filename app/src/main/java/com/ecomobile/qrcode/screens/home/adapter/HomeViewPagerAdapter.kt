package com.ecomobile.qrcode.screens.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ecomobile.qrcode.screens.home.fragment.food.FoodFragment
import com.ecomobile.qrcode.screens.home.fragment.history.HistoryFragment
import com.ecomobile.qrcode.screens.home.fragment.scan.ScanFragment
import com.ecomobile.qrcode.screens.home.fragment.settings.SettingsFragment
import com.ecomobile.qrcode.screens.home.model.NavItem

class HomeViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            NavItem.SCAN.ordinal -> ScanFragment()
            NavItem.HISTORY.ordinal -> HistoryFragment()
            NavItem.SETTINGS.ordinal -> SettingsFragment()
            else -> FoodFragment()
        }
    }
}