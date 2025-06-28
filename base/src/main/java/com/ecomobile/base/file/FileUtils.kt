package com.ecomobile.base.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ecomobile.firebase.analystic.Tracking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    const val ASSETS_FOLDER_STICKER = "sticker"
    const val ASSETS_FOLDER_FONT = "font"
    const val ASSETS_FOLDER_BACKGROUNDS = "backgrounds"
    const val ASSETS_FOLDER_TEMP_CROP = "temp_crop"

    fun availableStickerPath(context: Context): String {
        val file = File(context.filesDir, ASSETS_FOLDER_STICKER)
        if (!file.exists()) {
            file.mkdirs()
        }
        addPermissionForPrivateFile(file)
        return file.path
    }

    fun availableFontPath(context: Context): String {
        val file = File(context.filesDir, ASSETS_FOLDER_FONT)
        if (!file.exists()) {
            file.mkdirs()
        }
        addPermissionForPrivateFile(file)
        return file.path
    }

    fun availableBackgroundsPath(context: Context): String {
        val file = File(context.filesDir, ASSETS_FOLDER_BACKGROUNDS)
        if (!file.exists()) {
            file.mkdirs()
        }
        addPermissionForPrivateFile(file)
        return file.path
    }

    fun getTemporaryCropPath(context: Context): String {
        val file = File(context.filesDir, ASSETS_FOLDER_TEMP_CROP)
        if (!file.exists()) {
            file.mkdirs()
        }
        addPermissionForPrivateFile(file)
        return file.path
    }

    fun getCutterPath(): String {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "V9Kut/CutterImage")
        if (!file.exists()) file.mkdirs()
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/V9Kut/CutterImage/"
    }

    fun getV9KutImagePath(): String {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "V9Kut/Images")
        if (!file.exists()) file.mkdirs()
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/V9Kut/Images/"
    }

    fun getRandomFileName(): String {
        return "V9Kut_${System.currentTimeMillis()}.png"
    }

    fun deleteAllFileInTemporaryCropFolder(context: Context) {
        val folder = File(getTemporaryCropPath(context))
        folder.listFiles()?.forEach {
            it.delete()
        }
    }

    private fun addPermissionForPrivateFile(file: File) {
        if (!file.canExecute()) file.setExecutable(true)
        if (!file.canRead()) file.setReadable(true)
        if (!file.canWrite()) file.setWritable(true)
    }

    fun copyAssetsToStorage(context: Context, assetsPath: String, destinationPath: String) {
        try {
            val assets = context.assets.list(assetsPath) ?: return
            val destinationDir = File(destinationPath)
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }

            for (item in assets) {
                val assetItemPath = if (assetsPath.isEmpty()) item else "$assetsPath/$item"
                val destItemPath = "$destinationPath/$item"
                val subItems = context.assets.list(assetItemPath)
                if (subItems.isNullOrEmpty()) {
                    copyFileIfNeeded(context, assetItemPath, destItemPath)
                } else {
                    copyAssetsToStorage(context, assetItemPath, destItemPath)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyFileIfNeeded(context: Context, assetFilePath: String, destFilePath: String) {
        try {
            val destFile = File(destFilePath)
            if (destFile.exists()) {
                return
            }
            destFile.parentFile?.mkdirs()
            context.assets.open(assetFilePath).use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveBitmapToInternalStorage(
        bitmap: Bitmap?,
        fileName: String,
        onDone: (Boolean, String?) -> Unit
    ) {
        if (bitmap == null || fileName.isBlank()) {
            Log.e("HAI", "saveBitmapToInternalStorage: Bitmap null hoặc fileName không hợp lệ")
            onDone(false, "Bitmap null hoặc fileName không hợp lệ")
            return
        }

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val file = File(fileName)
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) // PNG không cần chất lượng
                    fos.flush()
                    onDone(true, null)
                }
            } catch (e: IOException) {
                Log.e("HAI", "saveBitmapToInternalStorage: Lỗi IO - ${e.message}")
                onDone(false, "Lỗi khi lưu file: ${e.message}")
            } catch (e: SecurityException) {
                Log.e("HAI", "saveBitmapToInternalStorage: Lỗi quyền - ${e.message}")
                onDone(false, "Không có quyền truy cập: ${e.message}")
            } catch (e: Exception) {
                Log.e("HAI", "saveBitmapToInternalStorage: Lỗi không xác định - ${e.message}")
                onDone(false, "Lỗi không xác định: ${e.message}")
            }
        }
    }

    fun scanFile(context: Context, filePath: String?, onDone: (String) -> Unit = {}) {
        if (filePath.isNullOrEmpty()) return
        try {
            MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { path, uri ->
                onDone(path)
            }
        } catch (e: Exception) {
            onDone(filePath)
            Tracking.logEvent("Exception - scanFile - ${e.message}")
        }
    }

    fun getBitmapFromFile(context: Context, path: String): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(path)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .submit()
                .get()
                .copy(Bitmap.Config.ARGB_8888, true)
        } catch (e: Exception) {
            Log.e("HAI", "getBitmapFromFile: ${e.message}")
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap
        } catch (e: OutOfMemoryError) {
            bitmap
        }
    }
}