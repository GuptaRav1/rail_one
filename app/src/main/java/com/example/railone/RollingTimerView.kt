package com.example.railone

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

class RollingTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val digitM1: DigitReelView
    private val digitM2: DigitReelView
    private val colonView: TextView
    private val digitS1: DigitReelView
    private val digitS2: DigitReelView

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        val density = context.resources.displayMetrics.density
        val digitWidth = (36 * density).toInt()
        val digitHeight = (68 * density).toInt()
        val gap = (2 * density).toInt()

        digitM1 = createDigitReel(digitWidth, digitHeight)
        digitM2 = createDigitReel(digitWidth, digitHeight)

        val montserratFont = try {
            ResourcesCompat.getFont(context, R.font.montserrat_semibold)
        } catch (_: Exception) {
            Typeface.create("sans-serif-medium", Typeface.BOLD)
        }

        colonView = TextView(context).apply {
            text = ":"
            setTextColor(Color.parseColor("#F44336"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 52f)
            setTypeface(montserratFont, Typeface.BOLD)
            gravity = Gravity.CENTER
            translationY = (-3 * density)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, digitHeight).apply {
                setMargins(gap, 0, gap, 0)
            }
        }

        digitS1 = createDigitReel(digitWidth, digitHeight)
        digitS2 = createDigitReel(digitWidth, digitHeight)

        addView(digitM1)
        addView(digitM2)
        addView(colonView)
        addView(digitS1)
        addView(digitS2)
    }

    private var lastMinutes: Long = -1L
    private var lastSeconds: Long = -1L

    private fun createDigitReel(width: Int, height: Int): DigitReelView {
        return DigitReelView(context).apply {
            layoutParams = LayoutParams(width, height).apply {
                val gap = (2 * context.resources.displayMetrics.density).toInt()
                setMargins(gap, 0, gap, 0)
            }
        }
    }

    fun setTime(minutes: Long, seconds: Long) {
        if ((minutes == lastMinutes) && (seconds == lastSeconds)) {
            return
        }

        val isFirstPaint = (lastMinutes == -1L) && (lastSeconds == -1L)
        lastMinutes = minutes
        lastSeconds = seconds

        val m1 = (minutes / 10).toString()
        val m2 = (minutes % 10).toString()
        val s1 = (seconds / 10).toString()
        val s2 = (seconds % 10).toString()

        digitM1.setDigit(m1, forceAnimate = false)
        digitM2.setDigit(m2, forceAnimate = false)
        digitS1.setDigit(s1, forceAnimate = !isFirstPaint)
        digitS2.setDigit(s2, forceAnimate = !isFirstPaint)
    }

    fun setTime(timeString: String) {
        val parts = timeString.split(":")
        if (parts.size == 2) {
            val m = parts[0].toLongOrNull() ?: 0L
            val s = parts[1].toLongOrNull() ?: 0L
            setTime(m, s)
        }
    }
}
