package com.ecomobile.qrcode.screens.home.fragment.scan.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.ecomobile.base.extension.runDelay
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class MLKitBarcodeAnalyzer: ImageAnalysis.Analyzer {

    private val barcodeScanner = BarcodeScanning
        .getClient(
            BarcodeScannerOptions.Builder()
            .enableAllPotentialBarcodes()
            .build()
        )

    private var callback: ScanningResultListener? = null

    fun setCallback(listener: ScanningResultListener) {
        this.callback = listener
    }

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        CoroutineScope(Dispatchers.IO).launch {
            barcodeScanner
                .process(
                    InputImage.fromMediaImage(image.image!!, 0)
                )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.isEmpty()) {
                            callback?.onFail()
                            image.close()
                            return@addOnCompleteListener
                        }
                        var hasResult = false
                        for (barcode in it.result) {
                            if (barcode.format != -1 && !isContainedInBatchMode(barcode)) {
//                                val res = image.image!!.toBitmap()?.rotate(90F)
                                hasResult = true
                                runDelay(1000L) {
                                    image.close()
                                }
                                callback?.onScanned(barcode, image.width, image.height, image.image!!.toBitmap())
                                break
                            }
                        }
                        if (!hasResult) {
                            callback?.onFail()
                            image.close()
                        }
                    } else {
                        callback?.onFail()
                        image.close()
                    }
                }
        }
    }

    private fun isContainedInBatchMode(barcode: Barcode): Boolean {
        return false
    }
}

interface ScanningResultListener {
    fun onScanned(barcode: Barcode, width: Int, height: Int, image: Bitmap?)
    fun onFail()
}

private fun Image.toBitmap(): Bitmap? {
    if (this.format != ImageFormat.YUV_420_888) {
        throw IllegalArgumentException("Định dạng hình ảnh không được hỗ trợ")
    }
    val yBuffer: ByteBuffer = this.planes[0].buffer
    val ySize = yBuffer.remaining()
    val data = ByteArray(ySize)
    yBuffer.get(data)
    return try {
        BitmapFactory.decodeByteArray(data, 0, data.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}