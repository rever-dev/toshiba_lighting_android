package com.sttptech.toshiba_lighting.DialogFragment.CDTPicker

import androidx.lifecycle.ViewModel

class CDTPickerDialogViewModel : ViewModel() {
    
    val hours = mutableListOf<String>()
    val mins = mutableListOf<String>()
    
    init {
        for (i in 0 until 7) {
            hours.add(i.toString())
        }
    
        for (i in 0 until 60) {
            mins.add(i.toString())
        }
    }
}