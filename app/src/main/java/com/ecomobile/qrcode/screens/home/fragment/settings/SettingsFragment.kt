package com.ecomobile.qrcode.screens.home.fragment.settings

import android.os.Bundle
import android.view.View
import com.ecomobile.base.BaseFragment
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.databinding.FragmentSettingsBinding
import com.ecomobile.qrcode.screens.home.fragment.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: BaseFragment<FragmentSettingsBinding>() {


    val viewModel: SettingsViewModel by viewModel()

    override val layoutResId: Int
        get() = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener()
        observerData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUiState()
    }
}