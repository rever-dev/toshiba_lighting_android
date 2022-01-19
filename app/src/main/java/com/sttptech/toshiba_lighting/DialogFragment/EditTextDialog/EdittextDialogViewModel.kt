package com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EdittextDialogViewModel : ViewModel() {

    companion object {

        private const val TAG = "EDitTextDialog"
    }

    var title =  MutableLiveData<String?>(null)
    var hint =  MutableLiveData<String?>(null)
    var inputText =  MutableLiveData<String?>(null)
}