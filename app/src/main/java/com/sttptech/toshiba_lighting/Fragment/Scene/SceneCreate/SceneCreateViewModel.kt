package com.sttptech.toshiba_lighting.Fragment.Scene.SceneCreate

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish

class SceneCreateViewModel(application: Application) : BaseViewModel(application) {
    
    var imageUri: MutableLiveData<Uri> = MutableLiveData()
    
    var imageByte: MutableLiveData<ByteArray> = MutableLiveData()
    
    var selectList: MutableLiveData<MutableList<Device>> = MutableLiveData()
    var groupList: MutableLiveData<List<Group>> = MutableLiveData()
    var deviceList: MutableLiveData<List<Device>> = MutableLiveData()
    
    var touchBlocker: FastTouchBlocker = FastTouchBlocker()
    
    init {
        Thread {
            deviceList.postValue(localService.allCeilingLights())
            selectList.postValue(mutableListOf())
            
            val groups = localService.allGroups()
            val tempGroups = groups?.toMutableList()
            if (groups != null) {
                for (group in groups) {
                    if (group.devUuids != null &&
                        group.devUuids!!.isEmpty())
                        tempGroups!!.remove(group)
                }
            }
            
            groupList.postValue(tempGroups?.toList())
        }.start()
    }
    
    fun saveScene(name: String): Boolean {
        
        val newScene = insertSceneToServer(name) ?: return false
        
        insertSceneToLocal(newScene)
        
        for (dev in selectList.value!!) {
            MqttPublish.triggerCustomMode(
                mqtt, newScene.seq!!, 1, dev.model!!, dev.macId
            )
        }
        
        return true
    }
    
    /** insert scene to server */
    private fun insertSceneToServer(name: String): Scene? {
    
        /** check seq code */
        var seqCode = 30
        var sceneList = localService.allScene()?.sortedBy { scene -> scene.seq }
        
        if (sceneList != null) {
            for (scene in sceneList) {
                if (seqCode != scene.seq) {
                    break
                } else
                    seqCode += 1
            }
        }
        
        /** insert scene */
        val insertResponse = remoteService
            .insertScene(seqCode, name, selectList.value!!.toList())
        
        /** if success, keep update scene image */
        if (insertResponse == null ||
            insertResponse.isSuccess().not() ||
            insertResponse.getDatum() == null
        ) return null
    
    
        val uUid = insertResponse.getDatum()?.grsituationUuid ?: return null
        val file = imageUri.value?.toFile() ?: return null
    
        val newScene = Scene(insertResponse.getDatum()!!.grsituationUuid!!)
        newScene.name = insertResponse.getDatum()!!.grsituationName
        newScene.seq = insertResponse.getDatum()!!.grsituationSeq
        newScene.def = "N"
        newScene.order = insertResponse.getDatum()!!.grsituationOrder
//        newScene.devUuids = kotlin.run {
//            if (selectList.value == null) return null
//
//            val strList: MutableList<String> = mutableListOf()
//            for (scene in selectList.value!!) {
//                strList.add(scene.uId!!)
//            }
//            strList.toList()
//        }
        newScene.devUuids = insertResponse.getDatum()!!.devUuids
        
        val imageResponse = remoteService.updateSceneImage(uUid, file)
        
        if (
            imageResponse == null ||
            imageResponse.isSuccess().not()
        ) return null
        
        newScene.image = imageByte.value
        newScene.imageUrl = imageResponse.getDatum()!!.grsituationImage
        
        return newScene
    }
    
    /** insert scene to local db */
    private fun insertSceneToLocal(scene: Scene) {
        localService.insertScene(scene)
    }
}
