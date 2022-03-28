package com.sttptech.toshiba_lighting.DialogFragment.TimePicker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogTimePickerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimePickerDialogFragment : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.BOTTOM, true
), View.OnClickListener {
    
    interface TimePickListener {
        fun onPick(time: Long?)
    }
    
    var listener: TimePickListener? = null
    
    enum class AmPm {
        上午, 下午
    }
    
    private lateinit var vm: TimePickerViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[TimePickerViewModel::class.java]
    }
    
    private lateinit var vb: DialogTimePickerBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = DialogTimePickerBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.timePickerPickAmPm.minValue = 0
        vb.timePickerPickAmPm.maxValue = vm.amPm.size - 1
        vb.timePickerPickAmPm.displayedValues = vm.amPm.toTypedArray()
        
        vb.timePickerPickHours.minValue = 0
        vb.timePickerPickHours.maxValue = vm.hour.size - 1
        vb.timePickerPickHours.displayedValues = vm.hour.toTypedArray()
        
        vb.timePickerPickMin.minValue = 0
        vb.timePickerPickMin.maxValue = vm.min.size - 1
        vb.timePickerPickMin.displayedValues = vm.min.toTypedArray()
        
        setListener()
    }
    
    private fun setListener() {
        vb.timePickerTvCancel.setOnClickListener(this)
        vb.timePickerTvConfirm.setOnClickListener(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (listener != null)
            listener?.onPick(null)
    }
    
    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        
        when (v?.id) {
            
            R.id.timePicker_tvCancel -> {
                dismiss()
            }
            
            R.id.timePicker_tvConfirm -> {
                val timeStr = "2020-01-01-${vm.amPm[vb.timePickerPickAmPm.value]} " +
                        "${vm.hour[vb.timePickerPickHours.value]}:" + vm.min[vb.timePickerPickMin.value]
                val inputFormat = SimpleDateFormat("yyyy-MM-dd-a KK:mm", Locale.TAIWAN)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    
                val date = inputFormat.parse(timeStr)
                listener?.onPick(date.time)
                listener = null
                
                Logger.i("Selected time: ${outputFormat.format(Date(date.time))}")
                
                dismiss()
            }
        }
    }
}