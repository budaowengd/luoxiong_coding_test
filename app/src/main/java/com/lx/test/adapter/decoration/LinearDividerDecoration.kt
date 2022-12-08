package com.lx.test.adapter.decoration

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.MarginLayoutParamsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *  desc: 支持垂直 和 水平 布局的分割线
 *      1. 支持第0个item的顶部有分割线
 *      2. 支持最底部的item没有分割线.
 */
class LinearDividerDecoration(context: Context, @ColorInt itemDividerColor: Int, pxHeight: Int? = null) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable? = null
    private val mBounds = Rect()

    // 最顶部是否支持分割线
    private var topHaveDivider = false

    // 最底部是否支持分割线
    private var bottomHaveDivider = true

    private var marginLeft = 0
    private var marginRight = 0
    private var marginTop = 0
    private var marginBottom = 0

    // 最左侧是否支持分割线
    private var leftHaveDivider = true

    // 最右侧是否支持分割线
    private var rightHaveDivider = true

    private var mOrientation = LinearLayout.VERTICAL

    private var ignoreOffset = false

    private var lastRowHideDivider = false

    fun setOrientation(orientation: Int): LinearDividerDecoration {
        require(!(orientation != LinearLayout.HORIZONTAL && orientation != LinearLayout.VERTICAL)) {
            "Invalid orientation. It should be " +
                    "either HORIZONTAL or VERTICAL"
        }
        mOrientation = orientation
        return this
    }

    fun setMarginTop(@Px top: Int): LinearDividerDecoration {
        marginTop = top
        return this
    }

    fun setMarginBottom(@Px bottom: Int): LinearDividerDecoration {
        marginBottom = bottom
        return this
    }

    fun setMarginTopBottom(@Px topBottom: Int): LinearDividerDecoration {
        marginTop = topBottom
        marginBottom = topBottom
        return this
    }

    fun setMarginLeft(@Px left: Int): LinearDividerDecoration {
        marginLeft = left
        return this
    }

    fun setMarginRight(@Px right: Int): LinearDividerDecoration {
        marginRight = right
        return this
    }


    fun setMarginLeftRight(@Px leftRight: Int): LinearDividerDecoration {
        marginLeft = leftRight
        marginRight = leftRight
        return this
    }

    fun setLastRowHideDivider(hide: Boolean): LinearDividerDecoration {
        lastRowHideDivider = hide
        return this
    }

    fun setTopHaveDivider(haveDivider: Boolean = true): LinearDividerDecoration {
        topHaveDivider = haveDivider
        return this
    }

    fun setBottomHaveDivider(haveDivider: Boolean = true): LinearDividerDecoration {
        bottomHaveDivider = haveDivider
        return this
    }

    fun setRightHaveDivider(haveDivider: Boolean = true): LinearDividerDecoration {
        rightHaveDivider = haveDivider
        return this
    }

    fun setIgnoreOffset(ignoreOffset: Boolean = true): LinearDividerDecoration {
        this.ignoreOffset = ignoreOffset
        return this
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(drawable: Drawable): LinearDividerDecoration {
        mDivider = drawable
        return this
    }

    /**
     * 在 itemView 显示之前进行绘制, itemView有可能会覆盖当前分割线.
     * 以图层的形式绘制
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    /**
     * 与onDraw类似，只不过是在绘制 itemView 之后绘制，具体表现形式，就是绘制的内容在 itemView 上层。
     * 为什么不在onDraw里执行绘制逻辑, 因为设置分割线时,如果item背景是白色, rv背景是红色, 当绘制了一根灰色分割线,并且需要设置左边margin 30,
     * 如果设置offset, 会导致左侧30能看到rv的背景.
     * 所以不能设置offset, 但是如果在onDraw里绘制, 并且不设置offset,item的绘制会覆盖分割线, 所以需要在onDrawOver里绘制
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (parent.layoutManager == null || mDivider == null) {
            return
        }
        if (mOrientation == LinearLayout.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    /**
     * 检索给定item的任何偏移量。 outRect每个字段指定项目视图应该插入的像素数，类似于填充或边距。 默认实现将 outRect 的边界设置为 0 并返回。
     * 如果这个 ItemDecoration 不影响 item 视图的定位，它应该在返回之前将outRect所有四个字段（左、上、右、下）设置为零
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == -1) {
            mOrientation = if (parent.layoutManager is LinearLayoutManager) {
                (parent.layoutManager as LinearLayoutManager?)!!.orientation
            } else {
                RecyclerView.VERTICAL
            }
        }
        if (mDivider == null || ignoreOffset) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (mOrientation == LinearLayout.VERTICAL) {
            if (topHaveDivider) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    // 因为绘制了顶部分割线，因此需要将第一行的item下移相应的距离
                    // 这里要注意，判断该RecycleView是什么布局，如果是竖直方向上的线性布局（水平方向上的这里没有考虑）
                    // 那么需要让第一个Item下移，移动的距离是分割线的高度，因为分割线会占据Item的空间
                    // 如果是网格布局，那么需要把第一行的所有Item都下移相应的高度
                    // logPrint("getItemOffsets() 将第一行的item下移相应的距离")
                    outRect.set(0, mDivider!!.intrinsicHeight, 0, mDivider!!.intrinsicHeight)
                    return
                }
            }
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            if (leftHaveDivider) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    // item的第0个的顶部也支持分割线
                    outRect.set(mDivider!!.intrinsicHeight, 0, mDivider!!.intrinsicHeight, 0)
                    return
                }
            }
            outRect.set(0, 0, mDivider!!.intrinsicHeight, 0)
        }
    }


    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        var left: Int = marginLeft
        var right = 0
        if (parent.clipToPadding) {
            left = parent.paddingLeft + marginLeft
            right = parent.width - parent.paddingRight - marginRight
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            right = parent.width - marginRight
        }
        // 可见屏幕的item数量
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (i == childCount - 1 && (!bottomHaveDivider || lastRowHideDivider)) {
                // 最后一行并且底部没有分割线 或者最后一行不需要分割线，不绘制
                continue
            } else if (i == 0) {
                // 如果支持顶部分割线, 绘制
                if (topHaveDivider) {
                    //不可以直接设置top = 0；因为这样的话分隔线就不会跟着移动，因为top = 0,是绝对位置，
                    // 所以应该设置为子view的相对位置, 这样才可以跟着滑动
                    //child的顶部坐标，减去设置的margin_top的值，再减去child为了给分割线腾出空间所下滑的高度，
                    val top: Int = child.top - child.marginTop - mDivider!!.intrinsicHeight
                    //logPrint("绘制第0个item的分割线...top=${top}")
                    mDivider!!.setBounds(left, top, right, top + mDivider!!.intrinsicHeight)
                    mDivider!!.draw(canvas)
                }
            }
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - mDivider!!.intrinsicHeight
            //Log.d("MVVM", "pos=$i left=$left  top=$top  right=$right bottom=$bottom")
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        var top: Int = marginTop
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop + marginTop
            bottom = parent.height - parent.paddingBottom - marginBottom
            canvas.clipRect(parent.paddingLeft, top, parent.width - parent.paddingRight, bottom)
        } else {
            bottom = parent.height - marginBottom
        }
        // parent.childCount 会一直递增
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (i == childCount - 1 && (!rightHaveDivider || lastRowHideDivider)) {
                continue
            }
            if (i == 0) {
                // 如果支持左侧分割线, 绘制
                if (leftHaveDivider) {
                    val left: Int = child.left - child.marginStart - mDivider!!.intrinsicHeight
                    mDivider!!.setBounds(left, top, left + mDivider!!.intrinsicHeight, bottom)
                    mDivider!!.draw(canvas)
                }
            }
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + Math.round(child.translationX)
            val left = right - mDivider!!.intrinsicHeight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }


    private fun createDivider(context: Context, @ColorInt itemDividerColor: Int, pxHeight: Int? = null): LinearDividerDecoration {
        val gd = GradientDrawable()
        gd.setColor(itemDividerColor)
        var height = pxHeight
        if (height == null || height == 0) {
            height = (Resources.getSystem().displayMetrics.density + 0.5).toInt()
        }
        // 注意: 这里width设置了0, 所以只能通过 mDivider!!.intrinsicHeight 获取分割线的大小
        gd.setSize(0, height)
        mDivider = gd
        return this
    }

    private inline val View.marginStart: Int
        get() {
            val lp = layoutParams
            return if (lp is ViewGroup.MarginLayoutParams) {
                MarginLayoutParamsCompat.getMarginStart(lp)
            } else {
                0
            }
        }

    private inline val View.marginTop: Int
        get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0

    init {
        createDivider(context, itemDividerColor, pxHeight)
    }

}
