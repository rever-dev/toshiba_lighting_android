package com.sttptech.toshiba_lighting.Fragment.SceneSchedule.ScheduleList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule

class ScheduleListViewModel(application: Application) : BaseViewModel(application) {
    
    val scheList: MutableLiveData<List<SceneSchedule>?> = MutableLiveData()
    val loadingStatus: MutableLiveData<Boolean> = MutableLiveData(false)
    val touchBlocker: FastTouchBlocker = FastTouchBlocker()
    
    fun init() {
        
        loadingStatus.value = true
        
        Thread {
            val response = remoteService.getSchedule()
            if (response != null &&
                response.isSuccess() &&
                response.getDatum()!!.infos != null
            ) {
                
                val scheduleList = mutableListOf<SceneSchedule>()
                for (sche in response.getDatum()!!.infos!!) {
                    
                    /* payload */
                    val payloadList = mutableListOf<SceneSchedule.PayloadDTO>()
                        .apply {
                            for (payload in sche.payload!!) {
                                val temp = SceneSchedule.PayloadDTO(
                                    payload.cycleTaskId,
                                    payload.dayOfWeek,
                                    payload.minuteOfDay,
                                    payload.act,
                                    payload.grsituationUuid
                                )
                                add(temp)
                            }
                        }.toList()
                
                    /* name */
                    val sceneName = localService.getSceneByUuid(sche.payload!![0].grsituationUuid!!)?.name
                
                    val schedule = SceneSchedule(
                        sche.taskId,
                        sche.taskCode,
                        sche.taskSeq,
                        sche.cycleTaskType,
                        sche.cycleTaskMode,
                        sche.statusDb,
                        payloadList,
                        sceneName
                    )
                    
                    Logger.i("schedule: \n$schedule")
                    
                    scheduleList.add(schedule)
                }
                scheduleList.sort()
                scheList.postValue(scheduleList)
                loadingStatus.postValue(false)
            } else
                scheList.postValue(null)
                loadingStatus.postValue(false)
        }.start()
        
    }
    
    fun scheduleOnOff(taskId: String, onOff: Boolean): Boolean {
        val response = remoteService.scheduleOnOff(taskId, onOff)
        
        if (response == null || response.isSuccess().not()) return false
        
        return true
    }
    
    fun scheduleDelete(taskId: String): Boolean {
        val response = remoteService.deleteSchedule(taskId)
        
        if (response == null || response.isSuccess().not()) return false
        
        return true
    }
    
}