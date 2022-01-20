package com.sttptech.toshiba_lighting.Fragment.Device.DeviceControl

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentDeviceControlBinding

class DeviceControlFragment : Fragment(), View.OnClickListener {

    companion object {

        const val TAG = "DeviceControl"
    }

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

        // set mode 50, 51, 53 icon, content text
        vb.devConModebtn50.setIcon(resources.getDrawable(R.drawable.button_ic_flash, null))
        vb.devConModebtn51.setIcon(resources.getDrawable(R.drawable.button_ic_sun, null))
        vb.devConModebtn53.setIcon(resources.getDrawable(R.drawable.button_ic_moon, null))
        vb.devConModebtn50.setName(getString(R.string.mode50))
        vb.devConModebtn51.setName(getString(R.string.mode51))
        vb.devConModebtn53.setName(getString(R.string.mode53))

        vb.devConModebtn50.setOnClickListener {
            modeChange()
            mainMode()
        }

        vb.devConModebtn51.setOnClickListener {
            modeChange()
            mainMode()
        }

        vb.devConModebtn53.setOnClickListener {
            modeChange()
            nightMode()
        }

        vb.devConBtnR.setOnClickListener {
            btnRTrigger()
        }

        vb.devConBtnG.setOnClickListener {
            btnGTrigger()
        }

        vb.devConBtnB.setOnClickListener {
            btnBTrigger()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            // Tool bar
            R.id.devCon_tvBack -> { }
            R.id.devCon_ibtSetting -> { }
            R.id.devCon_ibtSwitch -> { }

            // Main mode
            R.id.devCon_modebtn50 -> { }
            R.id.devCon_modebtn51 -> { }
            R.id.devCon_modebtn53 -> { }

            // RGB button
            R.id.devCon_btnR -> { }
            R.id.devCon_btnRAdd -> { }
            R.id.devCon_btnRLess -> { }
            R.id.devCon_btnG -> { }
            R.id.devCon_btnGAdd -> { }
            R.id.devCon_btnGLess -> { }
            R.id.devCon_btnB -> { }
            R.id.devCon_btnBAdd -> { }
            R.id.devCon_btnBLess -> { }

        }
    }

    fun modeChange() {
        vb.devConLinlayMainBar.visibility = View.INVISIBLE
        vb.devConLinlayNightMode.visibility = View.INVISIBLE
        vb.devConCoslayRGBBtn.visibility = View.INVISIBLE
        vb.devConBtnR.visibility = View.VISIBLE
        vb.devConBtnG.visibility = View.VISIBLE
        vb.devConBtnB.visibility = View.VISIBLE
        vb.devConBtnRLess.visibility = View.INVISIBLE
        vb.devConBtnRAdd.visibility = View.INVISIBLE
        vb.devConBtnGLess.visibility = View.INVISIBLE
        vb.devConBtnGAdd.visibility = View.INVISIBLE
        vb.devConBtnBLess.visibility = View.INVISIBLE
        vb.devConBtnBAdd.visibility = View.INVISIBLE
    }

    fun mainMode() {
        vb.devConLinlayMainBar.visibility = View.VISIBLE
        vb.devConCoslayRGBBtn.visibility = View.VISIBLE
    }

    fun nightMode() {
        vb.devConLinlayNightMode.visibility = View.VISIBLE
        vb.devConCoslayRGBBtn.visibility = View.VISIBLE
    }

    fun colorMode() {

    }

    fun rgbMode() {

    }

    fun builtinMode() {

    }

    fun btnRTrigger() {
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

    fun btnGTrigger() {
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

    fun btnBTrigger() {
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


}