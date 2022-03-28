package com.sttptech.toshiba_lighting.Activity.NSD

import android.app.Activity
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdManager.ResolveListener
import android.net.nsd.NsdServiceInfo
import android.util.Log

class NSDClient(activity: Activity) : BaseNSD(activity) {
    private var mNSDDiscoveryListener: DiscoveryListener? = null
    private var mResolveListener: ResolveListener? = null
    private var mNsdServiceInfo: NsdServiceInfo? = null
    public override fun init() {
        super.init()
        mNSDDiscoveryListener = object : DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.i(TAG, "onStartDiscoveryFailed--> $serviceType:$errorCode")
            }
            
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.i(TAG, "onStopDiscoveryFailed--> $serviceType:$errorCode")
            }
            
            override fun onDiscoveryStarted(serviceType: String) {
                Log.i(TAG, "onDiscoveryStarted--> $serviceType")
            }
            
            override fun onDiscoveryStopped(serviceType: String) {
                Log.i(TAG, "onDiscoveryStopped--> $serviceType")
            }
            
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "onServiceFound Info: --> $serviceInfo")
                mNsdServiceInfo = serviceInfo
                resolveServer()
            }
            
            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "onServiceLost--> $serviceInfo")
                mNsdServiceInfo = null
            }
        }
        mResolveListener = object : ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d(TAG, "onResolveFailed,serviceInfo = $serviceInfo,errorCode = $errorCode")
            }
            
            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.i(
                    TAG, """serviceInfo = $serviceInfo,resolution : ${serviceInfo.serviceName} 
 host_from_server: ${serviceInfo.host}
 port from server: ${serviceInfo.port}"""
                )
                val hostAddress = serviceInfo.host.hostAddress
                Log.i(TAG, "hostAddress ip--> $hostAddress")
                stopDiscover()
            }
        }
    }
    
    fun discoverNsdServer() {
        mNsdManager!!.discoverServices(
            SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            mNSDDiscoveryListener
        )
    }
    
    fun resolveServer() {
        mNsdManager!!.resolveService(mNsdServiceInfo, mResolveListener)
    }
    
    fun stopDiscover() {
        mNsdManager!!.stopServiceDiscovery(mNSDDiscoveryListener)
    }
}