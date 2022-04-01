package com.sttptech.toshiba_lighting.Fragment.SceneSchedule.ScheduleList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.BaseViewModel
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule

class ScheduleListViewModel(application: Application) : BaseViewModel(application) {
    
    val scheList: MutableLiveData<List<SceneSchedule>> = MutableLiveData()
    
    fun init() {
        Thread{
            val response = remoteService.getSchedule()
            if (response != null && response.isSuccess()) {
            
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
                scheList.postValue(scheduleList)
            }
        }.start()
    }
}