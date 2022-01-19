package com.sttptech.toshiba_lighting.DialogFragment.GroupSelected

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class GroupsSelectedViewModel(application: Application) : AndroidViewModel(application) {

    companion object {

        private const val TAG = "GroupsSelector"
    }

    var roomList = MutableLiveData(mutableListOf("客廳", "房間", "臥室", "廁所", "廚房"))

    fun addGroup(group :String) {
        roomList.value!!.add(group)
    }




}