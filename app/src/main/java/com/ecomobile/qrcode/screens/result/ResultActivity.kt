package com.ecomobile.qrcode.screens.result

import com.ecomobile.base.BaseActivity
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.databinding.ActivityResultBinding
import com.ecomobile.qrcode.screens.result.viewmodel.ResultViewModel
import com.ecomobile.qrcode.viewmodel.AppViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultActivity: BaseActivity<ActivityResultBinding>() {

    val appViewModel: AppViewModel by inject()
    val viewModel: ResultViewModel by viewModel()

    override val layoutResId: Int
        get() = R.layout.activity_result

    override fun onCreate() {
        super.onCreate()
        observerData()
    }
}