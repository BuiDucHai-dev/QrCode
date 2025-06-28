package com.eco.ads.ads.banner

import android.content.res.Resources
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.eco.ads.banner.EcoBannerAdListener
import com.eco.ads.banner.EcoBannerAdView
import com.ecomobile.base.extension.gone
import com.ecomobile.base.extension.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class BannerAdManager(
    private val activity: AppCompatActivity,
    private val adUnitId: String,
    private val ecoCrossId: String = "",
    private val container: FrameLayout
) {

    var adView: AdView? = null
    var ecoBanner: EcoBannerAdView? = null

    fun loadAd(callback: BannerAdsListener? = null) {
        adView = AdView(activity)
        adView?.adUnitId = adUnitId
        adView?.setAdSize(getAdSizeInline(container.height))
        val adRequest = AdRequest.Builder().build()
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                container.removeAllViews()
                container.addView(adView)
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                lp.gravity = Gravity.CENTER
                adView?.layoutParams = lp
                callback?.onBannerAdLoaded()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                ecoBanner = EcoBannerAdView
                    .Builder(activity)
                    .setAdId(ecoCrossId)
                    .setBannerAdsListener(object : EcoBannerAdListener() {
                        override fun onAdFailedToLoad(error: String) {
                            super.onAdFailedToLoad(error)
                        }
                    })
                    .build()
                ecoBanner?.load(container)
                callback?.onBannerAdFailedToLoad(p0)
            }
        }
        container.post {
            adView?.loadAd(adRequest)
        }
    }

    fun loadInlineAd(crossBannerLayout: FrameLayout? = null) {
        val adSize = AdSize.getInlineAdaptiveBannerAdSize(350, 500)
        adView = AdView(activity)
        adView?.adUnitId = adUnitId
        adView?.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                container.removeAllViews()
                container.addView(adView)
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                lp.gravity = Gravity.CENTER
                adView?.layoutParams = lp
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                crossBannerLayout?.let { layout ->
                    container.gone()
                    layout.visible()
                    ecoBanner = EcoBannerAdView
                        .Builder(activity)
                        .setAdId(ecoCrossId)
                        .setBannerAdsListener(object : EcoBannerAdListener() {
                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                container.gone()
                                layout.visible()
                            }
                            override fun onAdFailedToLoad(error: String) {
                                super.onAdFailedToLoad(error)
                            }
                        })
                        .build()
                    ecoBanner?.load(layout)
                }
            }
        }
        adView?.loadAd(adRequest)
    }

    fun loadAdaptiveBanner(crossBannerLayout: FrameLayout? = null) {
        adView = AdView(activity)
        adView?.adUnitId = adUnitId
        adView?.setAdSize(getAdaptiveAdSize())
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                container.removeAllViews()
                container.addView(adView)
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                lp.gravity = Gravity.CENTER
                adView?.layoutParams = lp
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                crossBannerLayout?.let { layout ->
                    container.gone()
                    layout.visible()
                    ecoBanner = EcoBannerAdView
                        .Builder(activity)
                        .setAdId(ecoCrossId)
                        .setBannerAdsListener(object : EcoBannerAdListener() {
                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                container.gone()
                                layout.visible()
                            }
                            override fun onAdFailedToLoad(error: String) {
                                super.onAdFailedToLoad(error)
                            }
                        })
                        .build()
                    ecoBanner?.load(layout)
                }
            }
        }
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    private fun getAdSizeInline(maxHeightDp: Int): AdSize {
        val displayMetrics = Resources.getSystem().displayMetrics
        val density = displayMetrics.density
        val screenWidthPx = displayMetrics.widthPixels

        val adWidthDp = (screenWidthPx / density).toInt()
        val adMaxHeightDp = maxHeightDp.coerceAtLeast(0)
        return AdSize.getInlineAdaptiveBannerAdSize(adWidthDp, adMaxHeightDp)
    }

    private fun getAdaptiveAdSize(): AdSize {
        val displayMetrics = activity.resources.displayMetrics
        val adWidthPixels = displayMetrics.widthPixels.toFloat()
        val adHeightPixels = displayMetrics.heightPixels.toFloat() / 5
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        val adHeight = (adHeightPixels / density).toInt()
        return AdSize.getInlineAdaptiveBannerAdSize(adWidth, adHeight)
    }

    fun release() {
        adView = null
        ecoBanner = null
    }

    abstract class BannerAdsListener {
        open fun onBannerAdLoaded() {}
        open fun onBannerAdFailedToLoad(error: LoadAdError) {}
    }

}