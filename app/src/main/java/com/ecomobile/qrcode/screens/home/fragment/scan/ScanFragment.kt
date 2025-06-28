package com.ecomobile.qrcode.screens.home.fragment.scan

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ecomobile.base.BaseFragment
import com.ecomobile.base.extension.toast
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.database.roomdb.viewmodel.RoomDatabaseViewModel
import com.ecomobile.qrcode.databinding.FragmentScanBinding
import com.ecomobile.qrcode.screens.home.fragment.scan.model.CameraManager
import com.ecomobile.qrcode.screens.home.fragment.scan.viewmodel.ScanViewModel
import com.ecomobile.qrcode.viewmodel.AppViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanFragment: BaseFragment<FragmentScanBinding>() {

    val cameraManager by lazy {
        CameraManager(requireContext())
    }

    val viewModel: ScanViewModel by viewModel()
    val db: RoomDatabaseViewModel by inject()
    val appViewModel: AppViewModel by inject()

    val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            viewModel.scanCodeFromImage(uri)
        } ?: run {
            requireContext().toast("Can not get image")
        }
    }

    override val layoutResId: Int
        get() = R.layout.fragment_scan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initCameraPermissionManager(requireActivity() as AppCompatActivity, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listener()
        observerData()
    }

    override fun onStart() {
        super.onStart()
        startCamera()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUiState()
        cameraManager.bindToLifecycle(viewLifecycleOwner)
        binding.imgFlash.setImageResource(if (cameraManager.isFlashOn()) R.drawable.ic_scan_flash_on else R.drawable.ic_scan_flash_off)
    }

    override fun onPause() {
        super.onPause()
        cameraManager.unbind()
    }
}