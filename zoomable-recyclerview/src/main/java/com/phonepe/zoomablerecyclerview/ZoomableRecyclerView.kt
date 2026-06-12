package com.phonepe.zoomablerecyclerview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.OverScroller
import androidx.recyclerview.widget.RecyclerView

class ZoomableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    /** Minimum zoom level. Default: 1.0 */
    var minScale: Float = DEFAULT_MIN_SCALE
        set(value) { field = value.coerceAtLeast(0.1f) }

    /** Maximum zoom level reachable via pinch. Default: 3.0 */
    var maxScale: Float = DEFAULT_MAX_SCALE
        set(value) { field = value.coerceAtLeast(minScale) }

    /** Zoom level jumped to on double-tap. Default: 2.0 */
    var doubleTapZoomScale: Float = DEFAULT_DOUBLE_TAP_ZOOM_SCALE

    /** Duration of the double-tap zoom animation in ms. Default: 300 */
    var zoomAnimationDuration: Long = DEFAULT_ZOOM_ANIMATION_DURATION

    /** Master switch — when false the view behaves like a plain RecyclerView. */
    var isZoomEnabled: Boolean = true

    /** When false, double-tap zoom is disabled while pinch still works. */
    var isDoubleTapZoomEnabled: Boolean = true

    /** When false, fling while zoomed is disabled. */
    var isFlingEnabled: Boolean = true

    var currentScale: Float = 1f
        private set

    private val matrix = Matrix()
    private val drawRect = RectF()
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())
    private val scroller = OverScroller(context)

    private val flingRunnable = Runnable { runFling() }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false

    private var viewWidth = 0f
    private var viewHeight = 0f

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ZoomableRecyclerView, defStyleAttr, 0)
            try {
                minScale = ta.getFloat(R.styleable.ZoomableRecyclerView_zrv_minScale, DEFAULT_MIN_SCALE)
                maxScale = ta.getFloat(R.styleable.ZoomableRecyclerView_zrv_maxScale, DEFAULT_MAX_SCALE)
                doubleTapZoomScale = ta.getFloat(R.styleable.ZoomableRecyclerView_zrv_doubleTapZoomScale, DEFAULT_DOUBLE_TAP_ZOOM_SCALE)
                zoomAnimationDuration = ta.getInt(R.styleable.ZoomableRecyclerView_zrv_zoomAnimationDuration, DEFAULT_ZOOM_ANIMATION_DURATION.toInt()).toLong()
                isZoomEnabled = ta.getBoolean(R.styleable.ZoomableRecyclerView_zrv_zoomEnabled, true)
                isDoubleTapZoomEnabled = ta.getBoolean(R.styleable.ZoomableRecyclerView_zrv_doubleTapZoomEnabled, true)
                isFlingEnabled = ta.getBoolean(R.styleable.ZoomableRecyclerView_zrv_flingEnabled, true)
            } finally {
                ta.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        viewHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.concat(matrix)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isZoomEnabled) return super.onTouchEvent(event)

        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY
                if (currentScale > 1f) {
                    matrix.postTranslate(dx, dy)
                    constrainTranslation()
                    invalidate()
                    isDragging = true
                }
                lastTouchX = event.x
                lastTouchY = event.y
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val index = event.actionIndex
                val newPointerIndex = if (index == 0) 1 else 0
                if (event.pointerCount > 1) {
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isDragging && currentScale == 1f) {
                    return super.onTouchEvent(event)
                }
            }
        }
        super.onTouchEvent(event)
        return true
    }

    private fun runFling() {
        if (scroller.computeScrollOffset()) {
            val dx = scroller.currX.toFloat() - drawRect.left
            val dy = scroller.currY.toFloat() - drawRect.top
            matrix.postTranslate(dx, dy)
            constrainTranslation()
            invalidate()
            postOnAnimation(flingRunnable)
        }
    }

    private fun constrainTranslation() {
        drawRect.set(0f, 0f, width.toFloat(), height.toFloat())
        matrix.mapRect(drawRect)

        val dx = when {
            drawRect.width() <= viewWidth -> (viewWidth - drawRect.width()) / 2f - drawRect.left
            drawRect.left > 0 -> -drawRect.left
            drawRect.right < viewWidth -> viewWidth - drawRect.right
            else -> 0f
        }

        val dy = when {
            drawRect.height() <= viewHeight -> (viewHeight - drawRect.height()) / 2f - drawRect.top
            drawRect.top > 0 -> -drawRect.top
            drawRect.bottom < viewHeight -> viewHeight - drawRect.bottom
            else -> 0f
        }

        matrix.postTranslate(dx, dy)
    }

    private fun animateZoom(targetScale: Float, focusX: Float, focusY: Float) {
        val clamped = targetScale.coerceIn(minScale, maxScale)
        ValueAnimator.ofFloat(currentScale, clamped).apply {
            duration = zoomAnimationDuration
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                val factor = scale / currentScale
                matrix.postScale(factor, factor, focusX, focusY)
                currentScale = scale
                constrainTranslation()
                invalidate()
            }
            start()
        }
    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = currentScale * scaleFactor
            if (newScale in minScale..maxScale) {
                matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                currentScale = newScale
                constrainTranslation()
                invalidate()
            }
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (!isDoubleTapZoomEnabled) return false
            val targetScale = if (currentScale > minScale) minScale else doubleTapZoomScale.coerceIn(minScale, maxScale)
            animateZoom(targetScale, e.x, e.y)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (isFlingEnabled && currentScale > 1f) {
                drawRect.set(0f, 0f, width.toFloat(), height.toFloat())
                matrix.mapRect(drawRect)
                scroller.fling(
                    drawRect.left.toInt(), drawRect.top.toInt(),
                    velocityX.toInt(), velocityY.toInt(),
                    Int.MIN_VALUE, Int.MAX_VALUE,
                    Int.MIN_VALUE, Int.MAX_VALUE
                )
                postOnAnimation(flingRunnable)
                return true
            }
            return false
        }
    }

    companion object {
        const val DEFAULT_MIN_SCALE = 1f
        const val DEFAULT_MAX_SCALE = 3f
        const val DEFAULT_DOUBLE_TAP_ZOOM_SCALE = 2f
        const val DEFAULT_ZOOM_ANIMATION_DURATION = 300L
    }
}
