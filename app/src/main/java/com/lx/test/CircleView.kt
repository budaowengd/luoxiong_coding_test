package com.lx.test

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.ViewAnimator
import com.blankj.utilcode.util.LogUtils

/**
 *  date: 2022/12/9
 *  version: 1.0
 *  desc:
 */
class CircleView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val mPaint = Paint().apply {
        setColor(Color.GREEN)
        strokeWidth = 3F
    }
    val mPaintRed = Paint().apply {
        setColor(Color.BLACK)
        strokeWidth = 3F
    }
    val mPaintWhite = Paint().apply {
        setColor(Color.WHITE)
        strokeWidth = 3F
        textSize = 64F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = measuredHeight.toFloat()
        val heightInt = measuredHeight
        canvas.drawCircle(height / 2F, height / 2F, height / 2F, mPaint)
        canvas.drawLine(0F, height / 2F, height, height / 2F, mPaintRed)
        canvas.drawLine(height / 2F, 0F, height / 2, height, mPaintRed)
        val text = "Hello World"
        canvas.drawText(text, 0, text.length, 0F, height / 2, mPaintWhite)
        //  canvas.drawPath(0F,0F,100F,100F,mPaint)
    }

    fun startAnim() {
        val anim = ScaleAnimation(
            1F, 1.2F, 1F, 1.2F, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5F
        )
        anim.duration = 3000
        startAnimation(anim)

        // ObjectAnimator.ofArgb()
    }
}