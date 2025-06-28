package com.ecomobile.qrcode.screens.result.repository

import android.content.Context
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.adapters.TextViewBindingAdapter.setTextSize
import androidx.lifecycle.MutableLiveData
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.database.model.SavedBarcode
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.client.result.AddressBookParsedResult
import com.google.zxing.client.result.CalendarParsedResult
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.GeoParsedResult
import com.google.zxing.client.result.ResultParser
import com.google.zxing.client.result.SMSParsedResult
import com.google.zxing.client.result.TelParsedResult
import com.google.zxing.client.result.WifiParsedResult
import com.kedia.ogparser.OpenGraphCallback
import com.kedia.ogparser.OpenGraphParser
import com.kedia.ogparser.OpenGraphResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultRepository(
    private val context: Context
) {

    //Contact Info
    var contactName: String = ""
    var contactPhone: String = ""
    var contactEmail: String = ""
    var contactAddress: String = ""
    var contactTitle: String = ""
    var contactOrganization: String = ""
    var contactUrl: String = ""

    //Email Info
    var emailTo: String = ""
    var emailSubject: String = ""
    var emailBody: String = ""

    //Phone Info
    var phoneNumber: String = ""

    //Sms Info
    var smsPhone: String = ""
    var smsMessage: String = ""

    //Url Info
    var urlUrl: String = ""

    //Wifi Info
    var wifiPassword: String = ""

    //Geo Info
    var geoLat: Double = 0.0
    var geoLong: Double = 0.0

    //Event Info
    var eventTitle: String = ""
    var eventStart: String = ""
    var eventEnd: String = ""
    var eventLocation: String = ""

    private val datePattern = "MMM dd, yyyy HH:mm:ss"

    private val _barcodeContentString = MutableLiveData<StringBuilder>()
    val barcodeContentString: MutableLiveData<StringBuilder> = _barcodeContentString

    private val _isNeedToUpdateSizeText = MutableLiveData(false)
    val isNeedToUpdateSizeText: MutableLiveData<Boolean> = _isNeedToUpdateSizeText

    private val _openGraphResult = MutableLiveData<OpenGraphResult?>()
    val openGraphResult: MutableLiveData<OpenGraphResult?> = _openGraphResult

    private var openGraphParser: OpenGraphParser? = null

    init {
        openGraphParser = OpenGraphParser(
            object : OpenGraphCallback {
                override fun onError(error: String) {
                    _openGraphResult.postValue(null)
                }

                override fun onPostResponse(openGraphResult: OpenGraphResult) {
                    _openGraphResult.postValue(openGraphResult)
                }
            }, true
        )
    }

    fun convertBarcodeContentToString(barcode: Barcode) {
        resetData()
        val result = when(barcode.valueType) {
            Barcode.TYPE_CONTACT_INFO -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithContactData(barcode)
            }
            Barcode.TYPE_EMAIL -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithEmailData(barcode)
            }
            Barcode.TYPE_ISBN -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithISBNData(barcode)
            }
            Barcode.TYPE_PHONE -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithPhoneData(barcode)
            }
            Barcode.TYPE_PRODUCT -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithProductData(barcode)
            }
            Barcode.TYPE_SMS -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithSmsData(barcode)
            }
            Barcode.TYPE_TEXT -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithTextData(barcode)
            }
            Barcode.TYPE_URL -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithUrlData(barcode)
            }
            Barcode.TYPE_WIFI -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithWifiData(barcode)
            }
            Barcode.TYPE_GEO -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithGeoData(barcode)
            }
            Barcode.TYPE_CALENDAR_EVENT -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithCalendarEvent(barcode)
            }
            else -> {
                _isNeedToUpdateSizeText.postValue(false)
                StringBuilder()
            }
        }
        _barcodeContentString.postValue(result)
    }
    
    fun convertSavedBarcodeContentToString(savedBarcode: SavedBarcode) {
        resetData()
        val zxingResult = Result(savedBarcode.rawValue, null, null, getBarcodeFormat(savedBarcode.format))
        val result = when(savedBarcode.type) {
            Barcode.TYPE_CONTACT_INFO -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithContactData(zxingResult)
            }
            Barcode.TYPE_EMAIL -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithEmailData(zxingResult)
            }
            Barcode.TYPE_ISBN -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithISBNData(zxingResult)
            }
            Barcode.TYPE_PHONE -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithPhoneData(zxingResult)
            }
            Barcode.TYPE_PRODUCT -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithProductData(zxingResult)
            }
            Barcode.TYPE_SMS -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithSmsData(zxingResult)
            }
            Barcode.TYPE_TEXT -> {
                _isNeedToUpdateSizeText.postValue(false)
                convertWithTextData(zxingResult)
            }
            Barcode.TYPE_URL -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithUrlData(zxingResult)
            }
            Barcode.TYPE_WIFI -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithWifiData(zxingResult)
            }
            Barcode.TYPE_GEO -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithGeoData(zxingResult)
            }
            Barcode.TYPE_CALENDAR_EVENT -> {
                _isNeedToUpdateSizeText.postValue(true)
                convertWithCalendarEvent(zxingResult)
            }
            else -> {
                _isNeedToUpdateSizeText.postValue(false)
                StringBuilder()
            }
        }
        _barcodeContentString.postValue(result)
    }

    fun parseWebPreview() {
        if (urlUrl.isBlank()) {
            _openGraphResult.postValue(null)
        } else {
            openGraphParser?.parse(urlUrl)
        }
    }

    private fun resetData() {
        contactName = ""
        contactPhone = ""
        contactEmail = ""
        contactAddress = ""
        contactTitle = ""
        contactOrganization = ""
        contactUrl = ""

        emailTo = ""
        emailSubject = ""
        emailBody = ""

        phoneNumber = ""

        smsPhone = ""
        smsMessage = ""

        urlUrl = ""

        wifiPassword = ""

        geoLat = 0.0
        geoLong = 0.0

        eventTitle = ""
        eventStart = ""
        eventEnd = ""
        eventLocation = ""
    }

    private fun convertWithContactData(barcode: Barcode): StringBuilder {
        val result = StringBuilder()
        var isFirst = true
        barcode.contactInfo?.apply {
            if (!name?.formattedName.isNullOrBlank()) {
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.name)}:")
                    .append(name?.formattedName)
                contactName = name?.formattedName ?: ""
            }
            if (phones.isNotEmpty()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                isFirst = false
                result.appendBoldText("${context.getString(R.string.phone)}:")
                phones.forEach { phone ->
                    result.append(phone.number)
                    if (phones.last() != phone) {
                        result.append("<br>")
                    }
                }
                contactPhone = phones.first().number ?: ""
            }
            if (emails.isNotEmpty()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                isFirst = false
                result.appendBoldText("${context.getString(R.string.email)}:")
                emails.forEach { email ->
                    result.append(email.address)
                    if (emails.last() != email) {
                        result.append("<br>")
                    }
                }
                contactEmail = emails.first().address ?: ""
            }
            if (addresses.isNotEmpty()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                isFirst = false
                result.appendBoldText("${context.getString(R.string.address)}:")
                addresses.forEach { address ->
                    result.append(address)
                    if (addresses.last() != address) {
                        result.append("<br>")
                    }
                }
                contactAddress = addresses.first().addressLines.first().toString()
            }
            if (!title.isNullOrBlank()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.note)}:")
                    .append(title)
                contactTitle = title ?: ""
            }
            if (urls.isNotEmpty()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                isFirst = false
                result.appendBoldText("${context.getString(R.string.website)}:")
                urls.forEach { url ->
                    result.append(url)
                    if (urls.last() != url) {
                        result.append("<br>")
                    }
                }
                contactUrl = urls.first() ?: ""
            }
            contactOrganization = organization ?: ""
        }
        return result
    }

    private fun convertWithEmailData(barcode: Barcode): StringBuilder {
        val result = StringBuilder()
        var isFirst = true
        barcode.email?.apply {
            if (!address.isNullOrBlank()) {
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.to)}:")
                    .append(address)
                emailTo = address ?: ""
            }
            if (!subject.isNullOrEmpty()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.subject)}:")
                    .append(subject)
                emailSubject = subject ?: ""
            }
            if (!body.isNullOrEmpty()) {
                if (!isFirst) {
                    result.append("<br>")
                }
                result
                    .appendBoldText("${context.getString(R.string.mail_content)}:")
                    .append(body)
                emailBody = body ?: ""
            }
        }
        return result
    }

    private fun convertWithISBNData(barcode: Barcode): StringBuilder {
        return StringBuilder(barcode.rawValue.toString())
    }

    private fun convertWithPhoneData(barcode: Barcode): StringBuilder {
        return StringBuilder(barcode.rawValue.toString())
    }

    private fun convertWithProductData(barcode: Barcode): StringBuilder {
        return StringBuilder(barcode.rawValue.toString())
    }

    private fun convertWithSmsData(barcode: Barcode): StringBuilder {
        val result = StringBuilder()
        var isFirst = true
        barcode.sms?.apply {
            if (phoneNumber != null) {
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.phone)}:")
                    .append(phoneNumber)
                smsPhone = phoneNumber ?: ""
            }
            if (message != null) {
                if (!isFirst) {
                    result.append("<br>")
                }
                result
                    .appendBoldText("${context.getString(R.string.message)}:")
                    .append(message)
                smsMessage = message ?: ""
            }
        }
        return result
    }

    private fun convertWithTextData(barcode: Barcode): StringBuilder {
        return StringBuilder(barcode.rawValue.toString())
    }

    private fun convertWithUrlData(barcode: Barcode): StringBuilder {
        var url = barcode.url?.url ?: ""
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        urlUrl = url
        return StringBuilder(url)
    }

    private fun convertWithWifiData(barcode: Barcode): StringBuilder {
        val result = StringBuilder()
        barcode.wifi?.apply {
            result
                .appendBoldText("${context.getString(R.string.name)}:")
                .append(ssid)
                .append("<br>")
                .appendBoldText("${context.getString(R.string.password)}:")
                .append(password)
                .append("<br>")
                .appendBoldText("${context.getString(R.string.type)}:")
                .append(
                    when (encryptionType) {
                        Barcode.WiFi.TYPE_OPEN -> "Open"
                        Barcode.WiFi.TYPE_WEP -> "WEP"
                        else -> "WPA/WPA2"
                    }
                )
            wifiPassword = password ?: ""
        }
        return StringBuilder(result.toString().replace("%", "%%"))
    }

    private fun convertWithGeoData(barcode: Barcode): StringBuilder {
        val result = StringBuilder()
        barcode.geoPoint?.apply {
            result
                .appendBoldText("${context.getString(R.string.longitude)}:")
                .append(lng)
                .appendBoldText("${context.getString(R.string.latitude)}:")
                .append(lat)
            geoLong = lng
            geoLat = lat
        }
        return result
    }

    private fun convertWithCalendarEvent(barcode: Barcode): StringBuilder {
        val result = StringBuilder()
        barcode.calendarEvent?.apply {
            result
                .appendBoldText("${context.getString(R.string.subject)}:")
                .append(summary)
                .append("<br>")
                .appendBoldText("${context.getString(R.string.start)}:")
                .append(start?.rawValue?.convertToTime(datePattern) ?: "")
                .append("<br>")
                .appendBoldText("${context.getString(R.string.end)}:")
                .append(end?.rawValue?.convertToTime(datePattern) ?: "")
                .append("<br>")
                .appendBoldText("${context.getString(R.string.address)}:")
                .append(location)
                .append("<br>")
            eventTitle = summary ?: ""
            eventStart = start?.rawValue?.convertToTime(datePattern) ?: ""
            eventEnd = end?.rawValue?.convertToTime(datePattern) ?: ""
            eventLocation = location ?: ""
        }
        return result
    }

    private fun convertWithContactData(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        var isFirst = true
        (ResultParser.parseResult(zxingResult) as? AddressBookParsedResult)?.apply {
            if (names.isNotEmpty()) {
                isFirst = false
                result.appendBoldText("${context.getString(R.string.name)}:")
                names.forEach { name ->
                    result.append(name)
                    if (names.last() != name) {
                        result.append("<br>")
                    }
                }
                contactName = names.first() ?: ""
            }
            if (phoneNumbers.isNotEmpty()) {
                if (!isFirst) result.append("<br>")
                isFirst = false
                result.appendBoldText("${context.getString(R.string.phone)}:")
                phoneNumbers.forEach { phoneNumber ->
                    result.append(phoneNumber)
                    if (phoneNumbers.last() != phoneNumber) {
                        result.append("<br>")
                    }
                }
                contactPhone = phoneNumbers.first() ?: ""
            }
            if (emails.isNotEmpty()) {
                if (!isFirst) result.append("<br>")
                isFirst = false
                result.appendBoldText("${context.getString(R.string.email)}:")
                emails.forEach { email ->
                    result.append(email)
                    if (emails.last() != email) {
                        result.append("<br>")
                    }
                }
                contactEmail = emails.first() ?: ""
            }
            if (addresses.isNotEmpty()) {
                if (!isFirst) result.append("<br>")
                isFirst = false
                result.appendBoldText("${context.getString(R.string.address)}:")
                addresses.forEach { address ->
                    result.append(address)
                    if (addresses.last() != address) {
                        result.append("<br>")
                    }
                }
                contactAddress = addresses.first() ?: ""
            }
            if (title.isNotEmpty()) {
                if (!isFirst) result.append("<br>")
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.note)}:")
                    .append(title)
                contactTitle = title ?: ""
            }
            if (urLs.isNotEmpty()) {
                if (!isFirst) result.append("<br>")
                isFirst = false
                result.appendBoldText("${context.getString(R.string.website)}:")
                urLs.forEach { url ->
                    result.append(url)
                    if (urLs.last() != url) {
                        result.append("<br>")
                    }
                }
            }
            contactOrganization = org ?: ""
        }
        return result
    }

    private fun convertWithEmailData(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        var isFirst = true
        (ResultParser.parseResult(zxingResult) as? EmailAddressParsedResult)?.apply {
            if (emailAddress.isNotBlank()) {
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.to)}:")
                    .append(emailAddress)
                emailTo = emailAddress
            }
            if (subject.isNotBlank()) {
                if (!isFirst) result.append("<br>")
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.subject)}:")
                    .append(subject)
                emailSubject = subject
            }
            if (body.isNotBlank()) {
                if (!isFirst) result.append("<br>")
                result
                    .appendBoldText("${context.getString(R.string.mail_content)}:")
                    .append(body)
                emailBody = body
            }
        }
        return result
    }

    private fun convertWithISBNData(zxingResult: Result): StringBuilder {
        return StringBuilder(zxingResult.text)
    }

    private fun convertWithPhoneData(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        (ResultParser.parseResult(zxingResult) as? TelParsedResult)?.apply {
            result.append(number)
        }
        return result
    }

    private fun convertWithProductData(zxingResult: Result): StringBuilder {
        return StringBuilder(zxingResult.text)
    }

    private fun convertWithSmsData(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        var isFirst = true
        (ResultParser.parseResult(zxingResult) as? SMSParsedResult)?.apply {
            if (numbers.isNotEmpty()) {
                isFirst = false
                result
                    .appendBoldText("${context.getString(R.string.phone)}:")
                    .append(numbers.first())
                smsPhone = numbers.first()
            }
            if (body != null) {
                if (!isFirst) result.append("<br>")
                result
                    .appendBoldText("${context.getString(R.string.message)}:")
                    .append(body)
                smsMessage = body
            }
        }
        return result
    }

    private fun convertWithTextData(zxingResult: Result): StringBuilder {
        return StringBuilder(zxingResult.text)
    }

    private fun convertWithUrlData(zxingResult: Result): StringBuilder {
        var url = zxingResult.text
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        urlUrl = url
        return StringBuilder(url)
    }

    private fun convertWithWifiData(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        (ResultParser.parseResult(zxingResult) as? WifiParsedResult)?.apply {
            result
                .appendBoldText("${context.getString(R.string.name)}:")
                .append(ssid)
                .append("<br>")
                .appendBoldText("${context.getString(R.string.password)}:")
                .append(password)
                .append("<br>")
                .appendBoldText("${context.getString(R.string.type)}:")
                .append(networkEncryption)
            wifiPassword = password ?: ""
        }
        return StringBuilder(result.toString().replace("%", "%%"))
    }

    private fun convertWithGeoData(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        (ResultParser.parseResult(zxingResult) as? GeoParsedResult)?.apply {
            result
                .appendBoldText("${context.getString(R.string.longitude)}:")
                .append(longitude)
                .appendBoldText("${context.getString(R.string.latitude)}:")
                .append(latitude)
            geoLong = longitude
            geoLat = latitude
        }
        return result
    }

    private fun convertWithCalendarEvent(zxingResult: Result): StringBuilder {
        val result = StringBuilder()
        (ResultParser.parseResult(zxingResult) as? CalendarParsedResult)?.apply {
            result
                .appendBoldText("${context.getString(R.string.subject)}:")
                .append(summary)
                .append("<br>")
                .appendBoldText("${context.getString(R.string.start)}:")
                .append(start?.convertToTime(datePattern) ?: "")
                .append("<br>")
                .appendBoldText("${context.getString(R.string.end)}:")
                .append(end?.convertToTime(datePattern) ?: "")
                .append("<br>")
                .appendBoldText("${context.getString(R.string.address)}:")
                .append(location)
                .append("<br>")
            eventTitle = summary ?: ""
            eventStart = start?.convertToTime(datePattern) ?: ""
            eventEnd = end?.convertToTime(datePattern) ?: ""
            eventLocation = location ?: ""
        }
        return result
    }

    private fun getBarcodeFormat(format: Int): BarcodeFormat {
        return when (format) {
            Barcode.FORMAT_CODE_128 -> BarcodeFormat.CODE_128
            Barcode.FORMAT_CODE_39 -> BarcodeFormat.CODE_39
            Barcode.FORMAT_CODE_93 -> BarcodeFormat.CODE_93
            Barcode.FORMAT_CODABAR -> BarcodeFormat.CODABAR
            Barcode.FORMAT_DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            Barcode.FORMAT_EAN_13 -> BarcodeFormat.EAN_13
            Barcode.FORMAT_EAN_8 -> BarcodeFormat.EAN_8
            Barcode.FORMAT_ITF -> BarcodeFormat.ITF
            Barcode.FORMAT_QR_CODE -> BarcodeFormat.QR_CODE
            Barcode.FORMAT_UPC_A -> BarcodeFormat.UPC_A
            Barcode.FORMAT_UPC_E -> BarcodeFormat.UPC_E
            Barcode.FORMAT_PDF417 -> BarcodeFormat.PDF_417
            Barcode.FORMAT_AZTEC -> BarcodeFormat.AZTEC
            else -> BarcodeFormat.QR_CODE
        }
    }

}

private fun StringBuilder.appendBoldText(content: String): StringBuilder {
    return this
        .append("<b> ")
        .append(content)
        .append(" </b>")
}

private fun String.convertToTime(datePattern: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        val outputFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

private fun Date.convertToTime(datePattern: String): String {
    return SimpleDateFormat(datePattern, Locale.getDefault()).format(this)
}