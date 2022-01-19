package com.sttptech.toshiba_lighting.Fragment

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.sttptech.toshiba_lighting.Application.BaseApplication

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val context: Context get() = getApplication<BaseApplication>().baseContext
    protected val repository get() = BaseApplication.repository

}