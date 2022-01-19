package com.sttptech.toshiba_lighting.Fragment.More

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.AppInfo
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Fragment.BaseViewModel

class MoreViewModel(application: Application) : BaseViewModel(application) {

    val version: MutableLiveData<String> = MutableLiveData()
    val account: MutableLiveData<String> = MutableLiveData()

    init {
        version.value = AppInfo.APP_VERSION
        account.value = repository.localS.getAccount()
    }
}