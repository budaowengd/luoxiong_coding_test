package com.lx.test.adapters

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.databinding.BindingAdapter

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc:
 */

/**
 * 动态设置TextView四周的图片, 并设置图片大小
 */
@BindingAdapter(
    value = [
        "bind_tv_drawable_left", "bind_tv_drawable_right", "bind_tv_drawable_top",
        "bind_tv_drawable_bottom", "bind_tv_drawable_color", "bind_tv_drawable_wh",
        "bind_tv_drawable_width", "bind_tv_drawable_height",
    ], requireAll = false
)
fun TextView.setDrawables(
    leftD: Drawable? = null,
    rightD: Drawable? = null,
    topD: Drawable? = null,
    bottomD: Drawable? = null,
    @ColorInt drawableColor: Int? = null,
    @Px drawableWh: Float? = null,
    @Px drawableWidth: Float? = null,
    @Px drawableHeight: Float? = null,
) {

    var width = 0
    var height = 0
    // 设置宽高
    if (drawableWh != null) {
        width = drawableWh.toInt()
        height = drawableWh.toInt()
    } else {
        if (drawableWidth != null) {
            width = drawableWidth.toInt()
        }
        if (drawableHeight != null) {
            height = drawableHeight.toInt()
        }
    }
    leftD?.setBounds(0, 0, width, height)
    rightD?.setBounds(0, 0, width, height)
    topD?.setBounds(0, 0, width, height)
    bottomD?.setBounds(0, 0, width, height)
    if (drawableColor != null && drawableColor != 0) {
        leftD?.mutate()?.colorFilter = PorterDuffColorFilter(drawableColor, PorterDuff.Mode.SRC_ATOP)
        rightD?.mutate()?.colorFilter = PorterDuffColorFilter(drawableColor, PorterDuff.Mode.SRC_ATOP)
        topD?.mutate()?.colorFilter = PorterDuffColorFilter(drawableColor, PorterDuff.Mode.SRC_ATOP)
        bottomD?.mutate()?.colorFilter = PorterDuffColorFilter(drawableColor, PorterDuff.Mode.SRC_ATOP)
    }
    /// 这一步必须要做,否则不会显示.
    this.setCompoundDrawables(leftD, topD, rightD, bottomD)
}