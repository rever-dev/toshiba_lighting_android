package com.sttptech.toshiba_lighting.AppUtil

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {

    const val LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
    const val CAMERA: String = Manifest.permission.CAMERA

    fun requestPermission(activity: Activity, permission: String) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            activity.applicationContext,
            permission
        )
        // 自定義的請求代碼
        val PERMISSION_REQUEST_FINE_LOCATION = 1
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // 沒有權限，申請權限。
            ActivityCompat.requestPermissions(
                activity, arrayOf(permission),
                PERMISSION_REQUEST_FINE_LOCATION
            )
        }
    }

}