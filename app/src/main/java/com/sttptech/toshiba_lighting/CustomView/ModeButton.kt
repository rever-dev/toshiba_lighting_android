package com.sttptech.toshiba_lighting.CustomView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.sttptech.toshiba_lighting.R

class ModeButton : FrameLayout {
    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private lateinit var icon: ImageView
    private lateinit var tvName: TextView
    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.button_mode, this, true)
        icon = findViewById(R.id.modeBtn_imgIcon)
        tvName = findViewById(R.id.modeBtn_tvName)
    }

    fun setIcon(drawable: Drawable?) {
        icon.setImageDrawable(drawable)
    }

    fun getModeName(): String {
        return tvName.text.toString()
    }

    fun setName(tvName: String?) {
        this.tvName.text = tvName
    }
}