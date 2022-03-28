package com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogfragEdittextBinding

class EditTextDialogFragment(
    var title: String,
    var textHint: String?,
    var inputText: String?
) : BaseDialogFragment(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.CENTER,
    true
) {

    interface OnTextInputCallback {
        fun onTextInput(str: String)
    }

    var callback: OnTextInputCallback? = null

    lateinit var vm: EdittextDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[EdittextDialogViewModel::class.java]
    }

    lateinit var vb: DialogfragEdittextBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogfragEdittextBinding.inflate(inflater, container, false)
        return vb.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.inpDiaTvTitle.text = title
        
        with(vb.inpDiaEditText) {
            hint = textHint
            setText(inputText)
        }
        
        setListener()
    }
    
    private fun setListener() {
        vb.inpDiaTvCancel.setOnClickListener { dismiss() }
        
        vb.inpDiaTvConfirm.setOnClickListener {
            if (callback != null) {
                val inputString = vb.inpDiaEditText.text.toString()
                if (inputString.trim().isNotEmpty()) {
                    callback!!.onTextInput(inputString)
                    dismiss()
                } else
                    Snackbar.make(it, R.string.nameIsNotEmpty, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}