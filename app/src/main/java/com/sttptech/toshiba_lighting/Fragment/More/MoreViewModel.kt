package com.sttptech.toshiba_lighting.Fragment.More

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.AppInfo
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.AppUtil.FastTouchBlocker
import com.sttptech.toshiba_lighting.BaseViewModel

class MoreViewModel(application: Application) : BaseViewModel(application) {
    
    val version: MutableLiveData<String> = MutableLiveData()
    val account: MutableLiveData<String> = MutableLiveData()
    val shareStatus: MutableLiveData<Boolean> = MutableLiveData()
    val shareEmail: MutableLiveData<String?> = MutableLiveData()
    
    val shp = application.getSharedPreferences(
        AppKey.SHP_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    
    val touchBlocker: FastTouchBlocker = FastTouchBlocker()
    
    init {
        version.value = AppInfo.APP_VERSION
        account.value = localService.getAccount()
        val shareS = shp.getBoolean(AppKey.SHP_SHARE, false)
        shareStatus.value = shareS
        if (shareS)
            shareEmail.value = shp.getString(AppKey.SHP_SHARE_EMAIL, null)
        else
            shareEmail.value = null
    }
    
    fun refresh() {
        val shareS = shp.getBoolean(AppKey.SHP_SHARE, false)
        shareStatus.value = shareS
        if (shareS)
            shareEmail.value = shp.getString(AppKey.SHP_SHARE_EMAIL, null)
        else
            shareEmail.value = null
    }
    
    fun inviteMember(account: String): Boolean {
        val response = remoteService.inviteMember(account)
        
        if (response == null || response.isSuccess().not()) return false
        
        return true
    }
    
//    fun bindShare(verifyCode: String) {
//        remoteService.bindShare(verifyCode)
//    }
    
    fun unbindShare(): Boolean {
        val response = remoteService.unbindShare()
        
        if (response == null || response.isSuccess().not()) return false
        
        return true
    }
}