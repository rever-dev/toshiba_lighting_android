package com.sttptech.toshiba_lighting.Fragment.Device.DeviceControl

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.CustomView.SeekBarIndicator
import com.sttptech.toshiba_lighting.DialogFragment.EditTextDialog.EditTextDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentDeviceControlBinding
import kotlin.math.ceil


class DeviceControlFragment : Fragment(), View.OnClickListener {
    
    companion object { const val TAG = "DeviceControl" }
    
    lateinit var vm: DeviceControlViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[DeviceControlViewModel::class.java]
    }
    
    lateinit var vb: FragmentDeviceControlBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentDeviceControlBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        /** text view back */
        vb.devConTvBack.text = arguments?.getString(AppKey.FROM_PAGE_NAME, "")
        
        /** custom mode number */
        vb.devConCusModeBtn61.tvNum.text = "1"
        vb.devConCusModeBtn62.tvNum.text = "2"
        vb.devConCusModeBtn63.tvNum.text = "3"
        vb.devConCusModeBtn64.tvNum.text = "4"
        
        /** set mode 50, 51, 53 icon, content text */
        vb.devConModebtn50.setIcon(resources.getDrawable(R.drawable.button_ic_flash, null))
        vb.devConModebtn51.setIcon(resources.getDrawable(R.drawable.button_ic_sun, null))
        vb.devConModebtn53.setIcon(resources.getDrawable(R.drawable.button_ic_moon, null))
        vb.devConModebtn50.setName(getString(R.string.mode50))
        vb.devConModebtn51.setName(getString(R.string.mode51))
        vb.devConModebtn53.setName(getString(R.string.mode53))
        
        /** set built in mode button */
        vb.devConModebtn01.setName(getString(R.string.mode01))
        vb.devConModebtn02.setName(getString(R.string.mode02))
        vb.devConModebtn03.setName(getString(R.string.mode03))
        vb.devConModebtn04.setName(getString(R.string.mode04))
        vb.devConModebtn05.setName(getString(R.string.mode05))
        vb.devConModebtn06.setName(getString(R.string.mode06))
        vb.devConModebtn07.setName(getString(R.string.mode07))
        vb.devConModebtn08.setName(getString(R.string.mode08))
        vb.devConModebtn09.setName(getString(R.string.mode09))
        vb.devConModebtn10.setName(getString(R.string.mode10))
        vb.devConModebtn11.setName(getString(R.string.mode11))
        vb.devConModebtn12.setName(getString(R.string.mode12))
        
        /** color picker view */
        val bubbleFlag = BubbleFlag(requireContext())
        bubbleFlag.flagMode = FlagMode.FADE
        vb.devConColorPickerView.flagView = bubbleFlag
        
        setViewListener()
    }
    
    override fun onStart() {
        super.onStart()
        
        /** init view data & listen topic & get status */
        Thread {
            vm.initDeviceData(arguments?.getString(AppKey.DEVICE_UID, ""))
            requireActivity().runOnUiThread() {
                vm.listenTopic()
                dataBindView()
                vm.getStatus()
            }
        }.start()
    }
    
    override fun onStop() {
        super.onStop()
        vm.unListenTopic()
    }
    
    override fun onClick(v: View) {
        if (vm.isSubscribe.value!!.not()) vm.listenTopic()
        
        when (v.id) {
            
            /** back */
            R.id.devCon_tvBack -> {
                Navigation.findNavController(requireView()).popBackStack()
            }
            
            /** settings */
            R.id.devCon_ibtSetting -> {
                Navigation.findNavController(requireView())
                    .navigate(
                        R.id.action_deviceControlFragment_to_deviceSettingsFragment,
                        bundleOf(Pair(AppKey.DEVICE_UID, requireArguments().get(AppKey.DEVICE_UID)))
                    )
            }
            
            /** switch */
            R.id.devCon_ibtSwitch -> {
                val switch = !vm.switchStatus.value!!
                vm.switchStatus.value = switch
                if (switch) vm.turnOn() else vm.turnOff()
            }
            
            /** 節能 */
            R.id.devCon_modebtn50 -> {
                
                if (vb.devConBtnR.visibility != View.VISIBLE) {
                    vb.devConBtnR.visibility = View.VISIBLE
                    vb.devConBtnRAdd.visibility = View.INVISIBLE
                    vb.devConBtnRLess.visibility = View.INVISIBLE
                }
                
                if (vb.devConBtnG.visibility != View.VISIBLE) {
                    vb.devConBtnG.visibility = View.VISIBLE
                    vb.devConBtnGAdd.visibility = View.INVISIBLE
                    vb.devConBtnGLess.visibility = View.INVISIBLE
                }
                
                if (vb.devConBtnB.visibility != View.VISIBLE) {
                    vb.devConBtnB.visibility = View.VISIBLE
                    vb.devConBtnBAdd.visibility = View.INVISIBLE
                    vb.devConBtnBLess.visibility = View.INVISIBLE
                }
                
                vm.opMode.value = 1
                vm.selectMode.value = 0
                vm.mBr.value = 15
                vm.mC.value = 15
                vm.mode50()
            }
            
            /** 全光 */
            R.id.devCon_modebtn51 -> {
                
                if (vb.devConBtnR.visibility != View.VISIBLE) {
                    vb.devConBtnR.visibility = View.VISIBLE
                    vb.devConBtnRAdd.visibility = View.INVISIBLE
                    vb.devConBtnRLess.visibility = View.INVISIBLE
                }
                
                if (vb.devConBtnG.visibility != View.VISIBLE) {
                    vb.devConBtnG.visibility = View.VISIBLE
                    vb.devConBtnGAdd.visibility = View.INVISIBLE
                    vb.devConBtnGLess.visibility = View.INVISIBLE
                }
                
                if (vb.devConBtnB.visibility != View.VISIBLE) {
                    vb.devConBtnB.visibility = View.VISIBLE
                    vb.devConBtnBAdd.visibility = View.INVISIBLE
                    vb.devConBtnBLess.visibility = View.INVISIBLE
                }
                
                vm.opMode.value = 1
                vm.selectMode.value = 0
                vm.mBr.value = 20
                vm.mC.value = 15
                vm.mode51()
            }
            
            /** 夜燈 */
            R.id.devCon_modebtn53 -> {
                
                if (vb.devConBtnR.visibility != View.VISIBLE) {
                    vb.devConBtnR.visibility = View.VISIBLE
                    vb.devConBtnRAdd.visibility = View.INVISIBLE
                    vb.devConBtnRLess.visibility = View.INVISIBLE
                }
                
                if (vb.devConBtnG.visibility != View.VISIBLE) {
                    vb.devConBtnG.visibility = View.VISIBLE
                    vb.devConBtnGAdd.visibility = View.INVISIBLE
                    vb.devConBtnGLess.visibility = View.INVISIBLE
                }
                
                if (vb.devConBtnB.visibility != View.VISIBLE) {
                    vb.devConBtnB.visibility = View.VISIBLE
                    vb.devConBtnBAdd.visibility = View.INVISIBLE
                    vb.devConBtnBLess.visibility = View.INVISIBLE
                }
                vm.opMode.value = 2
                vm.selectMode.value = 0
                vm.mode53()
            }
            
            /** R adjust */
            R.id.devCon_btnR -> {
                btnRTrigger()
                vm.mode13()
            }
            
            /** R add */
            R.id.devCon_btnRAdd -> vm.rAdjust(1)
            
            /** R less */
            R.id.devCon_btnRLess -> vm.rAdjust(2)
            
            /** G adjust */
            R.id.devCon_btnG -> {
                btnGTrigger()
                vm.mode14()
            }
            
            /** G add */
            R.id.devCon_btnGAdd -> vm.gAdjust(1)
            
            /** G less */
            R.id.devCon_btnGLess -> vm.gAdjust(2)
            
            /** B adjust */
            R.id.devCon_btnB -> {
                btnBTrigger()
                vm.mode15()
            }
            
            /** B add */
            R.id.devCon_btnBAdd -> vm.bAdjust(1)
            
            /** B less */
            R.id.devCon_btnBLess -> vm.bAdjust(2)
            
            R.id.devCon_modebtn01 -> {
                vm.selectMode.value = 1
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode01()
            }
            
            R.id.devCon_modebtn02 -> {
                vm.selectMode.value = 2
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode02()
            }
            
            R.id.devCon_modebtn03 -> {
                vm.selectMode.value = 3
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode03()
            }
            
            R.id.devCon_modebtn04 -> {
                vm.selectMode.value = 4
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode04()
            }
            
            R.id.devCon_modebtn05 -> {
                vm.selectMode.value = 5
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode05()
            }
            
            R.id.devCon_modebtn06 -> {
                vm.selectMode.value = 6
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode06()
            }
            
            R.id.devCon_modebtn07 -> {
                vm.selectMode.value = 7
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode07()
            }
            
            R.id.devCon_modebtn08 -> {
                vm.selectMode.value = 8
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode08()
            }
            
            R.id.devCon_modebtn09 -> {
                vm.selectMode.value = 9
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode09()
            }
            
            R.id.devCon_modebtn10 -> {
                vm.selectMode.value = 10
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode10()
                
                vb.devConIbtPlay.isEnabled = false
                vb.devConIbtPause.isEnabled = true
                vb.devConIbtPlay.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.gray_600, null))
                vb.devConIbtPause.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.black, null))
            }
            
            /** play */
            R.id.devCon_ibtPlay -> {
                imageButtonSetChange(vb.devConIbtPlay)
                imageButtonSetChange(vb.devConIbtPause)
                vm.colorModePlay()
            }
            
            /** pause */
            R.id.devCon_ibtPause -> {
                imageButtonSetChange(vb.devConIbtPlay)
                imageButtonSetChange(vb.devConIbtPause)
                vm.colorModePause()
            }
            
            R.id.devCon_modebtn11 -> {
                vm.selectMode.value = 11
                vm.opMode.value = 3
                vm.rgbBr.value = 10
                vm.mode11()
            }
            
            /** 全彩 */
            R.id.devCon_modebtn12 -> {
                vm.selectMode.value = 12
                vm.opMode.value = 3
                vm.rgbBr.value = 3
                vm.mode12()
                vb.devConColorPickerView.setInitialColor((getIntOfColor(255, 0, 0)))
            }
            
        }
    }
    
    private fun setViewListener() {
        
        /** back */
        vb.devConTvBack.setOnClickListener(this)
        
        /** settings */
        vb.devConIbtSetting.setOnClickListener(this)
        
        /** switch */
        vb.devConIbtSwitch.setOnClickListener(this)
        
        /** mode button */
        vb.devConModebtn50.setOnClickListener(this)
        vb.devConModebtn51.setOnClickListener(this)
        vb.devConModebtn53.setOnClickListener(this)
        vb.devConBtnR.setOnClickListener(this)
        vb.devConBtnRAdd.setOnClickListener(this)
        vb.devConBtnRLess.setOnClickListener(this)
        vb.devConBtnG.setOnClickListener(this)
        vb.devConBtnGAdd.setOnClickListener(this)
        vb.devConBtnGLess.setOnClickListener(this)
        vb.devConBtnB.setOnClickListener(this)
        vb.devConBtnBAdd.setOnClickListener(this)
        vb.devConBtnBLess.setOnClickListener(this)
        vb.devConModebtn01.setOnClickListener(this)
        vb.devConModebtn02.setOnClickListener(this)
        vb.devConModebtn03.setOnClickListener(this)
        vb.devConModebtn04.setOnClickListener(this)
        vb.devConModebtn05.setOnClickListener(this)
        vb.devConModebtn06.setOnClickListener(this)
        vb.devConModebtn07.setOnClickListener(this)
        vb.devConModebtn08.setOnClickListener(this)
        vb.devConModebtn09.setOnClickListener(this)
        vb.devConModebtn10.setOnClickListener(this)
        vb.devConIbtPlay.setOnClickListener(this)
        vb.devConIbtPause.setOnClickListener(this)
        vb.devConModebtn11.setOnClickListener(this)
        vb.devConModebtn12.setOnClickListener(this)
        
        /** seek bar */
        vb.devConSbMbr.setOnSeekBarChangeListener(sbl)
        vb.devConSbMc.setOnSeekBarChangeListener(sbl)
        vb.devConSbNBr.setOnSeekBarChangeListener(sbl)
        vb.devConSbRGBBr.setOnSeekBarChangeListener(sbl)
        
        /** color picker */
        vb.devConColorPickerView.setColorListener(object : ColorEnvelopeListener {
            override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                if (vm.switchStatus.value!! && fromUser) {
                    vm.rgbChange(
                        ceil(vm.rgbBr.value!!.toDouble()).toInt(),
                        getColorPickerRGB(envelope!!.argb)
                    )
                }
            }
        })
        
        /** tab view */
        vb.devConTabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val text = tab?.text.toString()
                if (text == getString(R.string.commonly_used)) {
                    vb.devConConslayMainBtn.visibility = View.VISIBLE
                    vb.devCOnConslayCustBtn.visibility = View.GONE
                } else if (text == getString(R.string.custom_mode)) {
                    vb.devConConslayMainBtn.visibility = View.GONE
                    vb.devCOnConslayCustBtn.visibility = View.VISIBLE
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        /** custom mode button */
        vb.devConCusModeBtn61.itemView.setOnClickListener { vm.callCustomMode(61) }
        vb.devConCusModeBtn62.itemView.setOnClickListener { vm.callCustomMode(62) }
        vb.devConCusModeBtn63.itemView.setOnClickListener { vm.callCustomMode(63) }
        vb.devConCusModeBtn64.itemView.setOnClickListener { vm.callCustomMode(64) }
        
        vb.devConCusModeBtn61.itemView.setOnLongClickListener {
            vm.saveCustomMode(61)
            Snackbar.make(it, R.string.modifyCustomName_save, Snackbar.LENGTH_SHORT).show()
            true
        }
        vb.devConCusModeBtn62.itemView.setOnLongClickListener {
            vm.saveCustomMode(62)
            Snackbar.make(it, R.string.modifyCustomName_save, Snackbar.LENGTH_SHORT).show()
            true
        }
        vb.devConCusModeBtn63.itemView.setOnLongClickListener {
            vm.saveCustomMode(63)
            Snackbar.make(it, R.string.modifyCustomName_save, Snackbar.LENGTH_SHORT).show()
            true
        }
        vb.devConCusModeBtn64.itemView.setOnLongClickListener {
            vm.saveCustomMode(64)
            Snackbar.make(it, R.string.modifyCustomName_save, Snackbar.LENGTH_SHORT).show()
            true
        }
        
        vb.devConCusModeBtn61.ibtEdit.setOnClickListener {
            EditTextDialogFragment(
                getString(R.string.mode61),
                getString(R.string.enterYourModeName),
                null
            ).apply {
                callback = object : EditTextDialogFragment.OnTextInputCallback {
                    override fun onTextInput(str: String) {
                        Thread {
                            val result = this@DeviceControlFragment.vm
                                .modifyCustomModName(61, str)
                            this@DeviceControlFragment.requireActivity().runOnUiThread {
                                if (result) {
                                    this@DeviceControlFragment.vb
                                        .devConCusModeBtn61.tvName.text = str
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_success, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                } else
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_fail, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                            }
                        }.start()
                    }
                }
            }.show(parentFragmentManager, null)
        }
        vb.devConCusModeBtn62.ibtEdit.setOnClickListener {
            EditTextDialogFragment(
                getString(R.string.mode62),
                getString(R.string.enterYourModeName),
                null
            ).apply {
                callback = object : EditTextDialogFragment.OnTextInputCallback {
                    override fun onTextInput(str: String) {
                        Thread {
                            val result = this@DeviceControlFragment.vm
                                .modifyCustomModName(62, str)
                            this@DeviceControlFragment.requireActivity().runOnUiThread {
                                if (result) {
                                    this@DeviceControlFragment.vb
                                        .devConCusModeBtn62.tvName.text = str
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_success, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                } else
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_fail, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                            }
                        }.start()
                    }
                }
            }.show(parentFragmentManager, null)
        }
        vb.devConCusModeBtn63.ibtEdit.setOnClickListener {
            EditTextDialogFragment(
                getString(R.string.mode63),
                getString(R.string.enterYourModeName),
                null
            ).apply {
                callback = object : EditTextDialogFragment.OnTextInputCallback {
                    override fun onTextInput(str: String) {
                        Thread {
                            val result = this@DeviceControlFragment.vm
                                .modifyCustomModName(63, str)
                            this@DeviceControlFragment.requireActivity().runOnUiThread {
                                if (result) {
                                    this@DeviceControlFragment.vb
                                        .devConCusModeBtn63.tvName.text = str
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_success, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                } else
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_fail, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                            }
                        }.start()
                    }
                }
            }.show(parentFragmentManager, null)
        }
        vb.devConCusModeBtn64.ibtEdit.setOnClickListener {
            EditTextDialogFragment(
                getString(R.string.mode64),
                getString(R.string.enterYourModeName),
                null
            ).apply {
                callback = object : EditTextDialogFragment.OnTextInputCallback {
                    override fun onTextInput(str: String) {
                        Thread {
                            val result = this@DeviceControlFragment.vm
                                .modifyCustomModName(64, str)
                            this@DeviceControlFragment.requireActivity().runOnUiThread {
                                if (result) {
                                    this@DeviceControlFragment.vb
                                        .devConCusModeBtn64.tvName.text = str
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_success, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                } else
                                    Snackbar.make(
                                        this@DeviceControlFragment.requireView(),
                                        R.string.modifyCustomName_fail, Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                            }
                        }.start()
                    }
                }
            }.show(parentFragmentManager, null)
        }
    }
    
    private val sbl = object : SeekBar.OnSeekBarChangeListener {
        
        var startProgress: Int? = null
        
        override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
            
            
            if (fromUser)
                if (sb!!.id == R.id.devCon_sbMc)
                    seekBarShowIndicatorMC(sb, vb.devConSbIndicator)
                else
                    seekBarShowIndicator(sb, vb.devConSbIndicator)
            
        }
        
        override fun onStartTrackingTouch(sb: SeekBar?) {
            startProgress = sb!!.progress
            
            seekBarShowIndicator(sb, vb.devConSbIndicator)
        }
        
        override fun onStopTrackingTouch(sb: SeekBar?) {
            seekBarDismissIndicator(vb.devConSbIndicator)
            
            if (startProgress != sb!!.progress) {
                // turn off check
                when (sb!!.id) {
                    R.id.devCon_sbMbr, R.id.devCon_sbNBr, R.id.devCon_sbRGBBr -> {
                        val switch = sb.progress != 0
                        vm.switchStatus.value = switch
                        if (!switch) {
                            vm.turnOff()
                            return
                        }
                    }
                }
                
                when (sb.id) {
                    
                    // MBr
                    R.id.devCon_sbMbr -> {
                        val mBr = ceil(sb.progress.toDouble() / 10).toInt()
                        vm.mBr.value = mBr
                        vm.mainChange(mBr, vm.mC.value!!)
                    }
                    
                    // MC
                    R.id.devCon_sbMc -> {
                        val mC = ceil(sb.progress.toDouble() / 10).toInt() + 1
                        vm.mC.value = mC
                        if (vm.switchStatus.value!!)
                            vm.mainChange(vm.mBr.value!!, mC)
                    }
                    
                    // NBr
                    R.id.devCon_sbNBr -> {
                        val nBr = ceil(sb.progress.toDouble() / 10).toInt()
                        vm.nBr.value = nBr
                        vm.nightChange(nBr)
                    }
                    
                    // RGBBr
                    R.id.devCon_sbRGBBr -> {
                        val rgbBr = ceil(sb.progress.toDouble() / 10).toInt()
                        vm.rgbBr.value = rgbBr
                        vm.rgbBrChange(
                            rgbBr,
                            getColorPickerRGB(vb.devConColorPickerView.colorEnvelope.argb)
                        )
                    }
                }
            }
        }
    }
    
    private fun dataBindView() {
        /** text view name */
        vm.devName.observe(viewLifecycleOwner) { vb.devConTvName.text = it }
        vm.devCusMode1.observe(viewLifecycleOwner) { vb.devConCusModeBtn61.tvName.text = it }
        vm.devCusMode2.observe(viewLifecycleOwner) { vb.devConCusModeBtn62.tvName.text = it }
        vm.devCusMode3.observe(viewLifecycleOwner) { vb.devConCusModeBtn63.tvName.text = it }
        vm.devCusMode4.observe(viewLifecycleOwner) { vb.devConCusModeBtn64.tvName.text = it }
        
        /** switch */
        vm.switchStatus.observe(viewLifecycleOwner) { if (it) swON() else swOFF() }
        
        /** mode */
        vm.opMode.observe(viewLifecycleOwner) {
            when (it) {
                
                1 -> {
                    if (vb.devConLinlayMainBar.visibility != View.VISIBLE) {
                        modeChange()
                        mainMode()
                    }
                }
                
                2 -> {
                    vb.devConSbNBr.progressTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.seekBar_night, null))
                    if (vb.devConLinlayNightMode.visibility != View.VISIBLE) {
                        modeChange()
                        nightMode()
                    }
                }
                
                3 -> {
                    if ((vm.selectMode.value == 13).not() && (vm.selectMode.value == 14).not() && (vm.selectMode.value == 15).not() &&
                        (vm.selectMode.value == 12).not() && (vm.selectMode.value == 10).not()
                    ) {
                        vb.devConSbNBr.progressTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.seekBar_mode, null))
                        if ((vm.switchStatus.value)!!.not())
                            vm.switchStatus.value = true
                        if (vb.devConLinlayNightMode.visibility != View.VISIBLE) {
                            modeChange()
                            builtinMode()
                        }
                    }
                }
            }
        }
        
        vm.selectMode.observe(viewLifecycleOwner) {
            
            when (it) {
                /** color mode */
                10 -> {
                    if (vb.devConLinlayNightMode.visibility != View.VISIBLE) {
                        modeChange()
                    }
                        colorMode()
                }
                
                /** rgb mode */
                12 -> {
                    if (vb.devConLinlayColorPick.visibility != View.VISIBLE) {
                        modeChange()
                        rgbMode()
                    }
                }
            }
            
            if (it != 10)
                vb.devConConslayPlayPause.visibility = View.INVISIBLE
            
            if (it == 12 && vm.isSubscribe.value!!)
                vm.unListenTopic()
            else if (vm.isSubscribe.value!!.not())
                vm.listenTopic()
            
        }
        
        /** seek bar */
        vm.mBr.observe(viewLifecycleOwner) { vb.devConSbMbr.progress = it * 10 }
        vm.mC.observe(viewLifecycleOwner) { vb.devConSbMc.progress = (it - 1) * 10 }
        vm.nBr.observe(viewLifecycleOwner) {
            if (vm.opMode.value == 2) vb.devConSbNBr.progress = it * 10
        }
        vm.rgbBr.observe(viewLifecycleOwner) {
            if (vm.selectMode.value == 12)
                vb.devConSbRGBBr.progress = it * 10
            else if (vm.opMode.value != 2)
                vb.devConSbNBr.progress = it * 10
        }
        
        /** color picker */
        vm.rgbValue.observe(viewLifecycleOwner) {
            val color = getIntOfColor(it[0], it[1], it[2])
            vb.devConColorPickerView.setInitialColor(color)
        }
    }
    
    private fun getIntOfColor(Red: Int, Green: Int, Blue: Int): Int {
        var rgb = Red
        rgb = (rgb shl 8) + Green
        rgb = (rgb shl 8) + Blue
        return rgb
    }
    
    /** rgb * br / 10 */
    private fun getColorPickerRGB(argb: IntArray): IntArray {
        val rgb = intArrayOf(argb[1], argb[2], argb[3])
        val newRGB = intArrayOf(
            (rgb[0].toDouble() * vm.rgbBr.value!!.toDouble() / 10).toInt(),
            (rgb[1].toDouble() * vm.rgbBr.value!!.toDouble() / 10).toInt(),
            (rgb[2].toDouble() * vm.rgbBr.value!!.toDouble() / 10).toInt()
        )
        return newRGB
    }
    
    private fun swON() {
        vb.devConIbtSwitch.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_lightbulb_24,
                null
            )
        )
    }
    
    private fun swOFF() {
        vb.devConIbtSwitch.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_lightbulb_off_24,
                null
            )
        )
        vb.devConSbMbr.progress = 0
        vb.devConSbNBr.progress = 0
        vb.devConSbRGBBr.progress = 0
    }
    
    private fun modeChange() {
        vb.devConLinlayMainBar.visibility = View.INVISIBLE
        vb.devConLinlayNightMode.visibility = View.INVISIBLE
        vb.devConConslayRGBBtn.visibility = View.INVISIBLE
        vb.devConLinlayColorPick.visibility = View.INVISIBLE
        vb.devConBtnR.visibility = View.VISIBLE
        vb.devConBtnG.visibility = View.VISIBLE
        vb.devConBtnB.visibility = View.VISIBLE
        vb.devConBtnRLess.visibility = View.INVISIBLE
        vb.devConBtnRAdd.visibility = View.INVISIBLE
        vb.devConBtnGLess.visibility = View.INVISIBLE
        vb.devConBtnGAdd.visibility = View.INVISIBLE
        vb.devConBtnBLess.visibility = View.INVISIBLE
        vb.devConBtnBAdd.visibility = View.INVISIBLE
        vb.devConConslayPlayPause.visibility = View.INVISIBLE
    }
    
    private fun mainMode() {
        vb.devConLinlayMainBar.visibility = View.VISIBLE
        vb.devConConslayRGBBtn.visibility = View.VISIBLE
    }
    
    private fun nightMode() {
        vb.devConLinlayNightMode.visibility = View.VISIBLE
        vb.devConSbNBr.progressTintList =
            ColorStateList.valueOf(resources.getColor(R.color.seekBar_night, null))
        vb.devConConslayRGBBtn.visibility = View.VISIBLE
    }
    
    fun colorMode() {
        vb.devConLinlayNightMode.visibility = View.VISIBLE
        vb.devConSbNBr.progressTintList =
            ColorStateList.valueOf(resources.getColor(R.color.seekBar_mode, null))
        vb.devConConslayRGBBtn.visibility = View.INVISIBLE
        if (vb.devConConslayPlayPause.visibility != View.VISIBLE)
            vb.devConConslayPlayPause.visibility = View.VISIBLE
    }
    
    private fun rgbMode() {
        vb.devConLinlayMainBar.visibility = View.INVISIBLE
        vb.devConLinlayNightMode.visibility = View.INVISIBLE
        vb.devConConslayRGBBtn.visibility = View.INVISIBLE
        vb.devConLinlayColorPick.visibility = View.VISIBLE
        vb.devConBtnR.visibility = View.INVISIBLE
        vb.devConBtnG.visibility = View.INVISIBLE
        vb.devConBtnB.visibility = View.INVISIBLE
        vb.devConBtnRLess.visibility = View.INVISIBLE
        vb.devConBtnRAdd.visibility = View.INVISIBLE
        vb.devConBtnGLess.visibility = View.INVISIBLE
        vb.devConBtnGAdd.visibility = View.INVISIBLE
        vb.devConBtnBLess.visibility = View.INVISIBLE
        vb.devConBtnBAdd.visibility = View.INVISIBLE
    }
    
    fun builtinMode() {
        vb.devConLinlayNightMode.visibility = View.VISIBLE
        vb.devConSbNBr.progressTintList =
            ColorStateList.valueOf(resources.getColor(R.color.seekBar_mode, null))
        vb.devConConslayRGBBtn.visibility = View.INVISIBLE
    }
    
    private fun btnRTrigger() {
        vb.devConBtnR.visibility = View.INVISIBLE
        vb.devConBtnRAdd.visibility = View.VISIBLE
        vb.devConBtnRLess.visibility = View.VISIBLE
        vb.devConBtnG.visibility = View.VISIBLE
        vb.devConBtnGAdd.visibility = View.INVISIBLE
        vb.devConBtnGLess.visibility = View.INVISIBLE
        vb.devConBtnB.visibility = View.VISIBLE
        vb.devConBtnBAdd.visibility = View.INVISIBLE
        vb.devConBtnBLess.visibility = View.INVISIBLE
    }
    
    private fun btnGTrigger() {
        vb.devConBtnR.visibility = View.VISIBLE
        vb.devConBtnRAdd.visibility = View.INVISIBLE
        vb.devConBtnRLess.visibility = View.INVISIBLE
        vb.devConBtnG.visibility = View.INVISIBLE
        vb.devConBtnGAdd.visibility = View.VISIBLE
        vb.devConBtnGLess.visibility = View.VISIBLE
        vb.devConBtnB.visibility = View.VISIBLE
        vb.devConBtnBAdd.visibility = View.INVISIBLE
        vb.devConBtnBLess.visibility = View.INVISIBLE
    }
    
    private fun btnBTrigger() {
        vb.devConBtnR.visibility = View.VISIBLE
        vb.devConBtnRAdd.visibility = View.INVISIBLE
        vb.devConBtnRLess.visibility = View.INVISIBLE
        vb.devConBtnG.visibility = View.VISIBLE
        vb.devConBtnGAdd.visibility = View.INVISIBLE
        vb.devConBtnGLess.visibility = View.INVISIBLE
        vb.devConBtnB.visibility = View.INVISIBLE
        vb.devConBtnBAdd.visibility = View.VISIBLE
        vb.devConBtnBLess.visibility = View.VISIBLE
    }
    
    fun seekBarShowIndicator(seekBar: SeekBar, indicator: SeekBarIndicator) {
        val width = (seekBar.width
                - seekBar.paddingLeft
                - seekBar.paddingRight)
        val thumbPosX = ((seekBar.paddingLeft
                + seekBar.paddingRight
                + width)
                * seekBar.progress
                / seekBar.max)
        val thumbPosY = seekBar.y + seekBar.height * 1.5
        
        val progress = ceil((seekBar.progress.toDouble() / 10)).toInt()
        
        indicator.tvValue.text = progress.toString()
        indicator.x = thumbPosX.toFloat()
        indicator.y = thumbPosY.toFloat()
        if (indicator.visibility != View.VISIBLE)
            indicator.visibility = View.VISIBLE
    }
    
    fun seekBarShowIndicatorMC(seekBar: SeekBar, indicator: SeekBarIndicator) {
        val width = (seekBar.width
                - seekBar.paddingLeft
                - seekBar.paddingRight)
        val thumbPosX = ((seekBar.paddingLeft
                + seekBar.paddingRight
                + width)
                * seekBar.progress
                / seekBar.max)
        val thumbPosY = seekBar.y + seekBar.height * 1.5
        
        val progress = ceil((seekBar.progress.toDouble() / 10)).toInt() + 1
        
        indicator.tvValue.text = progress.toString()
        indicator.x = thumbPosX.toFloat()
        indicator.y = thumbPosY.toFloat()
        if (indicator.visibility != View.VISIBLE)
            indicator.visibility = View.VISIBLE
    }
    
    fun seekBarDismissIndicator(indicator: SeekBarIndicator) {
        if (indicator.visibility == View.VISIBLE)
            indicator.visibility = View.GONE
    }
    
    private fun imageButtonSetChange(button: ImageButton) {
        if (button.isEnabled) {
            button.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.gray_600, null))
            button.isEnabled = false
        } else {
            button.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.black, null))
            button.isEnabled = true
        }
    }
    
}