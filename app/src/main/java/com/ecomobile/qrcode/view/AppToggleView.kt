package com.ecomobile.qrcode.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.ecomobile.base.extension.click
import com.ecomobile.qrcode.R
import com.ecomobile.qrcode.databinding.AppToggleViewBinding

class AppToggleView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_PERCENT = 0.9f
    }

    private val binding = AppToggleViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var thumbColor: Int = Color.WHITE
    private var thumbColorCheck: Int = Color.RED
    private var trackColor: Int = Color.BLUE
    private var trackColorCheck: Int = Color.BLUE
    private var thumbPercent: Float = DEFAULT_PERCENT

    var isCheck = false
    var onCheckedChangeListener: ((Boolean) -> Unit)? = null
    var onCustomClickListener: (() -> Unit)? = null

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AppToggleView)
            thumbColor = typedArray.getColor(R.styleable.AppToggleView_app_tg_thumbColor, ContextCompat.getColor(context, R.color.color_919FAF))
            thumbColorCheck = typedArray.getColor(R.styleable.AppToggleView_app_tg_thumbColorCheck, ContextCompat.getColor(context, R.color.color_206FE6))
            trackColor = typedArray.getColor(R.styleable.AppToggleView_app_tg_trackColor, ContextCompat.getColor(context, R.color.white))
            trackColorCheck = typedArray.getColor(R.styleable.AppToggleView_app_tg_trackColorCheck, ContextCompat.getColor(context, R.color.white))
            thumbPercent = typedArray.getFloat(R.styleable.AppToggleView_app_tg_thumbPercent, DEFAULT_PERCENT)
            isCheck = typedArray.getBoolean(R.styleable.AppToggleView_app_tg_isCheck, false)
            if (thumbPercent < 0 || thumbPercent > 1) {
                thumbPercent = DEFAULT_PERCENT
            }
            setupView()
            typedArray.recycle()
        }
        binding.root.click {
            onCustomClickListener?.invoke()
        }
    }

    private fun setupView() {
        binding.track.setCardBackgroundColor(trackColor)
        binding.thumb.setCardBackgroundColor(thumbColor)
        binding.thumb.scaleX = thumbPercent
        binding.thumb.scaleY = thumbPercent
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraint)
        if (isCheck) {
            constraintSet.setHorizontalBias(R.id.thumb, 1f)
            binding.thumb.animateColor(thumbColor, thumbColorCheck)
            binding.track.animateColor(trackColor, trackColorCheck)
        } else {
            constraintSet.setHorizontalBias(R.id.thumb, 0f)
            binding.thumb.animateColor(thumbColorCheck, thumbColor)
            binding.track.animateColor(trackColorCheck, trackColor)
        }
        TransitionManager.beginDelayedTransition(binding.constraint)
        constraintSet.applyTo(binding.constraint)
        onCheckedChangeListener?.invoke(isCheck)
    }

    fun setChecked(value: Boolean) {
        if (isCheck == value) return
        isCheck = value
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraint)
        if (isCheck) {
            constraintSet.setHorizontalBias(R.id.thumb, 1f)
            binding.thumb.animateColor(thumbColor, thumbColorCheck)
            binding.track.animateColor(trackColor, trackColorCheck)
        } else {
            constraintSet.setHorizontalBias(R.id.thumb, 0f)
            binding.thumb.animateColor(thumbColorCheck, thumbColor)
            binding.track.animateColor(trackColorCheck, trackColor)
        }
        TransitionManager.beginDelayedTransition(binding.constraint)
        constraintSet.applyTo(binding.constraint)
        onCheckedChangeListener?.invoke(isCheck)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun CardView.animateColor(startColor: Int, endColor: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
        colorAnimation.duration = 300
        colorAnimation.addUpdateListener { animator ->
            setCardBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }
}

