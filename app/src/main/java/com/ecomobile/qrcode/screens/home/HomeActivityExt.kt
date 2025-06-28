package com.ecomobile.qrcode.screens.home

import com.ecomobile.base.extension.click
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.screens.home.adapter.HomeViewPagerAdapter
import com.ecomobile.qrcode.screens.home.model.NavItem

fun HomeActivity.setupViewpager() {
    binding.viewpager.apply {
        isUserInputEnabled = false
        adapter = HomeViewPagerAdapter(this@setupViewpager)
        offscreenPageLimit = 2
    }
}

fun HomeActivity.listener() {
    binding.apply {
        foodFact.click {
            viewModel.changeTab(NavItem.FOOD)
        }
        myQr.click {
            viewModel.changeTab(NavItem.MY_QR)
        }
        scan.click {
            viewModel.changeTab(NavItem.SCAN)
        }
        history.click {
            viewModel.changeTab(NavItem.HISTORY)
        }
        settings.click {
            viewModel.changeTab(NavItem.SETTINGS)
        }
    }
}

fun HomeActivity.observerData() {
    viewModel.navItem.observe(this) {
        changeTab(it)
    }
}

fun HomeActivity.changeTab(navItem: NavItem) {
    binding.apply {
        imgFoodFact.setImageResource(R.drawable.ic_home_food_line)
        imgCreate.setImageResource(R.drawable.ic_home_create_line)
        imgScan.setImageResource(R.drawable.ic_home_scan_line)
        imgHistory.setImageResource(R.drawable.ic_home_history_line)
        imgSettings.setImageResource(R.drawable.ic_home_settings_line)
        when(navItem) {
            NavItem.FOOD -> imgFoodFact.setImageResource(R.drawable.ic_home_food_fill)
            NavItem.MY_QR -> imgCreate.setImageResource(R.drawable.ic_home_create_fill)
            NavItem.SCAN -> imgScan.setImageResource(R.drawable.ic_home_scan_fill)
            NavItem.HISTORY -> imgHistory.setImageResource(R.drawable.ic_home_history_fill)
            NavItem.SETTINGS -> imgSettings.setImageResource(R.drawable.ic_home_settings_fill)
        }
        viewpager.setCurrentItem(navItem.ordinal, false)
    }
}