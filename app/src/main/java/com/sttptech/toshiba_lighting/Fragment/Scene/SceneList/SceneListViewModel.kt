package com.sttptech.toshiba_lighting.Fragment.Scene.SceneList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish

class SceneListViewModel(application: Application) : BaseViewModel(application) {
    
    lateinit var sceneList: MutableLiveData<List<Scene>?>
    lateinit var mode: MutableLiveData<Boolean>
    
    init {
        Thread {
            sceneList = MutableLiveData()
            mode = MutableLiveData()
            
            sceneList.postValue(localService.allScene())
            mode.postValue(false)
        }.start()
    }
    
    fun triggerScene(seq: Int, scene: Scene) {
        val allDev = localService.allCeilingLights() ?: return
        for (dev in allDev) {
            MqttPublish.triggerCustomMode(mqtt, seq, 2, dev.model!!, dev.macId)
        }
    }
    
    fun refresh() {
        Thread {
            if (sceneList.value == null) sceneList = MutableLiveData()
            if (mode.value == null) mode = MutableLiveData()
            
            sceneList.postValue(localService.allScene())
            mode.postValue(false)
        }.start()
    }
    
    fun delete(uUid: String): Boolean {
        
        val response = remoteService.deleteScene(uUid)
        if (response == null || response.isSuccess().not()) return false
        
        localService.deleteSceneByUuid(uUid)
        
        return true
    }
    
}