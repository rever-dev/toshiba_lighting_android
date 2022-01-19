package com.sttptech.toshiba_lighting.DialogFragment.ConnectToWiFi

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogfragConnectToWifiBinding

class ConnectToWiFiDialogFragment : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.BOTTOM,
    true
) {

    companion object {

        private const val TAG = "ConnectToWiFi"
    }

    private lateinit var vm: ConnectToWiFiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[ConnectToWiFiViewModel::class.java]
        vm.getWifiInfo()
    }

    private lateinit var vb: DialogfragConnectToWifiBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogfragConnectToWifiBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // imageButton visibility
        vb.conWifiIbtPwdVisib.setOnClickListener {
            vm.pwdVisi.value = !vm.pwdVisi.value!!
        }

        // button confirm
        vb.conWifiBtnConfirm.setOnClickListener {
            vm.startESP(
                activity!!,
                vb.conWifiEtPwd.text.toString()
            )
            dismiss()
        }

        // button cancel
        vb.conWifiBtnCancel.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()

        // imageButton visibility
        vm.pwdVisi.observe(this, {
            val pwd = vb.conWifiEtPwd
            val visi = vb.conWifiIbtPwdVisib

            if (it) { // visible
                pwd.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                visi.setImageResource(R.drawable.ic_visibility_24)
                visi.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.ios_blue, null))

            } else { // invisible
                pwd.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                visi.setImageResource(R.drawable.ic_visibility_off_24)
                visi.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.black, null))
            }
        })

        // textView wifi name
        vm.wifiName.observe(this, {
            if (it != null) {
                val strConWifi = resources.getString(R.string.connectWifiTo) + it
                vb.conWifiTvConWifi.text = strConWifi
            } else {
                vb.conWifiTvConWifi.text = resources.getString(R.string.noWifiAvailable)
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

}