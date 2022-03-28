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
    lateinit var imageUri: MutableLiveData<Uri>
    
    lateinit var imageByte: MutableLiveData<ByteArray>
    
    lateinit var selectList: MutableLiveData<MutableList<Device>>
    lateinit var groupList: MutableLiveData<List<Group>>
    lateinit var deviceList: MutableLiveData<List<Device>>
    
    var touchBlocker: FastTouchBlocker = FastTouchBlocker()
    
    init {
        imageUri = MutableLiveData()
        
        imageByte = MutableLiveData()
        selectList = MutableLiveData()
        groupList = MutableLiveData()
        deviceList = MutableLiveData()
        
        Thread {
            deviceList.postValue(localService.allCeilingLights())
            groupList.postValue(localService.allGroups())
            selectList.postValue(mutableListOf())
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
        val sceneList = localService.allScene()?.toList()
        
        if (sceneList != null) {
            sceneList.sortedBy { seqCode }
            for (scene in sceneList) {
                if (seqCode != scene.seq) break
                else seqCode++
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
        newScene.name = name
        newScene.seq = seqCode
        newScene.def = "N"
        newScene.order = sceneList?.size?.plus(1)
        newScene.devUuids = kotlin.run {
            if (selectList.value == null) return null
            
            val strList: MutableList<String> = mutableListOf()
            for (scene in selectList.value!!) {
                strList.add(scene.uId!!)
            }
            strList.toList()
        }
        
        val imageResponse = remoteService.updateSceneImage(uUid, file)
        
        if (
            imageResponse == null ||
            imageResponse.isSuccess().not()
        ) return null
        
        newScene.image = imageByte.value
//        newScene.imageUrl = imageResponse.getDatum().
        
        return newScene
    }
    
    /** insert scene to local db */
    private fun insertSceneToLocal(scene: Scene) {
        localService.insertScene(scene)
    }
}
