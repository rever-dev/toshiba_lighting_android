package com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleWeek

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.BaseViewModel

class ScheduleWeekViewModel(application: Application) : BaseViewModel(application) {

    val selectWeek: MutableLiveData<MutableSet<Int>> = MutableLiveData(mutableSetOf())
    
}