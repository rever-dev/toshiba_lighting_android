package com.sttptech.toshiba_lighting.Fragment.Scene.SceneList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish

class SceneListViewModel(application: Application) : BaseViewModel(application) {
    
    var sceneList: MutableLiveData<List<Scene>?> = MutableLiveData()
    var mode: MutableLiveData<Boolean> = MutableLiveData(false)
    
    init {
        Thread {
            val sceneData = localService.allScene() ?: return@Thread
            sceneData.sortedBy { scene -> scene.order }
            sceneList.postValue(sceneData)
            
            Logger.i("all scene data (init): $sceneData")
        }.start()
    }
    
    fun triggerScene(seq: Int, scene: Scene) {
        val devList = mutableListOf<Device>()
    
        for (devUuid in scene.devUuids!!) {
            val dev = localService.getCeilingLightById(devUuid) ?: return
            devList.add(dev)
        }
    
        for (dev in devList) {
            MqttPublish.triggerCustomMode(mqtt, scene.seq!!, 2, dev.model!!, dev.macId)
        }
    }
    
    fun refresh() {
        Thread {
            val newData = localService.allScene() ?:return@Thread
            newData.sortedBy { scene -> scene.order }
            sceneList.postValue(newData)
    
            Logger.i("all scene data (refresh): $newData")
        }.start()
    }
    
    fun delete(uUid: String): Boolean {
        
        val response = remoteService.deleteScene(uUid)
        if (response == null || response.isSuccess().not()) return false
        
        localService.deleteSceneByUuid(uUid)
        
        return true
    }
    
    fun updateSceneOrder(newList: List<Scene>): Boolean {
        val response = remoteService.sortScene(newList)
        if (response == null || response.isSuccess().not()) return false
        
        for (newOrder in newList.indices) {
            newList[newOrder].order = newOrder + 1
        }
        
        localService.insertAllScene(newList)
        
        return true
    }
    
}