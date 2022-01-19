package com.sttptech.toshiba_lighting.Fragment.Member.Login

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.Fragment.BaseViewModel
import com.sttptech.toshiba_lighting.RetrofitUtil.ServerResponse

class LoginViewModel(application: Application) : BaseViewModel(application) {

    private val mainHandler = Handler(Looper.getMainLooper())

    var passwordVisibility: MutableLiveData<Boolean> = MutableLiveData()
    var loadStatus: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * login status
     * 0 : default
     * 1 : success
     * 2 : fail
     * */
    var loginStatus: MutableLiveData<Int> = MutableLiveData()

    init {
        passwordVisibility.value = false
        loginStatus.value = 0
    }

    fun login(account: String, password: String) {
        loadStatus.value = true
        Thread {
            val response: ServerResponse? =
                repository.remoteS.memberLogin(account, password)

            if (response == null || !response.isSuccess()) { // login fail
                mainHandler.post() {
                    loginStatus.value = 2
                    loadStatus.value = false
                }
            } else { // login success
                context.getSharedPreferences(KeyOfShp.SHP_NAME, Context.MODE_PRIVATE).edit {
                    putBoolean(KeyOfShp.SHP_LOGIN, true)
                    putString(KeyOfShp.SHP_ACCOUNT, account)
                    putString(KeyOfShp.SHP_TOKEN, response.getDatum()?.token)
                }
                mainHandler.post() {
                    loginStatus.value = 1
                    loadStatus.value = false
                }
            }
        }.start()
    }
}