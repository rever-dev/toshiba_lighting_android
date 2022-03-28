package com.sttptech.toshiba_lighting.Activity.NSD

import android.app.Activity
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo
import android.util.Log


open class NSDServer(mActivity: Activity) : BaseNSD(mActivity) {
    private var mRegistrationListener: RegistrationListener? = null
    override fun init() {
        super.init()
        mRegistrationListener = object : RegistrationListener {
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "NsdServiceInfo onRegistrationFailed")
            }
            
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.i(
                    TAG,
                    "onUnregistrationFailed serviceInfo: $serviceInfo ,errorCode:$errorCode"
                )
            }
            
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "onServiceRegistered: $serviceInfo")
            }
            
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "onServiceUnregistered serviceInfo: $serviceInfo")
            }
        }
    }
    
    fun registerService(serviceName: String?, port: Int) {
        val serviceInfo = NsdServiceInfo()
        serviceInfo.serviceName = serviceName
        serviceInfo.port = port
        serviceInfo.serviceType = SERVICE_TYPE //客戶端發現服務器是需要對應的這個Type字符串
        mNsdManager?.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener)
    }
    
    fun stopNSDServer() {
        mNsdManager?.unregisterService(mRegistrationListener)
    }
}