package com.sttptech.toshiba_lighting.Fragment.Device.DeviceSettings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import com.google.android.material.snackbar.Snackbar
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceSettings.*
import com.sttptech.toshiba_lighting.DialogFragment.CDTPicker.CDTPickerDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog.EditTextDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.GroupsSelectedDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.TimePicker.TimePickerDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentDeviceSettingsBinding
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class DeviceSettingsFragment : Fragment(),
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener,
    OnToggledListener {
    
    private lateinit var vm: DeviceSettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[DeviceSettingsViewModel::class.java]
    }
    
    private lateinit var vb: FragmentDeviceSettingsBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentDeviceSettingsBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init(requireArguments().getString(AppKey.DEVICE_UID, ""))
        observerVM()
    }
    
    override fun onStop() {
        super.onStop()
        
        vm.unListenTopic()
    }
    
    override fun onClick(v: View?) {
        
        if (vm.touchBlocker.onTouch().not())
            return
        
        when (v!!.id) {
            
            R.id.devSet_tvBack -> {
                Navigation.findNavController(requireView()).popBackStack()
            }
            
            R.id.devSet_ibtEditName -> {
                EditTextDialogFragment(
                    getString(R.string.renameMessage),
                    vm.devName.value,
                    vm.devName.value,
                ).apply {
                    callback = object : EditTextDialogFragment.OnTextInputCallback {
                        override fun onTextInput(str: String) {
                            Thread {
                                with(this@DeviceSettingsFragment) {
                                    val result = vm.modifyName(str)
                                    requireActivity().runOnUiThread {
                                        val msgText =
                                            if (result) R.string.renameSuccess else R.string.renameFail
                                        val msgColor =
                                            if (result)
                                                resources.getColor(R.color.snackBar_success, null)
                                            else
                                                resources.getColor(R.color.snackBar_fail, null)
                                        
                                        Snackbar.make(
                                            requireView(),
                                            msgText,
                                            Snackbar.LENGTH_SHORT
                                        )
                                            .setBackgroundTint(msgColor)
                                            .show()
                                    }
                                }
                            }.start()
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            R.id.devSet_ibtEditGroup -> {
                GroupsSelectedDialogFragment()
                    .apply {
                        groupSelCallback =
                            object : GroupsSelectedDialogFragment.GroupSelectedCallback {
                                override fun onGroupSelected(group: String?) {
                                    if (group != null) {
                                        Thread {
                                            val result = vm.modifyDeviceGroup(group)
                                            with(this@DeviceSettingsFragment) {
                                                requireActivity().runOnUiThread {
                                                    val msgText =
                                                        if (result) R.string.groupUpdateSuccess else R.string.groupUpdateFail
                                                    val msgColor =
                                                        if (result)
                                                            resources.getColor(
                                                                R.color.snackBar_success,
                                                                null
                                                            )
                                                        else
                                                            resources.getColor(
                                                                R.color.snackBar_fail,
                                                                null
                                                            )
                                                    
                                                    Snackbar.make(
                                                        requireView(),
                                                        msgText,
                                                        Snackbar.LENGTH_SHORT
                                                    )
                                                        .setBackgroundTint(msgColor)
                                                        .show()
                                                }
                                            }
                                        }.start()
                                    }
                                }
                            }
                    }
                    .show(parentFragmentManager, null)
            }
            
            /** wake up */
            R.id.devSet_tvWakup -> {
                TimePickerDialogFragment().apply {
                    listener = object : TimePickerDialogFragment.TimePickListener {
                        @SuppressLint("SimpleDateFormat")
                        override fun onPick(time: Long?) {
                            if (time != null) {
                                Thread {
                                    val result = vm.modifySchedule(Wakeup, time, true)
                                    
                                    with(this@DeviceSettingsFragment) {
                                        requireActivity().runOnUiThread {
                                            
                                            if (result) {
                                                showSnackBarSchedule(
                                                    getString(R.string.dailyUpdateSuccess),
                                                    result
                                                )
                                                vm.wakeUpTime.value = time
                                                vm.wakeUpState.value = true
                                            } else
                                                showSnackBarSchedule(
                                                    getString(R.string.dailyUpdateFail),
                                                    result
                                                )
                                                
                                        }
                                    }
                                }.start()
                            }
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            /** daily on */
            R.id.devSet_tvDailyOn -> {
                TimePickerDialogFragment().apply {
                    listener = object : TimePickerDialogFragment.TimePickListener {
                        @SuppressLint("SimpleDateFormat")
                        override fun onPick(time: Long?) {
                            if (time != null) {
                                Thread {
                                    val result = vm.modifySchedule(DailyOn, time, true)
                                    with(this@DeviceSettingsFragment) {
                                        requireActivity().runOnUiThread {
                                            if (result) {
                                                vm.dailyOnTime.value = time
                                                vm.dailyOnState.value = true
                                                showSnackBarSchedule(
                                                    getString(R.string.dailyUpdateSuccess),
                                                    result
                                                )
                                            } else
                                                showSnackBarSchedule(
                                                    getString(R.string.dailyUpdateFail),
                                                    result
                                                )
                                        }
                                    }
                                }.start()
                            }
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            /** daily off */
            R.id.devSet_tvDailyOff -> {
                TimePickerDialogFragment().apply {
                    listener = object : TimePickerDialogFragment.TimePickListener {
                        @SuppressLint("SimpleDateFormat")
                        override fun onPick(time: Long?) {
                            if (time != null) {
                                Thread {
                                    val result = vm.modifySchedule(DailyOff, time, true)
                                    with(this@DeviceSettingsFragment) {
                                        requireActivity().runOnUiThread {
                                            if (result) {
                                                vm.dailyOffTime.value = time
                                                vm.dailyOffState.value = true
                                                showSnackBarSchedule(
                                                    getString(R.string.dailyUpdateSuccess),
                                                    result
                                                )
                                            } else
                                                showSnackBarSchedule(
                                                    getString(R.string.dailyUpdateFail),
                                                    result
                                                )
                                        }
                                    }
                                }.start()
                            }
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            R.id.devSet_tvCDT -> {}
            
            R.id.devSet_btnRest -> {
                AlertDialog.Builder(context)
                    .setTitle(R.string.resetTitle)
                    .setMessage(R.string.resetMessage)
                    .setPositiveButton(
                        R.string.confirm
                    ) { dialog, _ ->
                        run {
                            Thread {
                                this.vm.reset()
                                this@DeviceSettingsFragment.requireActivity().runOnUiThread {
                                    this@DeviceSettingsFragment.vm.init(arguments?.getString(AppKey.DEVICE_UID)!!)
                                    dialog.dismiss()
                                }
                            }.start()
                        }
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
            
            R.id.devSet_btnDelete -> {
                AlertDialog.Builder(context)
                    .setTitle(R.string.deleteTitle)
                    .setMessage(R.string.deleteMessage)
                    .setPositiveButton(
                        R.string.confirm
                    ) { dialog, _ ->
                        run {
                            Thread {
                                val result = this.vm.delete()
                                with(this@DeviceSettingsFragment) {
                                    requireActivity().runOnUiThread {
                                        if (result) {
                                            showSnackBarSchedule(getString(R.string.deleteDevSuccess), result)
                                            dialog.dismiss()
                                            Navigation.findNavController(v).popBackStack(R.id.deviceListFragment, false)
                                        } else {
                                            showSnackBarSchedule(getString(R.string.deleteDevFail), result)
                                        }
                                    }
                                }
                            }.start()
                        }
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
            
        }
    }
    
    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        
        when (button?.id) {
            
            /** wake up */
            R.id.devSet_swWakeup -> {
                if (isChecked) {
                    TimePickerDialogFragment().apply {
                        listener = object : TimePickerDialogFragment.TimePickListener {
                            @SuppressLint("SimpleDateFormat")
                            override fun onPick(time: Long?) {
                                
                                // user selected daily time
                                if (time != null) {
                                    Thread {
                                        val result = vm.modifySchedule(Wakeup, time, true)
                                        
                                        with(this@DeviceSettingsFragment) {
                                            requireActivity().runOnUiThread {
                                                
                                                // server update success
                                                if (result) {
                                                    vm.wakeUpTime.value = time
                                                    vm.wakeUpState.value = true
    
                                                    showSnackBarSchedule(
                                                        getString(R.string.dailyUpdateSuccess),
                                                        result
                                                    )
                                                    
                                                    // server update fail
                                                } else {
                                                    vb.devSetSwWakeup.setOnCheckedChangeListener(
                                                        null
                                                    )
                                                    vm.wakeUpState.value = false
                                                    vb.devSetSwWakeup.setOnCheckedChangeListener(
                                                        this
                                                    )
    
                                                    showSnackBarSchedule(
                                                        getString(R.string.dailyUpdateFail),
                                                        result
                                                    )
                                                }
                                            }
                                        }
                                    }.start()
                                    
                                    // user not selected
                                } else {
                                    vb.devSetSwWakeup.setOnCheckedChangeListener(null)
                                    vm.wakeUpState.value = false
                                    vb.devSetSwWakeup.setOnCheckedChangeListener(this@DeviceSettingsFragment)
                                }
                            }
                        }
                    }.show(parentFragmentManager, null)
                } else {
                    Thread {
                        val result = vm.modifySchedule(Wakeup, vm.wakeUpTime.value!!, false)
                        requireActivity().runOnUiThread {
                            
                            // server update success
                            if (result) {
                                vm.wakeUpState.value = false
    
                                showSnackBarSchedule(
                                    getString(R.string.dailyUpdateSuccess),
                                    result
                                )
                                
                                // server update fail
                            } else {
                                vb.devSetSwWakeup.setOnCheckedChangeListener(null)
                                vm.wakeUpState.value = true
                                vb.devSetSwWakeup.setOnCheckedChangeListener(this)
    
                                showSnackBarSchedule(
                                    getString(R.string.dailyUpdateFail),
                                    result
                                )
                            }
                        }
                    }.start()
                }
            }
            
            /** daily on */
            R.id.devSet_swDailyOn -> {
                if (isChecked) {
                    TimePickerDialogFragment().apply {
                        listener = object : TimePickerDialogFragment.TimePickListener {
                            @SuppressLint("SimpleDateFormat")
                            override fun onPick(time: Long?) {
                                
                                // user selected daily time
                                if (time != null) {
                                    Thread {
                                        val result = vm.modifySchedule(DailyOn, time, true)
                                        
                                        with(this@DeviceSettingsFragment) {
                                            requireActivity().runOnUiThread {
                                                
                                                // server update success
                                                if (result) {
                                                    vm.dailyOnTime.value = time
                                                    vm.dailyOnState.value = true
    
                                                    showSnackBarSchedule(
                                                        getString(R.string.dailyUpdateSuccess),
                                                        result
                                                    )
                                                    
                                                    // server update fail
                                                } else {
                                                    vb.devSetSwDailyOn.setOnCheckedChangeListener(
                                                        null
                                                    )
                                                    vm.dailyOnState.value = false
                                                    vb.devSetSwDailyOn.setOnCheckedChangeListener(
                                                        this
                                                    )
    
                                                    showSnackBarSchedule(
                                                        getString(R.string.dailyUpdateFail),
                                                        result
                                                    )
                                                }
                                            }
                                        }
                                    }.start()
                                    
                                    // user not selected
                                } else {
                                    vb.devSetSwDailyOn.setOnCheckedChangeListener(null)
                                    vm.dailyOnState.value = false
                                    vb.devSetSwDailyOn.setOnCheckedChangeListener(this@DeviceSettingsFragment)
                                }
                            }
                        }
                    }.show(parentFragmentManager, null)
                } else {
                    Thread {
                        val result = vm.modifySchedule(DailyOn, vm.wakeUpTime.value!!, false)
                        requireActivity().runOnUiThread {
                            
                            // server update success
                            if (result) {
                                vm.dailyOnState.value = false
    
                                showSnackBarSchedule(
                                    getString(R.string.dailyUpdateSuccess),
                                    result
                                )
                                
                                // server update fail
                            } else {
                                vb.devSetSwDailyOn.setOnCheckedChangeListener(null)
                                vm.dailyOnState.value = true
                                vb.devSetSwDailyOn.setOnCheckedChangeListener(this)
    
                                showSnackBarSchedule(
                                    getString(R.string.dailyUpdateFail),
                                    result
                                )
                            }
                        }
                    }.start()
                }
            }
            
            /** daily off */
            R.id.devSet_swDailyOff -> {
                if (isChecked) {
                    TimePickerDialogFragment().apply {
                        listener = object : TimePickerDialogFragment.TimePickListener {
                            @SuppressLint("SimpleDateFormat")
                            override fun onPick(time: Long?) {
                                
                                // user selected daily time
                                if (time != null) {
                                    Thread {
                                        val result = vm.modifySchedule(DailyOff, time, true)
                                        
                                        with(this@DeviceSettingsFragment) {
                                            requireActivity().runOnUiThread {
                                                
                                                // server update success
                                                if (result) {
                                                    vm.dailyOffTime.value = time
                                                    vm.dailyOffState.value = true
    
                                                    showSnackBarSchedule(
                                                        getString(R.string.dailyUpdateSuccess),
                                                        result
                                                    )
                                                    
                                                    // server update fail
                                                } else {
                                                    vb.devSetSwDailyOff.setOnCheckedChangeListener(
                                                        null
                                                    )
                                                    vm.dailyOffState.value = false
                                                    vb.devSetSwDailyOff.setOnCheckedChangeListener(
                                                        this
                                                    )
    
                                                    showSnackBarSchedule(
                                                        getString(R.string.dailyUpdateFail),
                                                        result
                                                    )
                                                }
                                            }
                                        }
                                    }.start()
                                    
                                    // user not selected
                                } else {
                                    vb.devSetSwDailyOff.setOnCheckedChangeListener(null)
                                    vm.dailyOffState.value = false
                                    vb.devSetSwDailyOff.setOnCheckedChangeListener(this@DeviceSettingsFragment)
                                }
                            }
                        }
                    }.show(parentFragmentManager, null)
                } else {
                    Thread {
                        val result = vm.modifySchedule(DailyOff, vm.wakeUpTime.value!!, false)
                        requireActivity().runOnUiThread {
                            
                            // server update success
                            if (result) {
                                vm.dailyOffState.value = false
    
                                showSnackBarSchedule(
                                    getString(R.string.dailyUpdateSuccess),
                                    result
                                )
                                
                                // server update fail
                            } else {
                                vb.devSetSwDailyOff.setOnCheckedChangeListener(null)
                                vm.dailyOffState.value = true
                                vb.devSetSwDailyOff.setOnCheckedChangeListener(this)
    
                                showSnackBarSchedule(
                                    getString(R.string.dailyUpdateFail),
                                    result
                                )
                            }
                        }
                    }.start()
                }
            }
            
            R.id.devSet_swCDT -> {
                if (isChecked) {
                    CDTPickerDialogFragment().apply {
                        listener = object : CDTPickerDialogFragment.OnTimeSelectListener {
                            override fun onSelected(time: Long?) {
                                if (time != null && time != 0L) {
                                    textTimeSetEnable(vb.devSetTvCDT)
                                    vm.countDownTime.value = System.currentTimeMillis() + time
                                    vm.setCountDownTime((time / 1000 / 60).toInt())
                                } else {
                                    vb.devSetSwCDT.setOnCheckedChangeListener(null)
                                    vb.devSetSwCDT.isChecked = false
                                    vb.devSetSwCDT.setOnCheckedChangeListener(this@DeviceSettingsFragment)
                                }
                            }
                        }
                    }
                        .show(parentFragmentManager, null)
                } else {
                    textTimeSetDisable(vb.devSetTvCDT)
                    vm.setCountDownTime(0)
                    vm.countDownTime.value = 0
                }
            }
            
            R.id.devSet_swBuzzer -> {
                vm.buzzerChange()
            }
        }
    }
    
    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        
        when (toggleableView?.id) {
            
            R.id.devSet_swSleep -> {
                val result = if (isOn) 255 else 0
                vm.sleepTimeChange(result)
                vm.sleepTime.value = isOn
            }
            
            R.id.devSet_swStatus -> {
                val result = if (isOn) 255 else 0
                vm.finalStatusChange(result)
                vm.switchStatus.value = isOn
            }
        }
    }
    
    private fun setListener() {
        vb.devSetTvBack.setOnClickListener(this)
        vb.devSetIbtEditName.setOnClickListener(this)
        vb.devSetIbtEditGroup.setOnClickListener(this)
        vb.devSetTvWakup.setOnClickListener(this)
        vb.devSetTvDailyOn.setOnClickListener(this)
        vb.devSetTvDailyOff.setOnClickListener(this)
        vb.devSetTvCDT.setOnClickListener(this)
        vb.devSetBtnRest.setOnClickListener(this)
        vb.devSetBtnDelete.setOnClickListener(this)
        
        vb.devSetSwWakeup.setOnCheckedChangeListener(this)
        vb.devSetSwDailyOn.setOnCheckedChangeListener(this)
        vb.devSetSwDailyOff.setOnCheckedChangeListener(this)
        vb.devSetSwCDT.setOnCheckedChangeListener(this)
        vb.devSetSwBuzzer.setOnCheckedChangeListener(this)
        
        vb.devSetSwSleep.setOnToggledListener(this)
        vb.devSetSwStatus.setOnToggledListener(this)
    }
    
    private fun observerVM() {
        
        /** bssid */
        vm.devBssid.observe(viewLifecycleOwner) {
            vm.listenTopic()
            vm.getStatus()
            setListener()
        }
        
        /** name */
        vm.devName.observe(viewLifecycleOwner) {
            vb.devSetTvName.text = it!!
        }
        
        /** location */
        vm.devLoc.observe(viewLifecycleOwner) {
            vb.devSetTvLoc.text = it?.groupName
        }
        
        /** wakeup time text view */
        vm.wakeUpTime.observe(viewLifecycleOwner) {
            vb.devSetTvWakup.text = convertTime(it!!)
        }
        
        /** wakeup switch */
        vm.wakeUpState.observe(viewLifecycleOwner) {
            
            vb.devSetSwWakeup.isChecked = it!!
            if (it)
                textTimeSetEnable(vb.devSetTvWakup)
            else
                textTimeSetDisable(vb.devSetTvWakup)
        }
        
        /** daily on text view */
        vm.dailyOnTime.observe(viewLifecycleOwner) {
            vb.devSetTvDailyOn.text = convertTime(it!!)
        }
        
        /** daily on switch */
        vm.dailyOnState.observe(viewLifecycleOwner) {
            vb.devSetSwDailyOn.isChecked = it!!
            
            if (it)
                textTimeSetEnable(vb.devSetTvDailyOn)
            else
                textTimeSetDisable(vb.devSetTvDailyOn)
        }
        
        /** daily off text view */
        vm.dailyOffTime.observe(viewLifecycleOwner) {
            vb.devSetTvDailyOff.text = convertTime(it!!)
        }
        
        /** daily off switch */
        vm.dailyOffState.observe(viewLifecycleOwner) {
            vb.devSetSwDailyOff.isChecked = it!!
            
            if (it)
                textTimeSetEnable(vb.devSetTvDailyOff)
            else
                textTimeSetDisable(vb.devSetTvDailyOff)
        }
        
        /** count down time */
        vm.countDownTime.observe(viewLifecycleOwner) {
            if (it != null) {
                vb.devSetSwCDT.isChecked = (it < System.currentTimeMillis()).not()
                if (it < System.currentTimeMillis())
                    vb.devSetTvCDT.text = "30 分鐘"
                else {
                    val min = (it - System.currentTimeMillis()) / 1000 / 60
                    val minStr = "$min 分鐘"
                    vb.devSetTvCDT.text = minStr
                }
            }
        }
        
        /** sleep switch */
        vm.sleepTime.observe(viewLifecycleOwner) {
            vb.devSetSwSleep.isOn = it!!
        }
        
        /** cdt status switch */
        vm.countDownState.observe(viewLifecycleOwner) {
            vb.devSetSwStatus.isOn = it!!
        }
        
        /** buzzer switch */
        vm.buzzer.observe(viewLifecycleOwner) {
            vb.devSetSwBuzzer.setOnCheckedChangeListener(null)
            vb.devSetSwBuzzer.isChecked = it!!
            vb.devSetSwBuzzer.setOnCheckedChangeListener(this)
        }
    }
    
    @SuppressLint("SimpleDateFormat")
    fun convertTime(milTime: Long): String {
        val dateFormat = SimpleDateFormat("aa hh:mm")
        return dateFormat.format(Date(milTime))
    }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun textTimeSetEnable(textView: TextView) {
        textView.isEnabled = true
        textView.background = resources.getDrawable(R.drawable.textview_bg_enable, null)
    }
    
    private fun textTimeSetDisable(textView: TextView) {
        textView.isEnabled = false
        textView.background = null
    }
    
    private fun showSnackBarSchedule(text: String, result: Boolean) {
        val msgColor =
            if (result)
                resources.getColor(R.color.snackBar_success, null)
            else
                resources.getColor(R.color.snackBar_fail, null)
        
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_SHORT
        )
            .setBackgroundTint(msgColor)
            .show()
    }
}