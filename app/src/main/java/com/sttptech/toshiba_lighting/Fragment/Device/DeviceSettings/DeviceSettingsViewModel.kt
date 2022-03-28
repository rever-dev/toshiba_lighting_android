package com.sttptech.toshiba_lighting.Fragment.Device.DeviceSettings

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Application.LocalService
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceGroup.ADD_DEVICE
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceGroup.REMOVE_DEVICE
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceSettings
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceSettings.*
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.DeviceStatus
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Mqtt.MqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag
import java.text.SimpleDateFormat
import java.util.*

class DeviceSettingsViewModel(application: Application) : BaseViewModel(application) {
    
    val touchBlocker = FastTouchBlocker()
    
    val devName: MutableLiveData<String?> = MutableLiveData()
    val devLoc: MutableLiveData<Group?> = MutableLiveData()
    val devModel: MutableLiveData<String?> = MutableLiveData()
    val devBssid: MutableLiveData<String?> = MutableLiveData()
    
    val switchStatus: MutableLiveData<Boolean?> = MutableLiveData()
    val wakeUpTime: MutableLiveData<Long?> = MutableLiveData()
    val wakeUpState: MutableLiveData<Boolean?> = MutableLiveData()
    val dailyOnTime: MutableLiveData<Long?> = MutableLiveData()
    val dailyOnState: MutableLiveData<Boolean?> = MutableLiveData()
    val dailyOffTime: MutableLiveData<Long?> = MutableLiveData()
    val dailyOffState: MutableLiveData<Boolean?> = MutableLiveData()
    val countDownTime: MutableLiveData<Long?> = MutableLiveData()
    val countDownState: MutableLiveData<Boolean?> = MutableLiveData()
    val sleepTime: MutableLiveData<Boolean?> = MutableLiveData()
    val buzzer: MutableLiveData<Boolean?> = MutableLiveData()
    
    private lateinit var uId: String
    
    fun init(uId: String) {
        this.uId = uId
        
        Thread {
            val dev = BaseApplication.repository.localS.getCeilingLightById(uId)
            if (dev != null) {
                devName.postValue(dev.name)
                devLoc.postValue(dev.group)
                wakeUpTime.postValue(dev.wakeUp)
                wakeUpState.postValue(dev.wakeUpS)
                dailyOnTime.postValue(dev.dailyOn)
                dailyOnState.postValue(dev.dailyOnS)
                dailyOffTime.postValue(dev.dailyOff)
                dailyOffState.postValue(dev.dailyOffS)
                countDownTime.postValue(dev.cdtTime)
                countDownState.postValue(dev.cdtStatus == 1)
                sleepTime.postValue(dev.sleep == 1)
                buzzer.postValue(dev.buzzer == 1)
                
                devModel.postValue(dev.model)
                devBssid.postValue(dev.macId)
            }
        }.start()
    }
    
    private val model get() = devModel.value!!
    private val bssid get() = devBssid.value!!
    
    fun listenTopic() {
        mqtt.subscribeTopic(model, bssid, MqttTopicTag.STATUS)
        mqtt.mqttCallbackListener = object : MqttClient.MqttCallbackListener {
            override fun msgArrived(topic: String, msg: String) {
                
                val bssId = topic.split("/")[4]
                
                if (bssId == (bssid)) {
                    val status = Gson().fromJson(msg, DeviceStatus::class.java)
                    
                    
                    switchStatus.postValue(
                        (status.payload.opMode.toInt() == 1 &&
                                status.payload.selectMode.toInt() == 0 &&
                                status.payload.mBr.toInt() == 0)
                            .not()
                    )
                    
                    
                    // Parse status to bytes.
                    val statusInt = status.payload.setStatus.toInt().toString(2)
                    val cha = (statusInt.toInt() + 900000000).toString().toCharArray()
                    
                    buzzer.postValue(cha[8] == '1')
                    countDownState.postValue(cha[7] == '1')
                    sleepTime.postValue(cha[6] == '1')
                }
                
            }
        }
    }
    
    fun unListenTopic() {
        mqtt.mqttCallbackListener = null
    }
    
    fun getStatus() {
        MqttPublish.getStatus(mqtt, model, bssid)
    }
    
