package com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleScene

import com.sttptech.toshiba_lighting.Data.Bean.Scene

class SceneSelector {
    
    var selectScene: Scene? = null
    
    fun onSelectScene(scene: Scene) {
        selectScene = scene
    }
}