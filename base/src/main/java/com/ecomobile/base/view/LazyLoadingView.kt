package com.ecomobile.base.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.ecomobile.base.R
import com.ecomobile.base.databinding.ViewLazyLoadingBinding
import kotlinx.coroutines.NonCancellable.start

class LazyLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewLazyLoadingBinding.inflate(LayoutInflater.from(context), this, true)

    private var loadingColor: Int
    private var backgroundColor: Int
    private var initialAlpha: Float
    private var finalAlpha: Float
    private var cornerRadius: Float
    private var animator: ObjectAnimator? = null
    private var lzDuration: Int = 1000

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LazyLoadingView, defStyleAttr, 0).apply {
            loadingColor = getColor(R.styleable.LazyLoadingView_lzl_primary_color, Color.parseColor("#BEC3F0"))
            backgroundColor = getColor(R.styleable.LazyLoadingView_lzl_background_color, Color.parseColor("#E9EEFF"))
            initialAlpha = getFloat(R.styleable.LazyLoadingView_lzl_start_alpha, 1f)
            finalAlpha = getFloat(R.styleable.LazyLoadingView_lzl_end_alpha, 0f)
            cornerRadius = getDimension(R.styleable.LazyLoadingView_lzl_radius, 0f)
            lzDuration = getInt(R.styleable.LazyLoadingView_lzl_duration, 1000)
        }
        setupView()
    }

    private fun setupView() {
        binding.card.apply {
            radius = cornerRadius
            setCardBackgroundColor(loadingColor)
        }
        binding.container.setBackgroundColor(backgroundColor)
        startAlphaAnimation()
    }

    fun startAlphaAnimation() {
        animator = ObjectAnimator.ofFloat(binding.card, View.ALPHA, initialAlpha, finalAlpha).apply {
            duration = lzDuration.toLong()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    fun setBackground(color: Int) {
        binding.container.setBackgroundColor(color)
    }

    fun setColor(color: Int) {
        binding.card.setCardBackgroundColor(color)
    }

    fun startAnimation() {
        animator?.start()
    }

    fun stopAnimation() {
        animator?.cancel()
    }
}