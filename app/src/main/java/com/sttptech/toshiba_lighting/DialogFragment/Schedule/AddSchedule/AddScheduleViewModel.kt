package com.sttptech.toshiba_lighting.DialogFragment.Schedule.AddSchedule

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule
import com.sttptech.toshiba_lighting.R
import dev.weiqi.resof.stringOf

class AddScheduleViewModel(application: Application) : BaseViewModel(application) {
    
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
    
    fun addSchedule2Server(min: Int): SceneSchedule? {
        val response = remoteService.addSchedule(selectScene.value!!.uId, selectWeek.value!!, min)
        
        if (response == null || response.isSuccess().not()) return null
        
        val payloadList = mutableListOf<SceneSchedule.PayloadDTO>().apply {
            for (payload in response.getDatum()!!.payload!!) {
                val temp = SceneSchedule.PayloadDTO(
                    payload.cycleTaskId,
                    payload.dayOfWeek,
                    payload.minuteOfDay,
                    payload.act,
                    payload.grsituationUuid
                )
                add(temp)
            }
        }
        
        val resSche = response.getDatum() ?: return null
        
        return SceneSchedule(
            resSche.taskId,
            resSche.taskCode,
            resSche.taskSeq,
            resSche.cycleTaskType,
            resSche.cycleTaskMode,
            resSche.statusDb,
            payloadList,
            localService.getSceneByUuid(payloadList[0].grsituationUuid!!)?.name
        )
    }
}