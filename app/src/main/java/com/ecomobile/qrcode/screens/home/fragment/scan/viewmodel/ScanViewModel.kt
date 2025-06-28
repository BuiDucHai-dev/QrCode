package com.ecomobile.qrcode.screens.home.fragment.scan.viewmodel

import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.ecomobile.qrcode.screens.home.fragment.scan.repository.ScanRepository

class ScanViewModel(
    private val repository: ScanRepository
): ViewModel() {

    val isCameraPermissionGranted = repository.isCameraPermissionGranted
    val galleryBarcode = repository.galleryBarcode

    fun initCameraPermissionManager(activity: AppCompatActivity, resultCaller: ActivityResultCaller) {
        repository.initCameraPermissionManager(activity, resultCaller)
    }

    fun checkCameraPermission(activity: AppCompatActivity) {
        repository.checkCameraPermission(activity)
    }

    fun updateUiState() {
        repository.updateUiState()
    }

    fun requestCameraPermission() {
        repository.requestCameraPermission()
    }

    fun scanCodeFromImage(imageUri: Uri) {
        repository.scanCodeFromImage(imageUri)
    }
}