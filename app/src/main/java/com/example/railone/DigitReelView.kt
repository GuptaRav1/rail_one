package com.example.railone

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

class DigitReelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val text1: TextView = createTextView()
    private val text2: TextView = createTextView()

    private var currentText: String = ""
    private var activeTextView: TextView = text1
    private var inactiveTextView: TextView = text2

    // Cubic-bezier(.36, .07, .19, .97) matches timer.html CSS transition
    private val interpolator = PathInterpolator(0.36f, 0.07f, 0.19f, 0.97f)

    init {
        clipChildren = true
        clipToPadding = true
        addView(text1)
        addView(text2)
    }

    private fun createTextView(): TextView {
        val montserratFont = try {
            ResourcesCompat.getFont(context, R.font.montserrat_semibold)
        } catch (_: Exception) {
            Typeface.create("sans-serif-medium", Typeface.BOLD)
        }

        return TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#F44336"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 56f)
            setTypeface(montserratFont, Typeface.BOLD)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
    }

    fun setDigit(newText: String, forceAnimate: Boolean = false) {
        if ((currentText == newText) && !forceAnimate) {
            return
        }

        val isFirstPaint = currentText.isEmpty()
        val prevText = if (isFirstPaint) newText else currentText
        currentText = newText

        val h = (if (height > 0) height else (56 * context.resources.displayMetrics.density * 1.2f).toInt()).toFloat()

        if (isFirstPaint) {
            activeTextView.text = newText
            activeTextView.translationY = 0f
            inactiveTextView.translationY = h
            return
        }

        activeTextView.animate().cancel()
        inactiveTextView.animate().cancel()

        val incomingView = inactiveTextView
        val outgoingView = activeTextView

        incomingView.text = newText
        outgoingView.text = prevText

        incomingView.translationY = -h
        outgoingView.translationY = 0f

        incomingView.animate()
            .translationY(0f)
            .setDuration(480)
            .setInterpolator(interpolator)
            .start()

        outgoingView.animate()
            .translationY(h)
            .setDuration(480)
            .setInterpolator(interpolator)
            .start()

        activeTextView = incomingView
        inactiveTextView = outgoingView
    }
}
