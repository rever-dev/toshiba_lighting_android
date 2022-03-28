package com.sttptech.toshiba_lighting.Fragment.More

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.AppInfo
import com.sttptech.toshiba_lighting.Fragment.BaseViewModel

class MoreViewModel(application: Application) : BaseViewModel(application) {

    val version: MutableLiveData<String> = MutableLiveData()
    val account: MutableLiveData<String> = MutableLiveData()

    init {
        version.value = AppInfo.APP_VERSION
        account.value = repository.localS.getAccount()
    }
}