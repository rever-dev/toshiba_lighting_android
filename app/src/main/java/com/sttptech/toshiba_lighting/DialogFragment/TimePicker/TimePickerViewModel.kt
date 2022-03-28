package com.sttptech.toshiba_lighting.DialogFragment.TimePicker

import androidx.lifecycle.ViewModel
import com.sttptech.toshiba_lighting.DialogFragment.TimePicker.TimePickerDialogFragment.AmPm


class TimePickerViewModel : ViewModel() {
    
    val amPm = mutableListOf<String>()
    val hour =  mutableListOf<String>()
    val min = mutableListOf<String>()
    
    init {
        
        amPm.add(AmPm.上午.name)
        amPm.add(AmPm.下午.name)
        
        for (i in 0 until 13) {
            if (i < 10)
                hour.add("0$i")
            else
                hour.add(i.toString())
        }
        
        for (i in 0 until 60) {
            if (i < 10)
                min.add("0$i")
            else
                min.add(i.toString())
        }
    }
    
    
}