package com.ecomobile.qrcode.screens.home.fragment.scan.repository

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ecomobile.base.permission.PermissionUtil
import com.ecomobile.qrcode.permission.CameraPermissionManager
import com.ecomobile.qrcode.screens.home.fragment.scan.model.CameraManager
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class ScanRepository(private val context: Context) {

    private val _isCameraPermissionGranted = MutableLiveData(false)
    val isCameraPermissionGranted: LiveData<Boolean> get() = _isCameraPermissionGranted

    private var cameraPermissionManager: CameraPermissionManager? = null
    private var cameraManager: CameraManager? = null

    private var _galleryBarcode = MutableLiveData<Barcode?>()
    val galleryBarcode: LiveData<Barcode?> get() = _galleryBarcode

    init {
        cameraManager = CameraManager(context)
    }

    fun initCameraPermissionManager(activity: AppCompatActivity, resultCaller: ActivityResultCaller) {
        cameraPermissionManager = CameraPermissionManager(activity, resultCaller)
    }

    fun checkCameraPermission(activity: AppCompatActivity) {
        _isCameraPermissionGranted.postValue(PermissionUtil.hasCameraPermission(activity))
    }

    fun updateUiState() {
    }

    fun requestCameraPermission() {
        cameraPermissionManager?.doOnPermissionGranted {
            _isCameraPermissionGranted.postValue(true)
        }
    }

    fun scanCodeFromImage(imageUri: Uri) {
        val inputImage = InputImage.fromFilePath(context, imageUri)
        try {
            BarcodeScanning
                .getClient()
                .process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        _galleryBarcode.postValue(barcodes.first())
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}