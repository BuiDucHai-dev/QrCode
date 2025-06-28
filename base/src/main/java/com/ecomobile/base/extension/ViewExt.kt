package com.ecomobile.base.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.BaseInterpolator
import android.view.animation.DecelerateInterpolator
import com.ecomobile.base.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        val l = left ?: p.leftMargin
        val t = top ?: p.topMargin
        val r = right ?: p.rightMargin
        val b = bottom ?: p.bottomMargin
        p.setMargins(l, t, r, b)
        layoutParams = p
        requestLayout()
    }
}

fun View.click(animation: Boolean = false, result: ((View) -> Unit)) {
    setOnClickListener {
        isEnabled = false
        if (animation) {
            animationButton {
                result(this)
            }
        } else {
            result(this)
        }
        kotlin.runCatching { postDelayed({ isEnabled = true }, 300) }
    }
}

fun View.animationButton(result: () -> Unit) {
    clearAnimation()
    val ani = AnimationUtils.loadAnimation(context, R.anim.ani_press_in)
    ani.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            CoroutineScope(Dispatchers.Main).launch {
                result()
            }
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    })
    startAnimation(ani)
}

fun View.rotate(from: Float, to: Float, dur: Long = 200L) {
    val rotate = ObjectAnimator.ofFloat(this, View.ROTATION, from, to)
    AnimatorSet().apply {
        playTogether(rotate)
        duration = dur
        interpolator = DecelerateInterpolator()
    }.start()
}

fun View.slideDown(isShow: Boolean = false, duration: Long = 200L, action: () -> Unit = {}) {
    clearAnimation()
    visible()
    val startY = if (isShow) -height.toFloat() else 0f
    val endY = if (isShow) 0f else height.toFloat()
    val transitionY = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, startY, endY).apply {
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
    }
    AnimatorSet().apply {
        play(transitionY)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                action()
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                action()
            }
        })
        start()
    }
}

fun View.slideUp(isShow: Boolean = false, duration: Long = 200L, action: () -> Unit = {}) {
    clearAnimation()
    visible()
    val startY = if (isShow) height.toFloat() else 0f
    val endY = if (isShow) 0f else -height.toFloat()
    val transitionY = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, startY, endY).apply {
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
    }
    AnimatorSet().apply {
        play(transitionY)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                action()
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                action()
            }
        })
        start()
    }
}

fun View.slideLeft(isShow: Boolean = false, duration: Long = 200L, interpolator: BaseInterpolator = AccelerateDecelerateInterpolator(), action: () -> Unit = {}) {
    clearAnimation()
    visible()
    val startY = if (isShow) width.toFloat() else 0f
    val endY = if (isShow) 0f else -width.toFloat()
    val transitionY = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, startY, endY).apply {
        this.duration = duration
        this.interpolator = interpolator
    }
    AnimatorSet().apply {
        play(transitionY)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                action()
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                action()
            }
        })
        start()
    }
}