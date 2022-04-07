package com.sttptech.toshiba_lighting.DialogFragment.VerificationCode

import android.app.Application
import com.sttptech.toshiba_lighting.BaseViewModel

class VerificationCodeViewModel(application: Application) : BaseViewModel(application) {
    
    fun verify(vCode: String): Boolean {
        val response = remoteService.bindShare(vCode)
        
        if (response == null || response.isSuccess().not()) return false
        
        return true
    }
}