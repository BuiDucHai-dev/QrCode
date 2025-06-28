package com.ecomobile.qrcode.screens.home.fragment.scan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.ecomobile.base.BaseActivity
import com.ecomobile.base.extension.click
import com.ecomobile.base.extension.launchWithTransition
import com.ecomobile.base.extension.runOnBackgroundThread
import com.ecomobile.base.extension.setMargin
import com.ecomobile.base.storage_data.AppStates
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.database.model.SavedCodeCategory
import com.ecomobile.qrcode.screens.home.fragment.scan.model.ScanningResultListener
import com.ecomobile.qrcode.screens.result.ResultActivity
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun ScanFragment.init() {
    viewModel.checkCameraPermission(requireActivity() as AppCompatActivity)
}

fun ScanFragment.listener() {
    binding.apply {
        layoutPermission.btnAllow.click {
            viewModel.requestCameraPermission()
        }
        imgGallery.click {
            openGallery()
        }
        imgFlash.click {
            toggleFlash()
        }
    }
    cameraManager.setCallback(object : ScanningResultListener {
        override fun onScanned(barcode: Barcode, width: Int, height: Int, image: Bitmap?) {
            appViewModel.postBarcode(barcode)
        }

        override fun onFail() {}
    })
}

fun ScanFragment.observerData() {
    (requireActivity() as BaseActivity<*>).insetsLiveData.observe(viewLifecycleOwner) {
        binding.layoutCameraController.setMargin(top = it.getInsets(WindowInsetsCompat.Type.statusBars()).top)
    }
    appViewModel.barcode.observe(viewLifecycleOwner) {
        it?.let { barcode ->
            playEffect()
            saveToDatabase(barcode)
            navigateToResultActivity()
        }
    }
    viewModel.isCameraPermissionGranted.observe(viewLifecycleOwner) {
        binding.layoutPermission.root.isVisible = !it
        if (it) { 
            
        }
    }
    viewModel.galleryBarcode.observe(viewLifecycleOwner) {
        it?.let { barcode ->
            appViewModel.postBarcode(barcode)
        }
    }
}

fun ScanFragment.startCamera() {
    cameraManager.bindToLifecycle(viewLifecycleOwner)
    cameraManager.startCamera(binding.cameraPreview)
}

fun ScanFragment.toggleFlash() {
    cameraManager.toggleFlash { isOn ->
        binding.imgFlash.setImageResource(if (isOn) R.drawable.ic_scan_flash_on else R.drawable.ic_scan_flash_off)
    }
}

fun ScanFragment.openGallery() {
    galleryResultLauncher.launchWithTransition(
        requireContext(),
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
    )
}

fun ScanFragment.saveToDatabase(barcode: Barcode) {
    CoroutineScope(Dispatchers.IO).launch {
        db.insertSavedCode(
            SavedBarcode(
                id = 0,
                displayValue = barcode.displayValue ?: "",
                rawValue = barcode.rawValue ?: "",
                type = barcode.valueType,
                format = barcode.format
            )
        )
    }
}

fun ScanFragment.playEffect() {
    if (AppStates.scanWithSound) {
        requireContext().runOnBackgroundThread {
            MediaPlayer.create(requireContext(), R.raw.beep).apply {
                start()
                release()
            }
        }
    }
    if (AppStates.scanWithVibrate) {
        (requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(75)
    }
}

fun ScanFragment.navigateToResultActivity() {
    if (isAdded) {
        startActivity(
            Intent(requireContext(), ResultActivity::class.java)
        )
    }
}