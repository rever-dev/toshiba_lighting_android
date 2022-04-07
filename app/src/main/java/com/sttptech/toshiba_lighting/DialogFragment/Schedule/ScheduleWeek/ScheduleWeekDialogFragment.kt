package com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleWeek

import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogScheduleSelectWeekBinding

class ScheduleWeekDialogFragment(val currentWeek: List<Int>?) : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.MATCH_PARENT,
    Gravity.BOTTOM,
    true
), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    
    interface WeekSelectCallback {
        fun onWeekSelect(weekList: List<Int>)
    }
    
    var weekSelectCallback: WeekSelectCallback? = null
    
    lateinit var vm: ScheduleWeekViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[ScheduleWeekViewModel::class.java]
    }
    
    lateinit var vb: DialogScheduleSelectWeekBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogScheduleSelectWeekBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListener()
        observerVM()
        
        if (currentWeek != null && currentWeek.isNotEmpty()) {
            for (i in currentWeek) {
                when (i) {
                    
                    1 -> {
                        vb.scheWeekCb1.isChecked = true
                    }
    
                    2 -> {
                        vb.scheWeekCb2.isChecked = true
                    }
    
                    3 -> {
                        vb.scheWeekCb3.isChecked = true
                    }
    
                    4 -> {
                        vb.scheWeekCb4.isChecked = true
                    }
    
                    5 -> {
                        vb.scheWeekCb5.isChecked = true
                    }
    
                    6 -> {
                        vb.scheWeekCb6.isChecked = true
                    }
    
                    7 -> {
                        vb.scheWeekCb7.isChecked = true
                    }
                }
            }
        }
        
    }
    
    private fun observerVM() {
        vm.selectWeek.observe(viewLifecycleOwner) {
        
        }
    }
    
    private fun setListener() {
        vb.scheWeekTvBack.setOnClickListener(this)
        
        vb.scheWeekCons1.setOnClickListener(this)
        vb.scheWeekCons2.setOnClickListener(this)
        vb.scheWeekCons3.setOnClickListener(this)
        vb.scheWeekCons4.setOnClickListener(this)
        vb.scheWeekCons5.setOnClickListener(this)
        vb.scheWeekCons6.setOnClickListener(this)
        vb.scheWeekCons7.setOnClickListener(this)
        
        vb.scheWeekCb1.setOnCheckedChangeListener(this)
        vb.scheWeekCb2.setOnCheckedChangeListener(this)
        vb.scheWeekCb3.setOnCheckedChangeListener(this)
        vb.scheWeekCb4.setOnCheckedChangeListener(this)
        vb.scheWeekCb5.setOnCheckedChangeListener(this)
        vb.scheWeekCb6.setOnCheckedChangeListener(this)
        vb.scheWeekCb7.setOnCheckedChangeListener(this)
    }
    
    override fun onClick(v: View?) {
        
        when (v?.id) {
            
            R.id.scheWeek_tvBack -> {
                weekSelectCallback?.onWeekSelect(vm.selectWeek.value!!.toList().sorted())
                dismiss()
            }
            
            R.id.scheWeek_cons1 -> {
                vb.scheWeekCb1.isChecked = vb.scheWeekCb1.isChecked.not()
            }
            
            R.id.scheWeek_cons2 -> {
                vb.scheWeekCb2.isChecked = vb.scheWeekCb2.isChecked.not()
            }
            
            R.id.scheWeek_cons3 -> {
                vb.scheWeekCb3.isChecked = vb.scheWeekCb3.isChecked.not()
            }
            
            R.id.scheWeek_cons4 -> {
                vb.scheWeekCb4.isChecked = vb.scheWeekCb4.isChecked.not()
            }
            
            R.id.scheWeek_cons5 -> {
                vb.scheWeekCb5.isChecked = vb.scheWeekCb5.isChecked.not()
            }
            
            R.id.scheWeek_cons6 -> {
                vb.scheWeekCb6.isChecked = vb.scheWeekCb6.isChecked.not()
            }
            
            R.id.scheWeek_cons7 -> {
                vb.scheWeekCb7.isChecked = vb.scheWeekCb7.isChecked.not()
            }
        }
    }
    
    override fun onCheckedChanged(btn: CompoundButton?, isChecked: Boolean) {
        
        when (btn?.id) {
            
            R.id.scheWeek_cb1 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(1)
                else
                    vm.selectWeek.value?.remove(1)
            }
            
            R.id.scheWeek_cb2 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(2)
                else
                    vm.selectWeek.value?.remove(2)
            }
            
            R.id.scheWeek_cb3 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(3)
                else
                    vm.selectWeek.value?.remove(3)
            }
            
            R.id.scheWeek_cb4 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(4)
                else
                    vm.selectWeek.value?.remove(4)
            }
            
            R.id.scheWeek_cb5 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(5)
                else
                    vm.selectWeek.value?.remove(5)
            }
            
            R.id.scheWeek_cb6 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(6)
                else
                    vm.selectWeek.value?.remove(6)
            }
            
            R.id.scheWeek_cb7 -> {
                if (isChecked)
                    vm.selectWeek.value?.add(7)
                else
                    vm.selectWeek.value?.remove(7)
            }
        }
        
        Logger.i("current week list info: ${vm.selectWeek.value?.sorted()}")
    }
}