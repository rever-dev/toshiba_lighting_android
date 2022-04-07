package com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleScene

import android.app.Application
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Scene

class ScheduleSceneViewModel(application: Application) : BaseViewModel(application) {
    
    val sceneList: MutableLiveData<List<Scene>> = MutableLiveData()
    val selector: SceneSelector = SceneSelector()
    
    init {
        Thread {
            val sceneList = localService.allScene()
            this.sceneList.postValue(sceneList)
        }.start()
    }
    
    inner class SceneSelector {
        
        var selectScene: Scene? = null
        private var checkBox: CheckBox? = null
        
        fun onSelectScene(scene: Scene, checkBox: CheckBox?) {
            this.checkBox?.isChecked = false
            this.checkBox = checkBox
            this.selectScene = scene
            
            Logger.i("current select scene: ${scene.name}")
        }
        
        fun unSelectScene(scene: Scene) {
            if (selectScene?.uId == scene.uId) {
                selectScene = null
                checkBox = null
                
                Logger.i("unselect scene: ${scene.name}")
            }
        }
    }
    
}