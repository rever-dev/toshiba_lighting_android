package com.sttptech.toshiba_lighting.DialogFragment.Schedule.EditSchedule

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule
import com.sttptech.toshiba_lighting.R
import dev.weiqi.resof.stringOf

class EditScheduleViewModel(application: Application) : BaseViewModel(application) {
    
    val amPm: MutableLiveData<Array<String>> = MutableLiveData()
    val hours: MutableLiveData<Array<String>> = MutableLiveData()
    val minutes: MutableLiveData<Array<String>> = MutableLiveData()
    
    val selectWeek: MutableLiveData<List<Int>> = MutableLiveData()
    val selectScene: MutableLiveData<Scene?> = MutableLiveData()
    
    val touchBlocker = FastTouchBlocker()
    
    init {
        Thread {
            val amPmArray = mutableListOf(stringOf(R.string.am), stringOf(R.string.pm))
            val hoursArray = mutableListOf<String>()
            val minutesArray = mutableListOf<String>()
            for (i in 1..12) {
                if (i < 10)
                    hoursArray.add("0$i")
                else
                    hoursArray.add(i.toString())
            }
            
            for (i in 0..59) {
                if (i < 10)
                    minutesArray.add("0$i")
                else
                    minutesArray.add(i.toString())
            }
            
            amPm.postValue(amPmArray.toTypedArray())
            hours.postValue(hoursArray.toTypedArray())
            minutes.postValue(minutesArray.toTypedArray())
        }.start()
    }
    
    fun init(min: Int) {
        Thread {
            val amPmArray = mutableListOf(stringOf(R.string.am), stringOf(R.string.pm))
            val hoursArray = mutableListOf<String>()
            val minutesArray = mutableListOf<String>()
            for (i in 1..12) {
                if (i < 10)
                    hoursArray.add("0$i")
                else
                    hoursArray.add(i.toString())
            }
        
            for (i in 0..59) {
                if (i < 10)
                    minutesArray.add("0$i")
                else
                    minutesArray.add(i.toString())
            }
        
            amPm.postValue(amPmArray.toTypedArray())
            hours.postValue(hoursArray.toTypedArray())
            minutes.postValue(minutesArray.toTypedArray())
        }.start()
    }
    
    fun modifySchedule2Server(scheUuid: String, min: Int): Boolean {
        val response = remoteService.modifySchedule(scheUuid, selectScene.value!!.uId, selectWeek.value!!, min)
        
        if (response == null || response.isSuccess().not()) return false
        
        return true
    }
    
    fun getScene(uUid: String): Scene? {
        return localService.getSceneByUuid(uUid)
    }
}