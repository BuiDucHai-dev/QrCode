package com.eco.ads.ads.app_open

import android.app.Activity
import com.eco.ads.AdsManager
import com.eco.ads.ads.AdStatus
import com.ecomobile.base.BaseActivity
import com.ecomobile.base.extension.hideLoadingView
import com.ecomobile.base.extension.showLoadingView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenAdManager(
    private val activity: Activity,
    private val adUnitId: String
) {

    private var appOpenAd: AppOpenAd? = null
    private var adStatus: AdStatus = AdStatus.None
    private var wasShown: Boolean = false

    fun loadAd() {
        if (adStatus == AdStatus.Loading || adStatus == AdStatus.Loaded) return
        adStatus = AdStatus.Loading
        // Load the App Open Ad
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            activity,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    adStatus = AdStatus.Loaded
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    appOpenAd = null
                    adStatus = AdStatus.Failed(error = p0.message)
                }
            }
        )
    }

    fun show(
        onActionAfterAdShow: ((Boolean) -> Unit)? = null
    ) {
        appOpenAd?.apply {
            if (activity.isFinishing || activity.isDestroyed) {
                onActionAfterAdShow?.invoke(false)
                return
            }
            val baseActivity = if (activity is BaseActivity<*>) {
                activity
            } else null
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    baseActivity?.hideLoadingView()
                    AdsManager.isFullScreenAdsShowing = false
                    AdsManager.lastTimeShowAds = System.currentTimeMillis()
                    appOpenAd = null
                    onActionAfterAdShow?.invoke(true)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    onActionAfterAdShow?.invoke(false)
                    appOpenAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    baseActivity?.showLoadingView()
                    AdsManager.isFullScreenAdsShowing = true
                    wasShown = true
                }
            }
            show(activity)
        }
    }

    fun wasShown() = wasShown

    fun getAdStatus() = adStatus
}