    fun modifyName(newName: String): Boolean {
        val result = remoteService.modifyDeviceName(uId, newName)
        if (result) {
            devName.postValue(newName)
            val dev = localService.getCeilingLightById(uId)
            dev?.name = newName
            if (dev != null)
                localService.updateCeilingLight(dev)
        }
        return result
    }
    
    fun modifyDeviceGroup(
        groupName: String,
    ): Boolean {
        val groups = localService.allGroups() ?: return false
        
        // If group already exists, add to the group, and remove from the old group
        for (i in groups.indices) {
            if (groups[i].groupName == groupName) {
                
                // Add device to the group
                val addResponse =
                    remoteService.modifyDeviceGroup(uId, groups[i].groupUuid!!, ADD_DEVICE)
                if (addResponse != null && addResponse.isSuccess()) {
                    
                    // Remove device from the old group
                    val removeResponse =
                        remoteService.modifyDeviceGroup(
                            uId,
                            devLoc.value?.groupUuid!!,
                            REMOVE_DEVICE
                        )
                    if (removeResponse != null && removeResponse.isSuccess()) {
                        
                        val group =
                            Gson().fromJson(
                                addResponse.getDatum()?.getGroupData(),
                                Group::class.java
                            )
                        val dev = localService.getCeilingLightById(uId)
                        dev?.group = group
                        
                        // update local data & view model
                        localService.updateCeilingLight(dev!!)
                        devLoc.postValue(group)
                        return true
                    }
                }
            }
        }
        
        // If not, create a new group
        val createResponse = remoteService.createGroup(uId, groupName)
        if (createResponse != null && createResponse.isSuccess()) {
            
            // Remove device from the old group
            val removeResponse =
                remoteService.modifyDeviceGroup(uId, devLoc.value?.groupUuid!!, REMOVE_DEVICE)
            if (removeResponse != null && removeResponse.isSuccess()) {
                val group =
                    Gson().fromJson(createResponse.getDatum()?.toString(), Group::class.java)
                val dev = localService.getCeilingLightById(uId)
                dev?.group = group
                
                // update local data & view model
                localService.updateCeilingLight(dev!!)
                devLoc.postValue(group)
                return true
            }
        }
        return false
    }
    
    @SuppressLint("SimpleDateFormat")
    fun modifySchedule(action: ModifyDeviceSettings, time: Long, status: Boolean): Boolean {
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timeStr = outputFormat.format(Date(time))
        
        val onOff = if (status) "O" else "F"
        val response = remoteService.modifyDeviceSetting(uId, action, timeStr, onOff)
        
        return if (response != null && response.isSuccess()) {
            val dev = localService.getCeilingLightById(uId)
            when (action) {
                Wakeup -> {
                    dev?.wakeUp = time
                    dev?.wakeUpS = status
                }
                DailyOn -> {
                    dev?.dailyOn = time
                    dev?.dailyOnS = status
                }
                
                DailyOff -> {
                    dev?.dailyOff = time
                    dev?.dailyOffS = status
                }
            }
            
            if (dev != null) {
                localService.updateCeilingLight(dev)
            }
            true
        } else
            false
    }
    
    fun setCountDownTime(min: Int) {
        MqttPublish.setCountDownTime(mqtt, min, model, bssid)
    }
    
    fun sleepTimeChange(time: Int) {
        MqttPublish.setSleepTime(mqtt, time, model, bssid)
    }
    
    fun finalStatusChange(status: Int) {
        MqttPublish.triggerCountDownTimeS(mqtt, status, model, bssid)
    }
    
    fun buzzerChange() {
        MqttPublish.setBuzzer(mqtt, model, bssid)
    }
    
    fun reset() {
        MqttPublish.reset(mqtt, model, bssid)
        val dev = CeilingLight(bssid)
        dev.uId = uId
        dev.model = model
        dev.name = devName.value
        dev.group = devLoc.value
        localService.updateCeilingLight(dev)
    }
    
    fun delete(): Boolean {
        val result = remoteService.deleteDevice(uId)
        return if (result != null) {
            if (result.isSuccess()) {
                val payload = JsonObject().apply { addProperty("config", "NETWORKRESET") }
                MqttPublish.reset(mqtt, model, bssid)
                mqtt.sendMsg(payload.toString(), "AD_CONFIG", bssid, MqttTopicTag.CONFIG)
                localService.deleteCeilingLight(uId)
                true
            } else
                false
        } else
            false
    }
    
}