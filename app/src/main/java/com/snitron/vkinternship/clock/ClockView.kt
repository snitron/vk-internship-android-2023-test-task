package com.snitron.vkinternship.clock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.snitron.vkinternship.R
import kotlinx.coroutines.*
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.*

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private val clockCoroutineScope = CoroutineScope(Dispatchers.Default)

        const val DEFAULT_SECOND_HAND_WIDTH = 6.0f
        const val DEFAULT_MINUTE_HAND_WIDTH = 14.0f
        const val DEFAULT_HOUR_HAND_WIDTH = 25.0f

        // Relatively to clock's radius (i.e. \frac{hand_radius}{clock_radius})
        const val DEFAULT_SECOND_HAND_RADIUS = 0.7f
        const val DEFAULT_MINUTE_HAND_RADIUS = 0.6f
        const val DEFAULT_HOUR_HAND_RADIUS = 0.3f

        const val DEFAULT_SECOND_HAND_COLOR = Color.BLACK
        const val DEFAULT_MINUTE_HAND_COLOR = Color.BLACK
        const val DEFAULT_HOUR_HAND_COLOR = Color.BLACK

        const val DEFAULT_BACKGROUND_COLOR = Color.WHITE
        const val DEFAULT_BORDER_COLOR = Color.BLACK
        const val DEFAULT_BORDER_WIDTH = 20.0f

        const val DEFAULT_DIVISION_COUNT = 60
        const val DEFAULT_DIVISION_COLOR = Color.BLACK
        const val DEFAULT_DIVISION_RADIUS = 10f
        const val DEFAULT_DIVISION_TEXT_COLOR = Color.BLACK
        const val DEFAULT_DIVISION_TEXT_SIZE = 60.0f

        //pred: 0 < redrawInterval < +\infty, measured in millis
        const val DEFAULT_REDRAW_INTERVAL = 100


        //pred: 0 < gapAngle < 2 * PI
        fun iterateOverCircle(gapAngle: Float, draw: (Int, Float) -> Unit) {
            var alpha = 0.0f
            var i = 0

            while (alpha < 2 * Math.PI) {
                draw(i, alpha)

                i++
                alpha += gapAngle
            }
        }

        fun drawHand(canvas: Canvas?, paint: Paint, width: Float, cx: Float, cy: Float, radius: Float, handRadius: Float, color: Int, unit: Int, maximum: Int) {
            val angle = unit.toDouble() / maximum * 2.0 * PI - PI / 2
            paint.color = color
            paint.strokeWidth = width

            val shiftX = (handRadius * radius * cos(angle)).toFloat()
            val shiftY = (handRadius * radius * sin(angle)).toFloat()
            canvas?.drawLine(
                cx - shiftX / 4.0f,
                cy - shiftY / 4.0f,
                cx + shiftX,
                cy + shiftY,
                paint
            )
        }

        fun assertNonNegative(x: Number) {
            if (x.toFloat() < 0) {
                throw java.lang.IllegalArgumentException("Argument has to be non-negative")
            }
        }

        suspend fun <E: View> redrawScheduled(view: E, interval: Int = DEFAULT_REDRAW_INTERVAL) {
            while (true) {
                view.invalidate()
                delay(interval.toLong())
            }
        }
    }
    
    var secondHandWidth = DEFAULT_SECOND_HAND_WIDTH
    set(value) {
        assertNonNegative(value)
        field = value
    }
    var minuteHandWidth = DEFAULT_MINUTE_HAND_WIDTH
        set(value) {
            assertNonNegative(value)
            field = value
        }
    var hourHandWidth = DEFAULT_HOUR_HAND_WIDTH
        set(value) {
            assertNonNegative(value)
            field = value
        }
    var secondHandRadius = DEFAULT_SECOND_HAND_RADIUS
        set(value) {
            assertNonNegative(value)
            field = value
        }
    var minuteHandRadius = DEFAULT_MINUTE_HAND_RADIUS
        set(value) {
            assertNonNegative(value)
            field = value
        }
    var hourHandRadius = DEFAULT_HOUR_HAND_RADIUS
        set(value) {
            assertNonNegative(value)
            field = value
        }

    var secondHandColor = DEFAULT_SECOND_HAND_COLOR
    var minuteHandColor = DEFAULT_MINUTE_HAND_COLOR
    var hourHandColor = DEFAULT_HOUR_HAND_COLOR

    var cvBackgroundColor = DEFAULT_BACKGROUND_COLOR
    var borderColor = DEFAULT_BORDER_COLOR
    var borderWidth = DEFAULT_BORDER_WIDTH
        set(value) {
            assertNonNegative(value)
            field = value
        }

    var divisionCount = DEFAULT_DIVISION_COUNT
        set(value) {
            assertNonNegative(value)
            field = value
        }
    var divisionColor = DEFAULT_DIVISION_COLOR
    var divisionRadius = DEFAULT_DIVISION_RADIUS
        set(value) {
            assertNonNegative(value)
            field = value
        }
    var divisionTextColor = DEFAULT_DIVISION_TEXT_COLOR
    var divisionTextSize = DEFAULT_DIVISION_TEXT_SIZE
        set(value) {
            assertNonNegative(value)
            field = value
        }

    var redrawInterval = DEFAULT_REDRAW_INTERVAL

    var calendarInstanceGetter: () -> Calendar = { GregorianCalendar.getInstance() }

    private val paint = Paint()
    private val textBounds = Rect()

    private var redrawJob: Job? = null

    private fun extractAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0)

        secondHandWidth = typedArray.getDimension(R.styleable.ClockView_second_hand_width, DEFAULT_SECOND_HAND_WIDTH)
        minuteHandWidth = typedArray.getDimension(R.styleable.ClockView_minute_hand_width, DEFAULT_MINUTE_HAND_WIDTH)
        hourHandWidth = typedArray.getDimension(R.styleable.ClockView_hour_hand_width, DEFAULT_HOUR_HAND_WIDTH)

        secondHandRadius = typedArray.getDimension(R.styleable.ClockView_second_hand_radius, DEFAULT_SECOND_HAND_RADIUS)
        minuteHandRadius = typedArray.getDimension(R.styleable.ClockView_minute_hand_radius, DEFAULT_MINUTE_HAND_RADIUS)
        hourHandRadius = typedArray.getDimension(R.styleable.ClockView_hour_hand_radius, DEFAULT_HOUR_HAND_RADIUS)

        secondHandColor = typedArray.getColor(R.styleable.ClockView_second_hand_color, DEFAULT_SECOND_HAND_COLOR)
        minuteHandColor = typedArray.getColor(R.styleable.ClockView_minute_hand_color, DEFAULT_MINUTE_HAND_COLOR)
        hourHandColor = typedArray.getColor(R.styleable.ClockView_hour_hand_color, DEFAULT_HOUR_HAND_COLOR)

        cvBackgroundColor = typedArray.getColor(R.styleable.ClockView_cv_background_color, DEFAULT_BACKGROUND_COLOR)
        borderColor = typedArray.getColor(R.styleable.ClockView_cv_border_color, DEFAULT_BORDER_COLOR)
        borderWidth = typedArray.getDimension(R.styleable.ClockView_cv_border_width, DEFAULT_BORDER_WIDTH)

        divisionCount = typedArray.getInteger(R.styleable.ClockView_division_count, DEFAULT_DIVISION_COUNT)
        divisionColor = typedArray.getColor(R.styleable.ClockView_division_color, DEFAULT_DIVISION_COLOR)
        divisionRadius = typedArray.getDimension(R.styleable.ClockView_division_radius, DEFAULT_DIVISION_RADIUS)
        divisionTextColor = typedArray.getColor(R.styleable.ClockView_division_text_color, DEFAULT_DIVISION_TEXT_COLOR)
        divisionTextSize = typedArray.getDimension(R.styleable.ClockView_division_text_size, DEFAULT_DIVISION_TEXT_SIZE)

        redrawInterval = typedArray.getInteger(R.styleable.ClockView_redraw_interval, DEFAULT_REDRAW_INTERVAL)

        typedArray.recycle()
    }

    private fun ensureTickerEnabled() {
        redrawJob?.cancel()
        redrawJob = clockCoroutineScope.launch { redrawScheduled(this@ClockView, redrawInterval) }
    }

    init {
        extractAttributes(attrs)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.reset()

        val radius = min(width, height) / 2.0f
        val cx = width / 2.0f
        val cy = height / 2.0f

        paint.color = borderColor
        canvas?.drawCircle(cx, cy, radius, paint)

        val workRadius = radius - borderWidth
        paint.color = cvBackgroundColor
        canvas?.drawCircle(width / 2.0f, height / 2.0f, workRadius, paint)

        paint.color = divisionColor
        iterateOverCircle((2 * PI / divisionCount).toFloat()) { _, alpha ->
            canvas?.drawCircle(0.93f * workRadius * cos(alpha) + cx, 0.93f * workRadius * sin(alpha) + cy, divisionRadius, paint)
        }

        paint.color = divisionTextColor
        paint.textSize = divisionTextSize
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER

        iterateOverCircle((2 * PI / 12).toFloat()) { i, alpha ->
            if (abs(alpha - 2 * PI) < 1e-5) { return@iterateOverCircle }
            val num = if (i < 10) { i + 3 } else { i - 9 }

            val numString = num.toString()
            paint.getTextBounds(numString, 0, numString.length, textBounds)

            canvas?.drawText(
                numString,
                0.8f * workRadius * cos(alpha) + cx,
                0.8f * workRadius * sin(alpha) + cy - textBounds.exactCenterY(),
                paint
            )
        }

        val calendarInstance = calendarInstanceGetter()
        val millis = calendarInstance.get(Calendar.MILLISECOND)
        val seconds = calendarInstance.get(Calendar.SECOND)
        val minutes = calendarInstance.get(Calendar.MINUTE)
        val hour = calendarInstance.get(Calendar.HOUR)

        //paint.setShadowLayer(12.0f, -22.0f, -22.0f, Color.GRAY)

        drawHand(
            canvas = canvas,
            paint = paint,
            unit = seconds * 1000 + millis,
            maximum = 60000,
            width = secondHandWidth,
            radius = workRadius,
            handRadius = secondHandRadius,
            cx = cx,
            cy = cy,
            color = secondHandColor
        )

        drawHand(
            canvas = canvas,
            paint = paint,
            unit = minutes * 60000 + seconds * 1000 + millis,
            maximum = 3600000,
            width = minuteHandWidth,
            radius = workRadius,
            handRadius = minuteHandRadius,
            cx = cx,
            cy = cy,
            color = minuteHandColor
        )

        drawHand(
            canvas = canvas,
            paint = paint,
            unit = hour * 3600000 + minutes * 60000 + seconds * 1000 + millis,
            maximum = 12 * 3600000,
            width = hourHandWidth,
            radius = workRadius,
            handRadius = hourHandRadius,
            cx = cx,
            cy = cy,
            color = hourHandColor
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        redrawJob?.cancel()
    }

    override fun onAttachedToWindow() {
        ensureTickerEnabled()
        super.onAttachedToWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) {
            ensureTickerEnabled()
        } else {
            redrawJob?.cancel()
        }

        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (visibility == VISIBLE) {
            ensureTickerEnabled()
        } else {
            redrawJob?.cancel()
        }

        super.onWindowVisibilityChanged(visibility)
    }
}