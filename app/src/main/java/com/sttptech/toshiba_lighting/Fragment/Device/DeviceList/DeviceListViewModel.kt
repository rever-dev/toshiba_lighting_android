package com.sttptech.toshiba_lighting.Fragment.Device.DeviceList

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.DeviceStatus
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Fragment.BaseViewModel
import com.sttptech.toshiba_lighting.Mqtt.MqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag

class DeviceListViewModel(application: Application) : BaseViewModel(application),
    MqttClient.MqttCallbackListener {
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val mqtt get() = BaseApplication.mqttClient
    
    var deviceList: MutableLiveData<List<Device>?> = MutableLiveData()
    var groupList: MutableLiveData<List<Group>?> = MutableLiveData()
    
    var updateStatus: MutableLiveData<Device> = MutableLiveData()
    
    init {
        Thread {
            // device
            val ceilingLight: List<CeilingLight>? = repository.localS.allCeilingLights()
            deviceList.postValue(ceilingLight)
            
            // group
            val groups: List<Group>? = repository.localS.allGroups()
            groupList.postValue(groups)
        }.start()
    }
    
    fun refreshData() {
        Thread {
            // device
            val ceilingLight: List<CeilingLight>? = repository.localS.allCeilingLights()
            deviceList.postValue(ceilingLight)
            
            // group
            val groups: List<Group>? = repository.localS.allGroups()
            groupList.postValue(groups)
        }.start()
    }
    
    fun getStatus() {
        if (mqtt.mqttCallbackListener == null )
            mqtt.mqttCallbackListener = this
            
        if (deviceList.value != null) {
            for (dev in deviceList.value!!) {
                mqtt.subscribeTopic(dev.model!!, dev.macId, MqttTopicTag.STATUS)
                MqttPublish.getStatus(mqtt, dev.model!!, dev.macId)
            }
        }
    }
    
    fun unListenerTopic() {
        mqtt.mqttCallbackListener = null
    }
    
    @Synchronized
    override fun msgArrived(topic: String, msg: String) {
        if (deviceList.value == null) return

        val bssId = topic.split("/")[4]
        for (i in deviceList.value!!.indices) {
            if (deviceList.value!![i].macId == bssId) {

                val status = Gson().fromJson(msg, DeviceStatus::class.java)
                val dev = (deviceList.value!![i] as CeilingLight).copy()

                dev.selectMode = status.payload.selectMode.toInt()
                dev.opMode = status.payload.opMode.toInt()
                dev.mBr = status.payload.mBr.toInt()
                dev.mC = status.payload.mC.toInt()
                dev.nBr = status.payload.nBr.toInt()
                dev.rgbBr = status.payload.rgbBr.toInt()

                val newData = (deviceList.value!!).toMutableList()
                newData[i] = dev

                deviceList.postValue(newData.toList())
                return
            }
        }
    }
    
    fun devOnOff(dev: Device, onOff: Boolean) {
        MqttPublish.triggerAll(mqtt, onOff, dev.model!!, dev.macId)
    }
}
