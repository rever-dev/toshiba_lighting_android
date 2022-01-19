package com.sttptech.toshiba_lighting.Fragment.Device.DeviceList

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Fragment.BaseViewModel
import kotlinx.coroutines.*

class DeviceListViewModel(application: Application) : BaseViewModel(application) {

    private val mainHandler = Handler(Looper.getMainLooper())

    var deviceList = MutableLiveData<List<Device>?>(null)

//    init {
//        deviceList.value = repository.localS.allCeilingLights()
//    }

    fun refreshData() {

        Thread {
            val ceilingLight: List<CeilingLight>? = repository.localS.allCeilingLights()
            mainHandler.post() { deviceList.value = ceilingLight}
        }.start()
    }
}
