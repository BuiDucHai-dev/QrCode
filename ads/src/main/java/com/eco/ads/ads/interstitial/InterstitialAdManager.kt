package com.eco.ads.ads.interstitial

import android.app.Activity
import com.eco.ads.AdsManager
import com.eco.ads.DialogCoverInterstitial
import com.eco.ads.EcoFullScreenCallback
import com.eco.ads.EcoInfoAdsCallback
import com.eco.ads.ads.AdStatus
import com.eco.ads.interstitial.EcoInterstitialAd
import com.eco.ads.interstitial.EcoInterstitialAdListener
import com.ecomobile.base.extension.runDelay
import com.ecomobile.base.storage_data.AppStates
import com.ecomobile.firebase.remote_config.RemoteConfigManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InterstitialAdManager(
    private val activity: Activity,
    private val adUnitId: String,
    private val ecoCrossId: String = ""
) {

    private var interstitialAd: InterstitialAd? = null
    private var ecoInterAd: EcoInterstitialAd? = null
    private var coverDialog: WeakReference<DialogCoverInterstitial>? = null

    private var scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private var adStatus: AdStatus = AdStatus.None

    fun loadAd() {
        if (adStatus == AdStatus.Loading || adStatus == AdStatus.Loaded) return
        adStatus = AdStatus.Loading
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    adStatus = AdStatus.Loaded
                }

                override fun onAdFailedToLoad(e: LoadAdError) {
                    interstitialAd = null
                    loadEcoCross()
                }
            }
        )
    }

    fun release() {
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
        ecoInterAd = null
        adStatus = AdStatus.None
        scope.cancel()
        job?.cancel()
    }

    private fun loadEcoCross() {
        EcoInterstitialAd.Builder(activity)
            .setAdId(ecoCrossId)
            .build().apply {
                setInterstitialAdListener(object : EcoInterstitialAdListener() {
                    override fun onAdLoaded(ecoInterstitialAd: EcoInterstitialAd) {
                        super.onAdLoaded(ecoInterstitialAd)
                        ecoInterAd = ecoInterstitialAd
                        adStatus = AdStatus.Loaded
                    }

                    override fun onAdFailToLoad(error: String) {
                        super.onAdFailToLoad(error)
                        ecoInterAd = null
                        adStatus = AdStatus.Failed(error = error)
                    }
                })
                load(activity)
            }
    }

    fun show(
        delayDialogDuration: Int = 3,
        onActionAfterAdShow: ((Boolean) -> Unit)? = null
    ) {
        if (isInCoolTime() || AppStates.isPurchase || adStatus is AdStatus.Failed || adStatus == AdStatus.None) {
            onActionAfterAdShow?.invoke(false)
            return
        }
        if (activity.isFinishing || activity.isDestroyed) {
            onActionAfterAdShow?.invoke(false)
            return
        }
        if (delayDialogDuration != 0) {
            coverDialog = WeakReference(DialogCoverInterstitial(activity)).apply { get()?.show() }
        }
        var wasShow = false
        scope = CoroutineScope(Dispatchers.IO)
        job = scope.launch {
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < delayDialogDuration * 1000) {
                if (!wasShow) {
                    when (adStatus) {
                        AdStatus.Loaded -> {
                            wasShow = true
                            withContext(Dispatchers.Main) {
                                interstitialAd?.let { ad ->
                                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                        override fun onAdShowedFullScreenContent() {
                                            super.onAdShowedFullScreenContent()
                                            AdsManager.isFullScreenAdsShowing = true
                                        }

                                        override fun onAdDismissedFullScreenContent() {
                                            super.onAdDismissedFullScreenContent()
                                            release()
                                            AdsManager.isFullScreenAdsShowing = false
                                            AdsManager.lastTimeShowAds = System.currentTimeMillis()
                                            dismissCoverDialog()
                                            onActionAfterAdShow?.invoke(true)
                                        }

                                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                            super.onAdFailedToShowFullScreenContent(p0)
                                            showEcoInter(onActionAfterAdShow)
                                        }
                                    }
                                    ad.show(activity)
                                } ?: run {
                                    showEcoInter(onActionAfterAdShow)
                                }
                            }
                        }
                        else -> {}
                    }
                    delay(100L)
                }
            }
            runDelay(200L) {
                if (!wasShow) {
                    showEcoInter(onActionAfterAdShow)
                }
            }
        }
    }

    private fun showEcoInter(
        onActionAfterAdShow: ((Boolean) -> Unit)? = null
    ) {
        ecoInterAd?.let { ad ->
            ad.setFullScreenCallback(object : EcoFullScreenCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    AdsManager.isFullScreenAdsShowing = true
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    AdsManager.isFullScreenAdsShowing = false
                    AdsManager.lastTimeShowAds = System.currentTimeMillis()
                    release()
                    dismissCoverDialog()
                    onActionAfterAdShow?.invoke(true)
                }

                override fun onAdFailedToShowFullScreenContent(error: String) {
                    super.onAdFailedToShowFullScreenContent(error)
                    release()
                    dismissCoverDialog()
                    onActionAfterAdShow?.invoke(false)
                }
            })
            ad.setInfoAdCallback(object : EcoInfoAdsCallback() {

            })
            ad.show(activity)
        } ?: run {
            CoroutineScope(Dispatchers.Main).launch {
                release()
                dismissCoverDialog()
                onActionAfterAdShow?.invoke(false)
            }
        }
    }

    private fun dismissCoverDialog() {
        if (activity.isFinishing || activity.isDestroyed) return
        coverDialog?.get()?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        coverDialog?.clear()
    }

    private fun isInCoolTime(): Boolean {
        return System.currentTimeMillis() - AdsManager.lastTimeShowAds < RemoteConfigManager.getCoolOfTime()
    }
}