package com.ecomobile.qrcode.screens.home.fragment.history

import android.content.Intent
import android.util.Log
import com.ecomobile.qrcode.database.model.SavedCodeCategory
import com.ecomobile.qrcode.screens.result.ResultActivity

fun HistoryFragment.init() {
    db.getSavedBarcodeByCategory(SavedCodeCategory.SCANNED)
    binding.rcvScanHistory.adapter = scanHistoryAdapter
}

fun HistoryFragment.observerData() {
    appViewModel.savedBarcode.observe(viewLifecycleOwner) {
        it?.let {
            navigateToResultActivity()
        }
    }
    db.scannedSavedCodeList.observe(viewLifecycleOwner) {
        scanHistoryAdapter.update(it)
    }
}

fun HistoryFragment.navigateToResultActivity() {
    if (isAdded) {
        startActivity(
            Intent(requireContext(), ResultActivity::class.java)
        )
    }
}