package com.ecomobile.qrcode.screens.home

import com.ecomobile.base.BaseActivity
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.databinding.ActivityHomeBinding
import com.ecomobile.qrcode.screens.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity: BaseActivity<ActivityHomeBinding>() {

    val viewModel: HomeViewModel by viewModel()

    override val layoutResId: Int
        get() = R.layout.activity_home

    override fun allowPadding(): Boolean {
        return false
    }

    override fun onCreate() {
        super.onCreate()
        setupViewpager()
        listener()
        observerData()
    }
}