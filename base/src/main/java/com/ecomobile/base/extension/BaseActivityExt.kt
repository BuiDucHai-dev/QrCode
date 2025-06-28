package com.ecomobile.base.extension

import android.annotation.SuppressLint
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ecomobile.base.BaseActivity
import com.ecomobile.base.R

fun BaseActivity<*>.isActive() = !isFinishing && !isDestroyed

fun BaseActivity<*>.consumeSystemBars(
    callback: ((statusBarHeight: Int, bottomBarHeight: Int) -> Unit)? = null
) {
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val bottomBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        if (allowPadding() && binding.root is ViewGroup && binding.root.paddingTop + binding.root.paddingBottom != statusBarHeight + bottomBarHeight) {
            findViewById<View>(R.id.topView)?.let {
                if (it.height == 0) {
                    it.layoutParams.height = statusBarHeight
                }
                it.requestLayout()
            }
            findViewById<View>(R.id.bottomView)?.let {
                if (bottomBarHidden) {
                    it.layoutParams.height = 1
                } else {
                    it.layoutParams.height = bottomBarHeight
                }
                it.requestLayout()
            }
        }
        callback?.invoke(statusBarHeight, bottomBarHeight)
        insetsLiveData.postValue(insets)
        WindowInsetsCompat.CONSUMED
    }
}

@SuppressLint("SourceLockedOrientationActivity")
fun BaseActivity<*>.lockPortraitScreen() {
    if (!isLockPortraitScreen()) return
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun BaseActivity<*>.hideBottomNavigationBar() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.navigationBars())
        bottomBarHidden = true
    }
}

fun BaseActivity<*>.showLoadingView() {
    runOnUiThread {
        layoutLoadingBinding.root.layoutParams = binding.root.layoutParams
        (binding.root as ViewGroup).apply {
            removeView(layoutLoadingBinding.root)
            addView(layoutLoadingBinding.root)
        }
    }
}

fun BaseActivity<*>.hideLoadingView() {
    runOnUiThread {
        delay { (binding.root as ViewGroup).removeView(layoutLoadingBinding.root) }
    }
}

fun BaseActivity<*>.startActivityWithTransition(intent: Intent?) {
    if (intent == null) {
        Log.d("BaseActivity", "Intent is null, cannot start activity")
        return
    }
    try {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.start_slide_enter, R.anim.start_slide_exit)
        startActivity(intent, options.toBundle())
    } catch (e: Exception) {
        Log.e("BaseActivity", "Failed to start activity with animation: ${e.message}")
        startActivity(intent)
    }
}

fun BaseActivity<*>.finisActivityWithTransition() {
    try {
        finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.back_slide_enter, R.anim.back_slide_exit)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.back_slide_enter, R.anim.back_slide_exit)
        }
    } catch (e: Exception) {
        Log.e("BaseActivity", "Failed to finish activity with animation: ${e.message}")
        finish()
    }
}