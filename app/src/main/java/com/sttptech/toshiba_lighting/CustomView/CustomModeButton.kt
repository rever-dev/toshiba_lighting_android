package com.sttptech.toshiba_lighting.CustomView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.sttptech.toshiba_lighting.R

class CustomModeButton : FrameLayout {
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

    private lateinit var itemView: ImageView
    private lateinit var tvNum: TextView
    private lateinit var tvName: TextView
    private lateinit var ibtEdit: ImageButton
    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.button_custom_mode, this, true)
        itemView = findViewById(R.id.cusModeBtn_img)
        tvNum = findViewById(R.id.cusModeBtn_tvNum)
        tvName = findViewById(R.id.cusModeBtn_tvName)
        ibtEdit = findViewById(R.id.cusModeBtn_ibtEdit)
        
    }
    
    
}