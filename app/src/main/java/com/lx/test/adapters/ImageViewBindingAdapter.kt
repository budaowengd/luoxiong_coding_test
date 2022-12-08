package com.lx.test.adapters

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.lx.test.const.MyConfig

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc:
 */

/**
 * 指定iv的宽高的情况下, 还可以设置iv的图标大小,基于矩阵
 */
@BindingAdapter(
    value = [
        "bind_iv1_src_drawable", "bind_iv1_layout_wh", "bind_iv1_layout_width", "bind_iv1_layout_height",
        "bind_iv1_icon_wh", "bind_iv1_icon_width", "bind_iv1_icon_height", "bind_iv1_icon_color",
    ], requireAll = false
)
fun ImageView.setImgSizeAndLayoutSize(
    drawable: Drawable? = null,
    @Px ivWH: Float? = null,
    @Px ivW: Float? = null,
    @Px ivH: Float? = null,
    @Px  imgWH: Float? = null,
    @Px  imgW: Float? = null,
    @Px  imgH: Float? = null,
    @ColorInt iconColor: Int? = null,
) {
    drawable ?: return
    val dWidth = drawable.intrinsicWidth
    val dHeight = drawable.intrinsicHeight
    var needIconW = 0F
    var needIconH = 0F
    var ivRealW = 0F
    var ivRealH = 0F
    // 获取iv需要显示的图标宽高
    if (imgWH != null) {
        needIconW = imgWH
        needIconH = imgWH
    } else {
        if (imgW != null) {
            needIconW = imgW
        }
        if (imgH != null) {
            needIconH = imgH
        }
    }
    if (ivWH != null) {
        ivRealW = ivWH
        ivRealH = ivWH
    } else {
        if (ivW != null) {
            ivRealW = ivW
        }
        if (ivH != null) {
            ivRealH = ivH
        }
    }
    /// Timber.d("needIconW=${needIconW} needIconH=$needIconH  dWidth=$dWidth dHeight=$dHeight")
    val iv=this
    iv.scaleType = ImageView.ScaleType.MATRIX;  //设置为矩阵模式
    val matrix = Matrix()
    //val vWidth = iv.measuredWidth  // 275, 这里拿不到的,measuredWidth 和 width都是0
    val scaleRatioX = needIconW / dWidth  // 150/50 = 3
    val scaleRatioY = needIconH / dHeight
//    LogUtils.d("vWidth=$ivRealW vW=${iv.width} scaleRatioX=$scaleRatioX scaleRatioY=$scaleRatioY pointX=$pointX  pointY=$pointY p=${(ivRealW - needIconW) / 4}")
    // (vWidth - dWidth) * 0.5f
    val dx = (ivRealW - dWidth * scaleRatioX) * 0.5f
    val dy = (ivRealH - dHeight * scaleRatioY) * 0.5f
    matrix.setScale(scaleRatioX, scaleRatioY)
    matrix.postTranslate(dx, dy)
    iv.imageMatrix = matrix
    iv.setImageDrawable(drawable)
    if (iconColor != null && iconColor != 0) {
        val colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP)
        iv.colorFilter = colorFilter
    }
}


// 设置图片的颜色，
@BindingAdapter(
    value = ["bind_iv_src_color"
    ], requireAll = false
)
fun ImageView.updateSrcColor(@ColorInt color: Int) {
    val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    this.colorFilter = colorFilter
}

@SuppressLint("CheckResult")
@BindingAdapter(
    value = ["bind_img_url", "bind_img_place", "bind_img_error",
        "bind_img_width", "bind_img_height", "bind_img_widthHeight",
        "bind_img_support_circle", "bind_img_corner",
        "bind_img_top_left_corner", "bind_img_top_right_corner",
        "bind_img_bottom_left_corner", "bind_img_bottom_right_corner"
    ], requireAll = false
)
fun ImageView.loadImg(
    url: Any?,
    placeOrder: Drawable? = null,
    placeError: Drawable? = null,
    width: Float? = null,
    height: Float? = null,
    widthHeight: Float? = null,
    supportCircle: Boolean? = null,
    radius: Float? = null,
    topLeftRadius: Float? = null,
    topRightRadius: Float? = null,
    bottomLeftRadius: Float? = null,
    bottomRightRadius: Float? = null
) {
    if (url == null) return
    val request = Glide.with(this).load(url)
    if (width != null && height != null) {
        val w = width.toInt()
        val h = height.toInt()
        if (w > 0 && h > 0) {
            request.override(w, h)
        }
    } else if (widthHeight != null && widthHeight > 0) {
        request.override(widthHeight.toInt())
    }
    if (placeOrder != null) {
        request.placeholder(placeOrder)
    } else if (MyConfig.glide_img_load_placeholder != null) {
        request.placeholder(MyConfig.glide_img_load_placeholder)
    }
    if (placeError != null) {
        request.error(placeError)
    }
    if (supportCircle == true) {
        request.circleCrop()
    } else if (radius != null && radius > 0) {
        request.transform(MultiTransformation(CenterCrop(), RoundedCorners(radius.toInt())))
    } else if (topLeftRadius != null || topRightRadius != null || bottomLeftRadius != null || bottomRightRadius != null) {
        val corner = GranularRoundedCorners(
            topLeftRadius ?: 0F,
            topRightRadius ?: 0F,
            bottomLeftRadius ?: 0F,
            bottomRightRadius ?: 0F,
        )
        request.transform(MultiTransformation(CenterCrop(), corner))
    }
    request.into(this)
}
