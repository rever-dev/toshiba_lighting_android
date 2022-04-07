package com.sttptech.toshiba_lighting.DialogFragment.Schedule.AddSchedule

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleScene.ScheduleSceneDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleWeek.ScheduleWeekDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogScheduleAddBinding
import dev.weiqi.resof.colorIntOf
import dev.weiqi.resof.stringOf

class AddScheduleDialogFragment : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.MATCH_PARENT,
    Gravity.BOTTOM,
    true
), View.OnClickListener {
    
    interface AddScheduleCallback {
        fun onAddSchedule(sche: SceneSchedule)
    }
    
    var addScheduleCallback: AddScheduleCallback? = null
    
    lateinit var vm: AddScheduleViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[AddScheduleViewModel::class.java]
    }
    
    lateinit var vb: DialogScheduleAddBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogScheduleAddBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListener()
        observerVM()
    }
    
    private fun observerVM() {
        vm.amPm.observe(viewLifecycleOwner) {
            val picker = vb.scheAddPkAmpm
            picker.maxValue = 1
            picker.displayedValues = it
        }
        
        vm.hours.observe(viewLifecycleOwner) {
            val picker = vb.scheAddPkHours
            picker.maxValue = 11
            picker.displayedValues = it
        }
        
        vm.minutes.observe(viewLifecycleOwner) {
            val picker = vb.scheAddPkMin
            picker.maxValue = 59
            picker.displayedValues = it
        }
        
        vm.selectScene.observe(viewLifecycleOwner) {
            vb.scheAddTvScene.text = it?.name
        }
        
        vm.selectWeek.observe(viewLifecycleOwner) {
            // week
            val strBuffer = StringBuffer()
            var checker = 0
            
            for (i in it) {
                strBuffer.append(" ")
                when (i) {
                    1 -> {
                        strBuffer.append(stringOf(R.string.monday))
                        checker++
                    }
                    2 -> {
                        strBuffer.append(stringOf(R.string.tuesday))
                        checker += 2
                    }
                    3 -> {
                        strBuffer.append(stringOf(R.string.wednesday))
                        checker += 4
                    }
                    4 -> {
                        strBuffer.append(stringOf(R.string.thursday))
                        checker += 8
                    }
                    5 -> {
                        strBuffer.append(stringOf(R.string.friday))
                        checker += 16
                    }
                    6 -> {
                        strBuffer.append(stringOf(R.string.saturday))
                        checker += 32
                    }
                    7 -> {
                        strBuffer.append(stringOf(R.string.sunday))
                        checker += 64
                    }
                }
            }
            
            when (checker) {
                31 -> vb.scheAddTvWeek.text = stringOf(R.string.everyWorkingDay)
                96 -> vb.scheAddTvWeek.text = stringOf(R.string.everyWeekend)
                127 -> vb.scheAddTvWeek.text = stringOf(R.string.everyDay)
                else -> vb.scheAddTvWeek.text = strBuffer.toString()
            }
        }
    }
    
    private fun setListener() {
        vb.scheAddTvCancel.setOnClickListener(this)
        vb.scheAddTvSave.setOnClickListener(this)
        vb.scheAddConsScene.setOnClickListener(this)
        vb.scheAddConsWeek.setOnClickListener(this)
    }
    
    private fun convertToMinute(): Int {
        var totalMin: Int = 0
        
        val amPm = vb.scheAddPkAmpm
        val hours = vb.scheAddPkHours
        val minutes = vb.scheAddPkMin
        
        if (amPm.value == 0 && hours.value == 11) {
            totalMin = minutes.value
            
            Logger.i("total minute: $totalMin")
            return totalMin
        } else if (amPm.value == 1 && hours.value == 11) {
            totalMin = minutes.value + 720
            
            Logger.i("total minute: $totalMin")
            return totalMin
        }
        
        totalMin += if (amPm.value == 0) 0 else 720
        totalMin += (hours.value + 1) * 60
        totalMin += minutes.value
        
        Logger.i("total minute: $totalMin")
        
        return totalMin
    }
    
    override fun onClick(v: View?) {
        
        if (vm.touchBlocker.onTouch().not()) return
        
        when (v?.id) {
            
            R.id.scheAdd_tvCancel -> {
                dismiss()
            }
            
            R.id.scheAdd_tvSave -> {
                if (vm.selectScene.value == null) {
                    Snackbar.make(requireView(), R.string.mustSelectScene, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                        .show()
                    return
                }
                
                if (vm.selectWeek.value == null || vm.selectWeek.value!!.isEmpty()) {
                    Snackbar.make(requireView(), R.string.mustSelectWeek, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                        .show()
                    return
                }
                
                val loading = BaseApplication.loadingView
                
                loading.show(parentFragmentManager, null)
                Thread {
                    val result = vm.addSchedule2Server(convertToMinute())
                    requireActivity().runOnUiThread {
                        if (result != null) {
                            Snackbar.make(requireView(), R.string.scheduleAddSuccess, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                .show()
                            addScheduleCallback?.onAddSchedule(result)
                            dismiss()
                        } else {
                            Snackbar.make(requireView(), R.string.scheduleAddFail, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                .show()
                        }
                        loading.dismiss()
                    }
                }.start()
            }
            
            R.id.scheAdd_consScene -> {
                ScheduleSceneDialogFragment(vm.selectScene.value).apply {
                    sceneSelectCallback = object : ScheduleSceneDialogFragment.SceneSelectCallback {
                        override fun onSceneSelect(scene: Scene?) {
                            vm.selectScene.value = scene
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            
            R.id.scheAdd_consWeek -> {
                ScheduleWeekDialogFragment(vm.selectWeek.value).apply {
                    weekSelectCallback = object : ScheduleWeekDialogFragment.WeekSelectCallback {
                        override fun onWeekSelect(weekList: List<Int>) {
                            this@AddScheduleDialogFragment.vm.selectWeek.value = weekList
                        }
                    }
                }.show(parentFragmentManager, null)
            }
        }
    }
}