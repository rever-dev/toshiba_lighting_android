package com.sttptech.toshiba_lighting.Fragment.Scene.SceneEdit

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.AppKey.CROP_PICTURE_NAME
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish
import java.io.File
import java.io.FileOutputStream


class SceneEditViewModel(application: Application) : BaseViewModel(application) {
    
    var imageUri: MutableLiveData<Uri> = MutableLiveData()
    
    var imageByte: MutableLiveData<ByteArray> = MutableLiveData()
    
    var scene: MutableLiveData<Scene> = MutableLiveData()
    var selectList: MutableLiveData<MutableList<Device>> = MutableLiveData()
    var groupList: MutableLiveData<List<Group>> = MutableLiveData()
    var deviceList: MutableLiveData<List<Device>> = MutableLiveData()
    
    var touchBlocker: FastTouchBlocker = FastTouchBlocker()
    
    fun init(seq: Int?) {
        Thread {
            if (seq == null) return@Thread
            val scene = localService.getSceneBySeq(seq) ?: return@Thread
            this.scene.postValue(scene)
            
            groupList.postValue(localService.allGroups())
            localService.allCeilingLights().apply {
                
                if (this == null) return@apply
                
                deviceList.postValue(this)
                
                val tempSelectList = mutableListOf<Device>()
                if (scene.devUuids != null) {
                    for (dev in this) {
                        if (scene.devUuids!!.contains(dev.uId!!))
                            tempSelectList.add(dev)
                    }
                }
                
                selectList.postValue(tempSelectList)
                
            } ?: return@Thread
            
            scene.image.apply {
                
                if (this == null) return@apply
                
                imageByte.postValue(this)
                
                
                var file = getApplication<BaseApplication>()
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                file = File(file, CROP_PICTURE_NAME)
                
                val os = FileOutputStream(file)
                os.write(this)
                os.close()
                
                imageUri.postValue(Uri.fromFile(file))
                
            }
        }.start()
    }
    
    fun saveScene(name: String?): Boolean {
        
        if (selectList.value == null) return false
        
        val oldDevUuid = scene.value?.devUuids ?: return false
        val newDevUuid = mutableListOf<String>()
        val addDevUuids = mutableListOf<String>()
        val removeDevUuids = mutableListOf<String>()
        
        for (selectDev in selectList.value!!) {
            newDevUuid.add(selectDev.uId!!)
        }
        
        for (newDev in newDevUuid) {
            if (oldDevUuid.contains(newDev).not())
                addDevUuids.add(newDev)
        }
        
        for (oldDev in oldDevUuid) {
            if (newDevUuid.contains(oldDev).not())
                removeDevUuids.add(oldDev)
        }
        
        val newScene = updateScene2Server(
            scene.value!!.uId,
            addDevUuids.toList(),
            removeDevUuids.toList(),
            name
        )
            ?: return false
        
        updateScene2Local(newScene)
        
        for (dev in selectList.value!!) {
            MqttPublish.triggerCustomMode(
                mqtt, newScene.seq!!, 1, dev.model!!, dev.macId
            )
        }
        
        return true
    }
    
    private fun updateScene2Server(
        uId: String,
        addDevs: List<String>,
        removeDevs: List<String>,
        name: String?
    ): Scene? {
        val response = remoteService.updateScene(uId, addDevs, removeDevs, name)
        
        // update scene info
        if (response != null && response.isSuccess()) {
            val newScene = this.scene.value!!.copy()
            newScene.name = response.getDatum()!!.grsituationName
            newScene.seq = response.getDatum()!!.grsituationSeq
            newScene.devUuids = response.getDatum()!!.devUuids
            newScene.order = response.getDatum()!!.grsituationOrder
            
            // keep update scene image
            val file = imageUri.value?.toFile() ?: return null
            
            val imageResponse = remoteService.updateSceneImage(newScene.uId, file)
            if (imageResponse != null && imageResponse.isSuccess()) {
                newScene.image = imageByte.value
                newScene.imageUrl = imageResponse.getDatum()!!.grsituationImage
                return newScene
            }
        }
        return null
    }
    
    private fun updateScene2Local(newScene: Scene) {
        localService.insertScene(newScene)
    }
    
    
}