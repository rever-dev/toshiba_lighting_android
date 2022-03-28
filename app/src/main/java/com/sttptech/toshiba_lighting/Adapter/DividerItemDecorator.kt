package com.sttptech.toshiba_lighting.Adapter

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

open class DividerItemDecorator(private val mDivider: Drawable) : RecyclerView.ItemDecoration() {
    
    
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val padding = parent.width / 20
        val dividerLeft = parent.paddingLeft + padding
        val dividerRight = parent.width - parent.paddingRight - padding
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + mDivider.intrinsicHeight
            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider.draw(c)
        }
    }
}