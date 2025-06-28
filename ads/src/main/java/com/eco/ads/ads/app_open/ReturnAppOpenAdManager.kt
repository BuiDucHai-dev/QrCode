package com.eco.ads.ads.app_open

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.eco.ads.AdsManager
import com.eco.ads.EcoFullScreenCallback
import com.eco.ads.EcoInfoAdsCallback
import com.eco.ads.ads.AdUnitId
import com.eco.ads.ads.EcoCrossAdId
import com.eco.ads.appopen.EcoAppOpenAd
import com.eco.ads.appopen.EcoAppOpenAdActivity
import com.eco.ads.appopen.EcoAppOpenAdListener
import com.ecomobile.base.BaseActivity
import com.ecomobile.base.extension.hideLoadingView
import com.ecomobile.base.extension.isActive
import com.ecomobile.base.extension.runDelay
import com.ecomobile.base.extension.showLoadingView
import com.ecomobile.base.storage_data.AppStates
import com.ecomobile.firebase.remote_config.RemoteConfigManager
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class ReturnAppOpenAdManager(private val application: Application): Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0L
    private var currentActivity: Activity? = null
    private var isCompleted = true
    private var isShowingAd = false

    private var isCrossLoaded: Boolean = false
    private var isCrossLoadFail: Boolean = false
    private var isCrossLoadComplete: Boolean = true

    private val ecoAppOpen by lazy {
        EcoAppOpenAd
            .Builder(application)
            .setAdId(EcoCrossAdId.ECO_CROSS_OPEN_APP_RETURN_APP)
            .setAppOpenAdListener(object : EcoAppOpenAdListener() {
                override fun onAdLoaded(ecoAppOpenAd: EcoAppOpenAd) {
                    super.onAdLoaded(ecoAppOpenAd)
                    isCrossLoaded = true
                    isCrossLoadComplete = true
                }

                override fun onAdFailToLoad(error: String) {
                    super.onAdFailToLoad(error)
                    isCrossLoadFail = true
                    isCrossLoadComplete = true
                }
            })
            .setColorBackgroundButtonSkip("#2597F5")
            .setColorNameAppButtonSkip("#FFFFFF")
            .setColorTextContinueToApp("#92CBFA")
            .build()
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun loadAd() {
        if (AppStates.isPurchase) return
        isCompleted = false
        loadEcoCross()
        loadAdMob()
    }

    fun getCurrentActivity() = currentActivity

    private fun loadEcoCross() {
        if (isCrossLoaded) return
        ecoAppOpen.load(application)
    }

    private fun loadAdMob() {
        if (isAdmobAvailable()) return
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            AdUnitId.ADMOB_APP_OPEN_RETURN_APP,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    isCompleted = true
                    appOpenAd = p0
                    loadTime = System.currentTimeMillis()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    isCompleted = true
                }
            }
        )
    }

    fun showAdIfAvailable() {
        if (AppStates.isPurchase) return
        if (isInCoolTime()) return
        if (currentActivity is AdActivity) return
        if (currentActivity !is BaseActivity<*>) return
        currentActivity?.apply {
            (currentActivity as BaseActivity<*>).apply {
                if (isActive()) {
                    showLoadingView()
                }
            }
            if (isAdmobAvailable()) {
                showAdMob(this)
            }
            else if (isCrossLoaded) {
                showCross(this)
            }
            else {
                if (currentActivity is BaseActivity<*>) {
                    (currentActivity as BaseActivity<*>).apply {
                        if (isActive()) {
                            hideLoadingView()
                        }
                    }
                }
                loadAd()
            }
        }
    }

    private fun showAdMob(activity: Activity) {
        appOpenAd?.apply {
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    AdsManager.isFullScreenAdsShowing = true
                    isShowingAd = true
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    AdsManager.isFullScreenAdsShowing = false
                    AdsManager.lastTimeShowAds = System.currentTimeMillis()
                    appOpenAd = null
                    isShowingAd = false
                    if (currentActivity is BaseActivity<*>) {
                        (currentActivity as BaseActivity<*>).hideLoadingView()
                    }
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    if (p0.code == 3) {
                        runDelay(200L) {
                            appOpenAd?.show(activity)
                        }
                    } else {
                        appOpenAd = null
                        isShowingAd = false
//                        showCross(activity)
                    }
                }
            }
        }?.show(activity)
    }

    private fun showCross(activity: Activity) {
        ecoAppOpen.setFullScreenCallback(object : EcoFullScreenCallback() {
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                isShowingAd = true
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                AdsManager.lastTimeShowAds = System.currentTimeMillis()
                isCrossLoaded = false
                isShowingAd = false
                if (currentActivity is BaseActivity<*>) {
                    (currentActivity as BaseActivity<*>).apply {
                        if (isActive()) {
                            hideLoadingView()
                        }
                    }
                }
            }

            override fun onAdFailedToShowFullScreenContent(error: String) {
                super.onAdFailedToShowFullScreenContent(error)
                isCrossLoaded = false
                isShowingAd = false
                if (currentActivity is BaseActivity<*>) {
                    (currentActivity as BaseActivity<*>).apply {
                        if (isActive()) {
                            hideLoadingView()
                        }
                    }
                }
            }
        })
        ecoAppOpen.setInfoAdsCallback(object : EcoInfoAdsCallback() {
            override fun onRemoveAllAdsClicked() {
                super.onRemoveAllAdsClicked()
//                if (!activity.isFinishing && !activity.isDestroyed) { }
            }
        })
        ecoAppOpen.show(activity)
    }

    private fun isAdmobAvailable(): Boolean {
        if (appOpenAd == null) return false
        val current = System.currentTimeMillis()
        return current - loadTime < 4 * 60 * 60 * 1000
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        if (activity !is AdActivity && activity !is EcoAppOpenAdActivity) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity !is AdActivity && activity !is EcoAppOpenAdActivity) {
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        if (activity !is AdActivity && activity !is EcoAppOpenAdActivity) {
            currentActivity = activity
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
//        currentActivity = null
    }

    private fun isInCoolTime(): Boolean {
        return System.currentTimeMillis() - AdsManager.lastTimeShowAds < RemoteConfigManager.getCoolOfTime()
    }
}