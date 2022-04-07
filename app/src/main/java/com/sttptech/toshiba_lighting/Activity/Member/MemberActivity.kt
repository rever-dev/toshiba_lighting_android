package com.sttptech.toshiba_lighting.Activity.Member

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.sttptech.toshiba_lighting.Activity.LaunchActivity.LaunchActivity
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.AppUtil.PermissionUtil
import com.sttptech.toshiba_lighting.R

class MemberActivity : AppCompatActivity() {

    companion object {
        fun isEmailValid(email: CharSequence?): Boolean {
            return Patterns.EMAIL_ADDRESS
                .matcher(email).matches()
        }
    }

    fun startLaunchActivity() {
        val intent = Intent(this, LaunchActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun checkLoginStatus(): Boolean {
        applicationContext.getSharedPreferences(AppKey.SHP_NAME, MODE_PRIVATE).apply {
            return getBoolean(AppKey.SHP_LOGIN, false) &&
                    getString(AppKey.SHP_ACCOUNT, null) != null &&
                    getString(AppKey.SHP_TOKEN, null) != null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_REQUEST_FINE_LOCATION)

        if (checkLoginStatus())
            startLaunchActivity()

    }
}