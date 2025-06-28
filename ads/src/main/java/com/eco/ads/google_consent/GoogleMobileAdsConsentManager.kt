package com.eco.ads.google_consent

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

object GoogleMobileAdsConsentManager {

    lateinit var consentInformation: ConsentInformation

    var consentGatheringComplete: (FormError?) -> Unit = {_ ->}
    var onConsentShow: () -> Unit = {}
    var onConsentDismiss: () -> Unit = {}

    var isShow: Boolean = false
    var isDismiss: Boolean = false

    fun initialize(context: Context) {
        consentInformation = UserMessagingPlatform.getConsentInformation(context)
    }

    fun gatherConsent(activity: Activity)  {
        if (!::consentInformation.isInitialized) {
            initialize(activity.applicationContext) // Gọi initialize nếu chưa được khởi tạo
        }
        val debugSettings = ConsentDebugSettings.Builder(activity).build()
        val param = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        consentInformation.requestConsentInfoUpdate(activity, param,
            {
              if (consentInformation.isConsentFormAvailable && consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                  UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                      activity,
                      { formError ->
                          isDismiss = true
                          if (isShow) {
                              onConsentDismiss()
                          }
                          consentGatheringComplete(formError)
                      }
                  )
                  if (!consentInformation.canRequestAds() && !isDismiss) {
                      isShow = true
                      onConsentShow()
                  }
              }
            },
            { formError ->
                consentGatheringComplete(formError)
            }
        )
    }

    fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    fun showPrivacyOptionsForm(activity: Activity, callback: ConsentForm.OnConsentFormDismissedListener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, callback)
    }
}