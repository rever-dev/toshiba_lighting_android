package com.sttptech.toshiba_lighting.DialogFragment.CDTPicker

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogCountDownTimePickerBinding

class CDTPickerDialogFragment : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.BOTTOM, true
), View.OnClickListener {
    
    interface OnTimeSelectListener {
        fun onSelected(time: Long?)
    }
    
    var listener: OnTimeSelectListener? = null
    
    private lateinit var vm: CDTPickerDialogViewModel
    
    private lateinit var vb: DialogCountDownTimePickerBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[CDTPickerDialogViewModel::class.java]
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = DialogCountDownTimePickerBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.cdtPickPickHour.minValue = 0
        vb.cdtPickPickHour.maxValue = vm.hours.size - 1
        vb.cdtPickPickHour.displayedValues = vm.hours.toTypedArray()
        
        vb.cdtPickPickMin.minValue = 0
        vb.cdtPickPickMin.maxValue = vm.mins.size - 1
        vb.cdtPickPickMin.displayedValues = vm.mins.toTypedArray()
        
        setListener()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        listener?.onSelected(null)
    }
    
    override fun onClick(v: View?) {
        when (v?.id) {
            
            R.id.cdtPick_tvCancel -> {
                dismiss()
            }
            
            R.id.cdtPick_tvConfirm -> {
                if (listener != null) {
                    val hour = vm.hours[vb.cdtPickPickHour.value].toLong() * 60
                    val min = vm.mins[vb.cdtPickPickMin.value].toLong()
                    val totalMin = (min.toInt() + hour)
                    listener?.onSelected(totalMin * 60 * 1000L)
                    listener = null
                }
                dismiss()
            }
        }
    }
    
    private fun setListener() {
        vb.cdtPickTvCancel.setOnClickListener(this)
        vb.cdtPickTvConfirm.setOnClickListener(this)
    }
}