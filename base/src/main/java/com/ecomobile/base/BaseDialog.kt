package com.ecomobile.base

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ecomobile.base.extension.isNavigationBarShown

abstract class BaseDialog<B : ViewDataBinding>(context: Context) : AlertDialog(context) {

    lateinit var binding: B

    protected abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes?.windowAnimations = R.style.DialogZoomAnimation
            setGravity(Gravity.CENTER)
        }

        binding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false)
        setContentView(binding.root)
        hideBottomNavigationBar(binding.root)
    }

    private fun hideBottomNavigationBar(viewRoot: View) {
        ViewCompat.setOnApplyWindowInsetsListener(viewRoot) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (!context.isNavigationBarShown()) {
                view.setPadding(
                    systemBarsInsets.left,
                    systemBarsInsets.top,
                    systemBarsInsets.right,
                    0
                )
            }
            insets
        }
        window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, window.decorView).apply {
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }
}