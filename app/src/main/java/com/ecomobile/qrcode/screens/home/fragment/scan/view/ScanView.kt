package com.ecomobile.qrcode.screens.home.fragment.scan.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min
import android.util.Log
import androidx.core.content.withStyledAttributes
import com.ecomobile.qrcode.R

class ScanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private enum class ScanState { IDLE, SCANNING, CAPTURING }

    private companion object {
        private const val SIZE_OF_ICON_CENTER = 0.3f
        private const val SIZE_OF_RECT_CENTER_START = 0.5f
        private const val SIZE_OF_RECT_CENTER_START_SHAZAM = 0.7f
        private const val SIZE_OF_RECT_CENTER_END = 0.75f
        private const val PERCENT_OF_CORNER_EDGE = 0.3f
        private const val EDGE_SCALE_FACTOR = 0.1f
        private const val OFFSET_CAPTURE = 20
        private const val DURATION_TRANSFORM_START = 1000L
        private const val DURATION_TRANSFORM_ZOOM_TO_POINT = 200L
        private const val DURATION_TRANSFORM_ZOOM_IN = 500L
        private const val DURATION_TRANSFORM_ZOOM_OUT = 500L
        private const val DURATION_TRANSFORM_BACK_TO_NULL = 300L
        private const val FOCUS_DURATION = 1000L
    }

    private val lock = Any()
    private var state = ScanState.IDLE
    private val hasPoint = AtomicBoolean(false)
    private var isCapturing = false
    private var isCameraAvailable = false

    private var backgroundColor: Int = Color.parseColor("#330038FF")
    private var scanColor: Int = Color.parseColor("#4DFFFFFF")
    private var highlightColor: Int = Color.parseColor("#FFB800")

    private val bgPaint = Paint().apply { color = backgroundColor }
    private val scanPaint = Paint().apply { color = scanColor }
    private val focusPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val path = Path()
    private val pathScan = Path()
    private var corners: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    private var callback: Callback? = null
    private lateinit var drawableTopLeft: Drawable
    private lateinit var drawableTopRight: Drawable
    private lateinit var drawableBottomLeft: Drawable
    private lateinit var drawableBottomRight: Drawable
    private lateinit var drawableIconCenter: Drawable
    private lateinit var areaTopLeft: Drawable
    private lateinit var areaTopRight: Drawable
    private lateinit var areaBottomLeft: Drawable
    private lateinit var areaBottomRight: Drawable

    private lateinit var animatorStart: ValueAnimator
    private lateinit var animatorZoomIn: ValueAnimator
    private lateinit var animatorZoomOut: ValueAnimator
    private lateinit var animatorZoomToPoint: ValueAnimator
    private lateinit var animatorBackWhenNull: ValueAnimator
    private lateinit var animatorScan: ValueAnimator

    private var focusPoint: Point? = null
    private val handler = Handler(Looper.getMainLooper())

    private var scrWidth: Int = 0
    private var scrHeight: Int = 0
    private var halfOfWidth: Int = 0
    private var halfOfHeight: Int = 0
    private var lengthOfStartEdge: Int = 0

    private val centerIconLocation = IntArray(4) // left, top, right, bottom
    private val topLeftIconLocation = IntArray(4)
    private val topRightIconLocation = IntArray(4)
    private val bottomLeftIconLocation = IntArray(4)
    private val bottomRightIconLocation = IntArray(4)
    private val topLeftArea = IntArray(4)
    private val topRightArea = IntArray(4)
    private val bottomLeftArea = IntArray(4)
    private val bottomRightArea = IntArray(4)
    private val cornerArea = IntArray(4)
    private var currentArea = IntArray(4)
    private var nextArea = IntArray(4)

    init {
        initAttributes(context, attrs)
        initPaints()
        initDrawables()
        configAnimations()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.ScanView) {
            backgroundColor = getColor(R.styleable.ScanView_backgroundColor, backgroundColor)
            scanColor = getColor(R.styleable.ScanView_scanColor, scanColor)
            highlightColor = getColor(R.styleable.ScanView_highlightColor, highlightColor)
        }
        bgPaint.color = backgroundColor
        scanPaint.color = scanColor
    }

    private fun initPaints() {
        bgPaint.color = backgroundColor
        scanPaint.color = scanColor
        focusPaint.color = Color.WHITE
        focusPaint.style = Paint.Style.STROKE
        focusPaint.strokeWidth = 3f
    }

    private fun initDrawables() {
        drawableTopLeft = ContextCompat.getDrawable(context, R.drawable.ic_scan_top_left)
            ?: throw IllegalStateException("Drawable ic_scan_top_left not found")
        drawableTopRight = ContextCompat.getDrawable(context, R.drawable.ic_scan_top_right)
            ?: throw IllegalStateException("Drawable ic_scan_top_right not found")
        drawableBottomLeft = ContextCompat.getDrawable(context, R.drawable.ic_scan_bottom_left)
            ?: throw IllegalStateException("Drawable ic_scan_bottom_left not found")
        drawableBottomRight = ContextCompat.getDrawable(context, R.drawable.ic_scan_bottom_right)
            ?: throw IllegalStateException("Drawable ic_scan_bottom_right not found")
        drawableIconCenter = ContextCompat.getDrawable(context, R.drawable.ic_scan_view_center)
            ?: throw IllegalStateException("Drawable ic_scan_view_center not found")
        areaTopLeft = drawableTopLeft
        areaTopRight = drawableTopRight
        areaBottomLeft = drawableBottomLeft
        areaBottomRight = drawableBottomRight
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isCameraAvailable = false
        state = ScanState.IDLE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scrWidth = if (h > w) w else h
        scrHeight = if (h > w) h else w
        halfOfWidth = scrWidth / 2
        halfOfHeight = scrHeight / 2
        lengthOfStartEdge = (EDGE_SCALE_FACTOR * scrWidth).toInt()
        updateCenterIconLocation()
        updateCornerLocations(SIZE_OF_RECT_CENTER_START, SIZE_OF_RECT_CENTER_START, 0f)
        currentArea = intArrayOf(
            (halfOfWidth - halfOfWidth * SIZE_OF_RECT_CENTER_END).toInt(),
            (halfOfHeight - halfOfWidth * SIZE_OF_RECT_CENTER_END).toInt(),
            (halfOfWidth + halfOfWidth * SIZE_OF_RECT_CENTER_END).toInt(),
            (halfOfHeight + halfOfWidth * SIZE_OF_RECT_CENTER_END).toInt()
        )
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun startScanning() {
        state = ScanState.SCANNING
        isCameraAvailable = true
        animatorZoomIn.pause()
        animatorZoomOut.pause()
        animatorStart.start()
        Log.d("ScanView", "Started scanning")
    }

    fun pause() {
        animatorStart.pause()
        animatorZoomIn.pause()
        animatorZoomOut.pause()
        animatorZoomToPoint.pause()
        animatorBackWhenNull.pause()
        animatorScan.pause()
        Log.d("ScanView", "Paused animations")
    }

    fun resume() {
        if (state == ScanState.SCANNING) {
            animatorZoomIn.start()
        }
        Log.d("ScanView", "Resumed animations")
    }

    fun reset() {
        isCameraAvailable = false
        hasPoint.set(false)
        pathScan.reset()
        animatorZoomIn.pause()
        animatorZoomOut.pause()
        animatorStart.pause()
        updateCornerLocations(SIZE_OF_RECT_CENTER_START, SIZE_OF_RECT_CENTER_START, 0f)
        state = ScanState.IDLE
        Log.d("ScanView", "Reset view")
    }

    private fun setDrawableColorFilter(@ColorInt color: Int) {
        areaTopLeft.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        areaTopRight.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        areaBottomLeft.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        areaBottomRight.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun updateCenterIconLocation() {
        val halfOfSize = (scrWidth * SIZE_OF_ICON_CENTER / 2).toInt()
        centerIconLocation[0] = halfOfWidth - halfOfSize
        centerIconLocation[1] = halfOfHeight - halfOfSize
        centerIconLocation[2] = halfOfWidth + halfOfSize
        centerIconLocation[3] = halfOfHeight + halfOfSize
    }

    private fun updateCornerLocations(start: Float, end: Float, progress: Float) {
        val dis = (scrWidth * (end - start) / 2 * progress + halfOfWidth * start).toInt()
        cornerArea[0] = halfOfWidth - dis
        cornerArea[1] = halfOfHeight - dis
        cornerArea[2] = halfOfWidth + dis
        cornerArea[3] = halfOfHeight + dis

        topLeftIconLocation[0] = cornerArea[0]
        topLeftIconLocation[1] = cornerArea[1]
        topLeftIconLocation[2] = cornerArea[0] + lengthOfStartEdge
        topLeftIconLocation[3] = cornerArea[1] + lengthOfStartEdge

        topRightIconLocation[0] = cornerArea[2] - lengthOfStartEdge
        topRightIconLocation[1] = cornerArea[1]
        topRightIconLocation[2] = cornerArea[2]
        topRightIconLocation[3] = cornerArea[1] + lengthOfStartEdge

        bottomLeftIconLocation[0] = cornerArea[0]
        bottomLeftIconLocation[1] = cornerArea[3] - lengthOfStartEdge
        bottomLeftIconLocation[2] = cornerArea[0] + lengthOfStartEdge
        bottomLeftIconLocation[3] = cornerArea[3]

        bottomRightIconLocation[0] = cornerArea[2] - lengthOfStartEdge
        bottomRightIconLocation[1] = cornerArea[3] - lengthOfStartEdge
        bottomRightIconLocation[2] = cornerArea[2]
        bottomRightIconLocation[3] = cornerArea[3]
    }

    private fun updateCurrentArea(progress: Float) {
        val left = (currentArea[0] + (nextArea[0] - currentArea[0]) * progress).toInt()
        val top = (currentArea[1] + (nextArea[1] - currentArea[1]) * progress).toInt()
        val right = (currentArea[2] + (nextArea[2] - currentArea[2]) * progress).toInt()
        val bottom = (currentArea[3] + (nextArea[3] - currentArea[3]) * progress).toInt()

        val distanceWidth = (PERCENT_OF_CORNER_EDGE * (right - left)).toInt()
        val distanceHeight = (PERCENT_OF_CORNER_EDGE * (bottom - top)).toInt()
        val dis = min(distanceWidth, distanceHeight)
        val offset = dis / 6

        topLeftArea[0] = left - offset
        topLeftArea[1] = top - offset
        topLeftArea[2] = left + dis - offset
        topLeftArea[3] = top + dis - offset

        topRightArea[0] = right - dis + offset
        topRightArea[1] = top - offset
        topRightArea[2] = right + offset
        topRightArea[3] = top + dis - offset

        bottomLeftArea[0] = left - offset
        bottomLeftArea[1] = bottom - dis + offset
        bottomLeftArea[2] = left + dis - offset
        bottomLeftArea[3] = bottom + offset

        bottomRightArea[0] = right - dis + offset
        bottomRightArea[1] = bottom - dis + offset
        bottomRightArea[2] = right + offset
        bottomRightArea[3] = bottom + offset

        corners = floatArrayOf(
            offset.toFloat(), offset.toFloat(),
            offset.toFloat(), offset.toFloat(),
            offset.toFloat(), offset.toFloat(),
            offset.toFloat(), offset.toFloat()
        )

        path.reset()
        path.addRoundRect(
            RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()),
            corners,
            Path.Direction.CW
        )
        path.close()

        if (currentArea != nextArea) {
            invalidate()
        }
    }

    private fun configAnimations() {
        animatorStart = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION_TRANSFORM_START
            interpolator = android.view.animation.PathInterpolator(0.05f, 0.7f, 0.1f, 1f)
            addUpdateListener {
                updateCornerLocations(
                    SIZE_OF_RECT_CENTER_START,
                    SIZE_OF_RECT_CENTER_END,
                    it.animatedValue as Float
                )
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    isCameraAvailable = true
                    animatorZoomIn.start()
                }

            })
        }

        animatorZoomIn = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION_TRANSFORM_ZOOM_IN
            addUpdateListener {
                updateCornerLocations(
                    SIZE_OF_RECT_CENTER_END,
                    SIZE_OF_RECT_CENTER_START_SHAZAM,
                    it.animatedValue as Float
                )
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    animatorZoomOut.start()
                }

            })
        }

        animatorZoomOut = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION_TRANSFORM_ZOOM_OUT
            addUpdateListener {
                updateCornerLocations(
                    SIZE_OF_RECT_CENTER_START_SHAZAM,
                    SIZE_OF_RECT_CENTER_END,
                    it.animatedValue as Float
                )
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    animatorZoomIn.start()
                }
            })
        }

        animatorZoomToPoint = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION_TRANSFORM_ZOOM_TO_POINT
            interpolator = android.view.animation.PathInterpolator(0.05f, 0.7f, 0.1f, 1f)
            addUpdateListener {
                updateCurrentArea(it.animatedValue as Float)
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    setDrawableColorFilter(highlightColor)
                }

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    currentArea = nextArea.copyOf()
                    updateCurrentArea(0f)
                    if (hasPoint.get()) {
                        if (isCapturing) {
                            endCapture()
                            isCapturing = false
                        } else {
                            animatorScan.start()
                            isCapturing = true
                        }
                    }
                }

            })
        }

        animatorBackWhenNull = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION_TRANSFORM_BACK_TO_NULL
            interpolator = android.view.animation.PathInterpolator(0.05f, 0.7f, 0.1f, 1f)
            addUpdateListener {
                updateCurrentArea(it.animatedValue as Float)
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    setDrawableColorFilter(Color.WHITE)
                }

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    hasPoint.set(false)
                    state = ScanState.SCANNING
                }
            })
        }

        animatorScan = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500L
            interpolator = android.view.animation.PathInterpolator(0.05f, 0.7f, 0.1f, 1f)
            addUpdateListener {
                pathScan.reset()
                pathScan.addRoundRect(
                    RectF(
                        nextArea[0].toFloat(),
                        nextArea[1].toFloat(),
                        nextArea[2].toFloat(),
                        (nextArea[1] + (nextArea[3] - nextArea[1]) * (it.animatedValue as Float)).toFloat()
                    ),
                    corners,
                    Path.Direction.CW
                )
                pathScan.close()
                invalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    if (hasPoint.get()) {
                        callback?.onEndAction()
                    }
                }
            })
        }
    }

    fun setPoint(imageWidth: Int, imageHeight: Int, rect: Rect) {
        val result = IntArray(4)
        result[0] = imageHeight - rect.bottom
        result[1] = rect.left
        result[2] = imageHeight - rect.top
        result[3] = rect.right
        result[0] = result[0] - (imageHeight - imageWidth * this.width / this.height) / 2
        result[2] = result[2] - (imageHeight - imageWidth * this.width / this.height) / 2
        for (i in result.indices) {
            result[i] = result[i] * this.height / imageWidth
        }
        setPoint(result)
    }

    fun setPoint(rect: IntArray?) {
        synchronized(lock) {
            if (rect != null) {
                hasPoint.set(true)
                state = ScanState.CAPTURING
                nextArea = rect.copyOf().apply {
                    this[0] += OFFSET_CAPTURE
                    this[1] += OFFSET_CAPTURE
                    this[2] -= OFFSET_CAPTURE
                    this[3] -= OFFSET_CAPTURE
                }
                animatorZoomToPoint.start()
                Log.d("ScanView", "Point set: ${rect.contentToString()}")
            } else {
                nextArea = cornerArea.copyOf()
                animatorBackWhenNull.start()
                Log.d("ScanView", "Point null, reverting to default area")
            }
        }
    }

    private fun endCapture() {
        nextArea = nextArea.copyOf().apply {
            this[0] -= OFFSET_CAPTURE
            this[1] -= OFFSET_CAPTURE
            this[2] += OFFSET_CAPTURE
            this[3] += OFFSET_CAPTURE
        }
        animatorZoomToPoint.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        setFocusPoint(event)
        return super.onTouchEvent(event)
    }

    private fun setFocusPoint(event: MotionEvent) {
        focusPoint = Point(event.x.toInt(), event.y.toInt())
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ focusPoint = null }, FOCUS_DURATION)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCenterView(canvas)
        drawCorners(canvas)
        drawFocusPoint(canvas)
    }

    private fun drawCenterView(canvas: Canvas) {
        if (isCameraAvailable) return
        setDrawableBounds(drawableIconCenter, centerIconLocation)
        drawableIconCenter.draw(canvas)
    }

    private fun drawCorners(canvas: Canvas) {
        if (hasPoint.get()) {
            canvas.drawPath(pathScan, scanPaint)
            setDrawableBounds(areaTopLeft, topLeftArea)
            setDrawableBounds(areaTopRight, topRightArea)
            setDrawableBounds(areaBottomLeft, bottomLeftArea)
            setDrawableBounds(areaBottomRight, bottomRightArea)
            areaTopLeft.draw(canvas)
            areaTopRight.draw(canvas)
            areaBottomLeft.draw(canvas)
            areaBottomRight.draw(canvas)
        } else {
            setDrawableBounds(drawableTopLeft, topLeftIconLocation)
            setDrawableBounds(drawableTopRight, topRightIconLocation)
            setDrawableBounds(drawableBottomLeft, bottomLeftIconLocation)
            setDrawableBounds(drawableBottomRight, bottomRightIconLocation)
            drawableTopLeft.draw(canvas)
            drawableTopRight.draw(canvas)
            drawableBottomLeft.draw(canvas)
            drawableBottomRight.draw(canvas)
        }
    }

    private fun setDrawableBounds(drawable: Drawable, bounds: IntArray) {
        drawable.setBounds(bounds[0], bounds[1], bounds[2], bounds[3])
    }

    private fun drawFocusPoint(canvas: Canvas) {
        focusPoint?.let {
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), 80f, focusPaint)
        }
    }

    interface Callback {
        fun onEndAction()
    }
}