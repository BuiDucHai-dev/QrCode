package com.ecomobile.qrcode.screens.home.fragment.scan.model

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.TorchState
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class CameraManager(private val context: Context) {

    private val mlKitBarcodeAnalyzer = MLKitBarcodeAnalyzer()
    private val executor = ContextCompat.getMainExecutor(context)

    private val cameraController: LifecycleCameraController = LifecycleCameraController(context).apply {
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
        imageAnalysisTargetSize = CameraController.OutputSize(Size(1080, 2000))
        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        setImageAnalysisAnalyzer(executor, mlKitBarcodeAnalyzer)
    }

    fun setCallback(listener: ScanningResultListener) {
        mlKitBarcodeAnalyzer.setCallback(listener)
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    fun unbind() {
        cameraController.unbind()
    }

    fun startCamera(view: PreviewView) {
        view.controller = cameraController
    }

    fun isFlashOn(): Boolean {
        if (cameraController.cameraInfo?.hasFlashUnit() == false) {
            Log.w("CameraManager", "Thiết bị không hỗ trợ flash")
            return false
        }
        return cameraController.torchState.value == TorchState.ON
    }

    fun toggleFlash(onCompleted: (Boolean) -> Unit) {
        try {
            if (cameraController.cameraInfo?.hasFlashUnit() == false) {
                Log.w("CameraManager", "Thiết bị không hỗ trợ flash")
                return
            }
            val currentFlashMode = cameraController.torchState.value ?: TorchState.OFF
            val newFlashMode = if (currentFlashMode == TorchState.ON) {
                TorchState.OFF
            } else {
                TorchState.ON
            }
            cameraController.enableTorch(newFlashMode == TorchState.ON).addListener({
                onCompleted.invoke((cameraController.torchState.value ?: TorchState.OFF) == TorchState.ON)
            }, executor)

        } catch (e: IllegalStateException) {
            Log.e("CameraManager", "Camera chưa được bind hoặc đã unbind: ${e.message}")
        } catch (e: Exception) {
            Log.e("CameraManager", "Lỗi không xác định khi chuyển đổi flash: ${e.message}")
        }
    }
}