package com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
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

    companion object {

        private const val TAG = "EditTextDialog"
    }

    interface OnTextInputCallback {
        fun onTextInput(str: String?)
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

        vb.inpDiaTvCancel.setOnClickListener { dismiss() }
        vb.inpDiaTvConfirm.setOnClickListener {
            if (vb.inpDiaEditText.text.toString().trim().isNotEmpty()) {
                callback!!.onTextInput(vb.inpDiaEditText.text.toString())
                callback = null
                dismiss()
            } else
                dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (callback != null)
            callback!!.onTextInput(null)
    }
}