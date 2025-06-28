package com.ecomobile.qrcode.screens.home.fragment.history

import android.os.Bundle
import android.view.View
import com.ecomobile.base.BaseFragment
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.database.roomdb.viewmodel.RoomDatabaseViewModel
import com.ecomobile.qrcode.databinding.FragmentHistoryBinding
import com.ecomobile.qrcode.screens.home.fragment.history.adapter.ScanHistoryAdapter
import com.ecomobile.qrcode.viewmodel.AppViewModel
import org.koin.android.ext.android.inject

class HistoryFragment: BaseFragment<FragmentHistoryBinding>() {

    val appViewModel: AppViewModel by inject()
    val db: RoomDatabaseViewModel by inject()

    val scanHistoryAdapter = ScanHistoryAdapter().apply {
        onItemClick = {
            appViewModel.postSavedBarcode(it)
        }
    }

    override val layoutResId: Int
        get() = R.layout.fragment_history

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observerData()
    }
}