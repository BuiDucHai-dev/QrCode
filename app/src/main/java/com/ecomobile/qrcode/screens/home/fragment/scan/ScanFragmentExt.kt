package com.ecomobile.qrcode.screens.home.fragment.scan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Vibrator
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.ecomobile.base.BaseActivity
import com.ecomobile.base.extension.click
import com.ecomobile.base.extension.launchActivity
import com.ecomobile.base.extension.launchWithTransition
import com.ecomobile.base.extension.runOnBackgroundThread
import com.ecomobile.base.extension.setMargin
import com.ecomobile.base.extension.toast
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
    setupLayoutPermission()
    viewModel.checkCameraPermission(requireActivity() as AppCompatActivity)
}

fun ScanFragment.listener() {
    binding.apply {
        layoutPermission.allowCard.click(true) {
            viewModel.requestCameraPermission()
        }
        layoutPermission.scanWithGallery.click {
            openGallery()
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

fun ScanFragment.setupLayoutPermission() {
    val bold1 = getString(R.string.camera_permission)
    val content1 = getString(R.string.requires_s_to_work, bold1)
    binding.layoutPermission.tvFirst.text = getBoldApart(content1, bold1)
    val bold2 = getString(R.string.instant_scan)
    val content2 = getString(R.string.s_with_super_fast, bold2)
    binding.layoutPermission.tvSecond.text = getBoldApart(content2, bold2)
    val bold31 = getString(R.string.no_data_collected)
    val bold32 = getString(R.string.shared)
    val content3 = getString(R.string.your_privacy_guarenteed_s_or_s_with_third_parties, bold31, bold32)
    binding.layoutPermission.tvThird.text = getBoldApart(content3, bold31, bold32)
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
    (requireActivity() as BaseActivity<*>).launchActivity(
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        },
        action = {
            it.data?.data?.let { uri ->
                viewModel.scanCodeFromImage(uri)
            } ?: run {
                requireContext().toast("Can not get image")
            }
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

private fun getBoldApart(content: String, vararg bolds: String): SpannableString {
    val spannable = SpannableString(content)
    bolds.forEach { bold ->
        val start = content.indexOf(bold)
        if (start == -1) return@forEach
        val end = start + bold.length
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannable
}