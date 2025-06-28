package com.ecomobile.qrcode.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ecomobile.base.permission.PermissionUtil
import com.ecomobile.base.storage_data.AppStates

class CameraPermissionManager(
    private val activity: AppCompatActivity,
    private val resultCaller: ActivityResultCaller
) {

    private var onPermissionGranted: () -> Unit = {}

    private val imagePermissionLauncher = resultCaller.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (PermissionUtil.hasCameraPermission(activity)) {
            onPermissionGranted.invoke()
        } else {
            AppStates.putBoolean(PermissionUtil.imagePermission, true)
        }
    }

    private val imagePermissionResult = resultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (PermissionUtil.hasCameraPermission(activity)) {
            onPermissionGranted.invoke()
        }
    }

    fun doOnPermissionGranted(action: () -> Unit) {
        onPermissionGranted = action
        if (PermissionUtil.hasCameraPermission(activity)) {
            action()
        } else {
            requestImagePermission()
        }
    }

    private fun requestImagePermission() {
        if (PermissionUtil.neverAskAgainSelected(
                activity,
                PermissionUtil.cameraPermission
            )
        ) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${activity.packageName}")
                imagePermissionResult.launch(this)
            }
        } else {
            imagePermissionLauncher.launch(PermissionUtil.cameraPermission)
        }
    }

}