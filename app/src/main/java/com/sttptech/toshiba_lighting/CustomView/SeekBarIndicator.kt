package com.sttptech.toshiba_lighting.CustomView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.R

class SeekBarIndicator : FrameLayout {
    
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
    
    var seekBarProgress: Int = 0
    
    lateinit var tvValue: TextView
    
    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.seekbar_indicator, this, true)
        tvValue = findViewById(R.id.seekBarIndicator_tvValue)
    }
}
