package com.lx.test.adapters

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.databinding.BindingAdapter
import kotlin.math.min

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc:
 */
private var vDp1F = Resources.getSystem().displayMetrics.density

/**
 * 设置View是否显示
 */
@BindingAdapter("bind_visible")
fun View.setVisibleOrGone(isVisible: Boolean?) {
    this.visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

/**
 * 动态设置边筐大小,背景色,以及圆角
 *
 * @param bind_bg_bgColor   背景色
 * @param bind_bg_borderColor   边框色
 * @param bind_bg_borderWidth   边框的大小
 * @param bind_bg_isRadiusAdjustBounds  背景样式是否为View高度的一半
 * @param bind_bg_radius    圆角
 * @param bind_bg_radiusTopLeft     左上圆角
 * @param bind_bg_radiusTopRight    右上圆角
 * @param bind_bg_radiusBottomLeft  左下圆角
 * @param bind_bg_radiusBottomRight 右下圆角
 */
@BindingAdapter(
    value = ["bind_bg_bgColor", "bind_bg_borderColor", "bind_bg_borderWidth", "bind_bg_isRadiusAdjustBounds",
        "bind_bg_radius", "bind_bg_radiusTopLeft", "bind_bg_radiusTopRight", "bind_bg_radiusBottomLeft", "bind_bg_radiusBottomRight"],
    requireAll = false
)
fun View.setBgAndRadius(
    @ColorInt bgColor: Int,
    @ColorInt borderColor: Int = 0,
    @Px bind_bg_borderWidth: Float? = null,
    adjustBounds: Boolean,
    @Px radius: Float? = null,
    @Px radiusTopLeft: Float? = null,
    @Px radiusTopRight: Float? = null,
    @Px radiusBottomLeft: Float? = null,
    @Px radiusBottomRight: Float? = null
) {
    this.background = getCustomDrawable(
        bgColor,
        borderColor,
        bind_bg_borderWidth,
        adjustBounds,
        radius,
        radiusTopLeft,
        radiusTopRight,
        radiusBottomLeft,
        radiusBottomRight
    )
}

fun getCustomDrawable(
    @ColorInt bgColor: Int,
    @ColorInt borderColor: Int,
    @Px lxBorderWidth: Float? = 0F,
    adjustBounds: Boolean? = null,
    @Px lxRadius: Float? = null,
    @Px lxRadiusTopLeft: Float? = null,
    @Px lxRadiusTopRight: Float? = null,
    @Px lxRadiusBottomLeft: Float? = null,
    @Px lxRadiusBottomRight: Float? = null
): Drawable {
    val radius = lxRadius ?: 0F
    val radiusTopLeft = lxRadiusTopLeft ?: 0F
    val radiusTopRight = lxRadiusTopRight ?: 0F
    val radiusBottomLeft = lxRadiusBottomLeft ?: 0F
    val radiusBottomRight = lxRadiusBottomRight ?: 0F
    var isRadiusAdjustBounds = adjustBounds ?: false
    val bg = object : GradientDrawable() {
        override fun onBoundsChange(r: Rect) {
            super.onBoundsChange(r)
            if (isRadiusAdjustBounds) {
                cornerRadius = (min(r.width(), r.height()) / 2).toFloat()
            }
        }
    }
    if (bgColor != 0) {
        //支持颜色选择器
        val colorListBg = ColorStateList.valueOf(bgColor)
        bg.color = colorListBg
    }
    if (borderColor != 0) {
        val borderWidth = if (lxBorderWidth == null || lxBorderWidth < 1) {
            (vDp1F * 0.7).toInt()
        } else {
            lxBorderWidth.toInt()
        }
        bg.setStroke(borderWidth, ColorStateList.valueOf(borderColor))
    }
    if (radiusTopLeft > 0 || radiusTopRight > 0 || radiusBottomLeft > 0 || radiusBottomRight > 0) {
        val radii = floatArrayOf(
            radiusTopLeft, radiusTopLeft, radiusTopRight, radiusTopRight, radiusBottomRight,
            radiusBottomRight, radiusBottomLeft, radiusBottomLeft
        )
        bg.cornerRadii = radii
        isRadiusAdjustBounds = false
    } else {
        if (radius > 0) {
            bg.cornerRadius = radius
            isRadiusAdjustBounds = false
        }
    }
    return bg
}
