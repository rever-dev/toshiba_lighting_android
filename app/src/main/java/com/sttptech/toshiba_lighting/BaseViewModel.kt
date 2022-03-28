package com.sttptech.toshiba_lighting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sttptech.toshiba_lighting.Application.BaseApplication

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    
    val mqtt get() = BaseApplication.mqttClient
    val remoteService get() = BaseApplication.repository.remoteS
    val localService get() = BaseApplication.repository.localS
}