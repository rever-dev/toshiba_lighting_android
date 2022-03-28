package com.sttptech.toshiba_lighting.Activity.NSD

import android.app.Activity
import android.content.Context
import android.net.nsd.NsdManager


abstract class BaseNSD(protected var mActivity: Activity) {
    protected var TAG: String = "Android_NSD"
    protected var mNsdManager: NsdManager? = null
    protected open fun init() {
        mNsdManager = mActivity.getSystemService(Context.NSD_SERVICE) as NsdManager
    }
    
    companion object {
        var SERVICE_TYPE = "_http._tcp."
    }
    
    init {
        init()
    }
}