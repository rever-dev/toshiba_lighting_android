package com.sttptech.toshiba_lighting.DialogFragment

import android.os.Bundle
import android.view.*
import android.widget.NumberPicker
import androidx.lifecycle.ViewModelProvider
import com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog.EditTextDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog.EditTextDialogFragment.OnTextInputCallback
import com.sttptech.toshiba_lighting.DialogFragment.GroupSelected.GroupsSelectedViewModel
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogfragGroupSelectorBinding

class GroupsSelectedDialogFragment : BaseDialogFragment(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.CENTER,
    true
) {

    companion object { private const val TAG: String = "GroupSelector" }

    interface GroupSelectedCallback {
        fun onGroupSelected(group: String?)
    }

    private lateinit var vm: GroupsSelectedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[GroupsSelectedViewModel::class.java]
    }

    private lateinit var vb: DialogfragGroupSelectorBinding

    var groupSelCallback: GroupSelectedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogfragGroupSelectorBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(vb.groupSelPicker) {
            minValue = 0
            maxValue = vm.roomList.value!!.size - 1
            displayedValues = vm.roomList.value!!.toTypedArray()
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
        }
        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (groupSelCallback != null)
            groupSelCallback!!.onGroupSelected(null)
    }

    private fun setListener() {
        vb.groupSelTvCancel.setOnClickListener { dismiss() }
        vb.groupSelTvConfirm.setOnClickListener {
            groupSelCallback?.onGroupSelected(vm.roomList.value!![vb.groupSelPicker.value])
            groupSelCallback = null
            dismiss()
        }
        vb.groupSelTvCustom.setOnClickListener {
            EditTextDialogFragment(
                getString(R.string.customYourGroup),
                getString(R.string.customYourGroupHint),
                null
            ).apply {
                callback = object : OnTextInputCallback {
                    override fun onTextInput(str: String) {
                        this@GroupsSelectedDialogFragment.let {
                            it.vm.addGroup(str)
                            it.vb.groupSelPicker.let { picker ->
                                picker.displayedValues = it.vm.roomList.value!!.toTypedArray()
                                picker.maxValue = it.vm.roomList.value!!.size - 1
                                picker.value = it.vm.roomList.value!!.size - 1
                            }
                        }
                    }
                }
            }.show(parentFragmentManager, null)
        }
    }
}