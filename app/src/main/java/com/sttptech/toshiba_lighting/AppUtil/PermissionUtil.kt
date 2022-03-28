package com.sttptech.toshiba_lighting.AppUtil

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    
    const val PERMISSION_REQUEST_FINE_LOCATION = 1
    private const val LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
    
    const val PERMISSION_REQUEST_CAMERA = 2
    private const val CAMERA: String = Manifest.permission.CAMERA
    
    fun checkPermission(activity: Activity, permission: Int): Boolean {
        val perm = when (permission) {
            1 -> LOCATION
        
            2 -> CAMERA
        
            else -> ""
        }
        
        val permissionCheck = ContextCompat.checkSelfPermission(
            activity.applicationContext,
            perm
        )
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }
    
    fun requestPermission(activity: Activity, permission: Int) {
        val perm = when (permission) {
            1 -> LOCATION
            
            2 -> CAMERA
            
            else -> ""
        }
        
        val permissionCheck = ContextCompat.checkSelfPermission(
            activity.applicationContext,
            perm
        )
        
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // 沒有權限，申請權限。
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(perm),
                permission
            )
        }
    }

}