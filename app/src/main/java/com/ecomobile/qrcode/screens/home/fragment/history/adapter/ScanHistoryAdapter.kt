package com.ecomobile.qrcode.screens.home.fragment.history.adapter

import android.content.Context
import com.ecomobile.base.BaseAdapter
import com.ecomobile.base.extension.click
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.ecomobile.qrcode.databinding.ItemScanHistoryBinding
import com.google.mlkit.vision.barcode.common.Barcode

class ScanHistoryAdapter: BaseAdapter<SavedBarcode, ItemScanHistoryBinding>(emptyList()) {

    var onItemClick: ((SavedBarcode) -> Unit)? = null

    override fun getLayoutResId(): Int {
        return R.layout.item_scan_history
    }

    override fun bind(binding: ItemScanHistoryBinding, item: SavedBarcode, position: Int) {
        binding.apply {
            tvBarcodeValue.text = item.displayValue
            tvBarcodeType.text = getBarcodeTypeText(root.context, item.type)

            root.click {
                onItemClick?.invoke(item)
            }
        }
    }

    private fun getBarcodeTypeText(context: Context, type: Int): String {
        return when(type) {
            Barcode.TYPE_CONTACT_INFO -> context.getString(R.string.barcode_type_contact)
            Barcode.TYPE_EMAIL -> context.getString(R.string.barcode_type_email)
            Barcode.TYPE_ISBN -> context.getString(R.string.barcode_type_isbn)
            Barcode.TYPE_PHONE -> context.getString(R.string.barcode_type_phone)
            Barcode.TYPE_PRODUCT -> context.getString(R.string.barcode_type_product)
            Barcode.TYPE_SMS -> context.getString(R.string.barcode_type_sms)
            Barcode.TYPE_TEXT -> context.getString(R.string.barcode_type_text)
            Barcode.TYPE_URL -> context.getString(R.string.barcode_type_url)
            Barcode.TYPE_WIFI -> context.getString(R.string.barcode_type_wifi)
            Barcode.TYPE_GEO -> context.getString(R.string.barcode_type_geo)
            Barcode.TYPE_CALENDAR_EVENT -> context.getString(R.string.barcode_type_calendar_event)
            else -> context.getString(R.string.barcode_type_unknown)
        }
    }
}