package com.ecomobile.qrcode.screens.result

import android.util.Log
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.ecomobile.base.extension.gone
import com.ecomobile.base.extension.visible
import com.ecomobile.qrcode.R
import com.google.mlkit.vision.barcode.common.Barcode
import com.kedia.ogparser.OpenGraphResult

fun ResultActivity.observerData() {
    appViewModel.barcode.observe(this) {
        it?.let { barcode ->
            viewModel.convertBarcodeContentToString(barcode)
            binding.tvBarcodeType.text = getBarcodeTypeText(barcode.valueType)
            binding.imgSearch.setImageResource(getSearchIcon(barcode.valueType))
            binding.tvSearch.text = getSearchTypeText(barcode.valueType)
            viewModel.parseWebPreview()
        }
    }
    appViewModel.savedBarcode.observe(this) {
        it?.let { savedBarcode ->
            viewModel.convertSavedBarcodeContentToString(savedBarcode)
            binding.tvBarcodeType.text = getBarcodeTypeText(savedBarcode.type)
            binding.imgSearch.setImageResource(getSearchIcon(savedBarcode.type))
            binding.tvSearch.text = getSearchTypeText(savedBarcode.type)
            viewModel.parseWebPreview()
        }
    }
    viewModel.barcodeContentString.observe(this) {
        var content = it
        if (appViewModel.barcode.value?.valueType == Barcode.TYPE_URL) {
            content = StringBuilder(String.format("<font color='#206FE6'><u><b>%s</b></u></font>", content))
        }
        binding.tvResult.text = HtmlCompat.fromHtml(String.format(content.toString()), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
    viewModel.isNeedToUpdateSizeText.observe(this) {
        binding.apply {
            if (it) {
                tvResult.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(com.intuit.sdp.R.dimen._14sdp))
                tvResult.setTypeface(ResourcesCompat.getFont(this@observerData, R.font.roboto_regular))
            } else {
                tvResult.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(com.intuit.sdp.R.dimen._25sdp))
                tvResult.setTypeface(ResourcesCompat.getFont(this@observerData, R.font.roboto_bold))
            }
        }
    }
    viewModel.openGraphResult.observe(this) {
        showWebPreview(it)
    }
}

fun ResultActivity.getBarcodeTypeText(type: Int): String {
    return when(type) {
        Barcode.TYPE_CONTACT_INFO -> getString(R.string.barcode_type_contact)
        Barcode.TYPE_EMAIL -> getString(R.string.barcode_type_email)
        Barcode.TYPE_ISBN -> getString(R.string.barcode_type_isbn)
        Barcode.TYPE_PHONE -> getString(R.string.barcode_type_phone)
        Barcode.TYPE_PRODUCT -> getString(R.string.barcode_type_product)
        Barcode.TYPE_SMS -> getString(R.string.barcode_type_sms)
        Barcode.TYPE_TEXT -> getString(R.string.barcode_type_text)
        Barcode.TYPE_URL -> getString(R.string.barcode_type_url)
        Barcode.TYPE_WIFI -> getString(R.string.barcode_type_wifi)
        Barcode.TYPE_GEO -> getString(R.string.barcode_type_geo)
        Barcode.TYPE_CALENDAR_EVENT -> getString(R.string.barcode_type_calendar_event)
        else -> getString(R.string.barcode_type_unknown)
    }
}

fun ResultActivity.getSearchTypeText(type: Int): String {
    return when(type) {
        Barcode.TYPE_CONTACT_INFO -> getString(R.string.add_contact)
        Barcode.TYPE_EMAIL -> getString(R.string.send_email)
        Barcode.TYPE_ISBN -> getString(R.string.find_book)
        Barcode.TYPE_PHONE -> getString(R.string.call)
        Barcode.TYPE_PRODUCT -> getString(R.string.web_search)
        Barcode.TYPE_SMS -> getString(R.string.send_message)
        Barcode.TYPE_TEXT -> getString(R.string.web_search)
        Barcode.TYPE_URL -> getString(R.string.open_website)
        Barcode.TYPE_WIFI -> getString(R.string.copy_password)
        Barcode.TYPE_GEO -> getString(R.string.show_on_map)
        Barcode.TYPE_CALENDAR_EVENT -> getString(R.string.add_to_calendar)
        else -> getString(R.string.barcode_type_unknown)
    }
}

fun ResultActivity.showWebPreview(openGraphResult: OpenGraphResult?) {
    binding.apply {
        if (openGraphResult != null) {
            layoutWebPreview.root.visible()
            tvResult.gone()
            openGraphResult.image?.let { image ->
                layoutWebPreview.imgWebPreview.visible()
                Glide
                    .with(this@showWebPreview)
                    .load(image)
                    .error(R.drawable.result_image_product_error)
                    .into(layoutWebPreview.imgWebPreview)
            } ?: run {
                layoutWebPreview.imgWebPreview.gone()
            }
            layoutWebPreview.tvWebTitle.text = openGraphResult.title
            layoutWebPreview.tvWebDescription.text = openGraphResult.description
            layoutWebPreview.tvWebUrl.text = HtmlCompat.fromHtml(String.format("<u>${openGraphResult.url}</u>"), HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            layoutWebPreview.root.gone()
            tvResult.visible()
        }
    }
}

fun getSearchIcon(type: Int): Int {
    return when(type) {
        Barcode.TYPE_CONTACT_INFO -> R.drawable.result_ic_search_contact
        Barcode.TYPE_EMAIL -> R.drawable.result_ic_search_email
        Barcode.TYPE_ISBN -> R.drawable.result_ic_search
        Barcode.TYPE_PHONE -> R.drawable.result_ic_search_phone
        Barcode.TYPE_PRODUCT -> R.drawable.result_ic_search
        Barcode.TYPE_SMS -> R.drawable.result_ic_search_sms
        Barcode.TYPE_TEXT -> R.drawable.result_ic_search
        Barcode.TYPE_URL -> R.drawable.result_ic_search_url
        Barcode.TYPE_WIFI -> R.drawable.result_ic_search_copy
        Barcode.TYPE_GEO -> R.drawable.result_ic_search_location
        Barcode.TYPE_CALENDAR_EVENT -> R.drawable.result_ic_search_calendar
        else -> R.drawable.result_ic_search
    }
}