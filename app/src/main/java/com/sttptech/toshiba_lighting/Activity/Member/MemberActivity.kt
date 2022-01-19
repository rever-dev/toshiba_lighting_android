package com.sttptech.toshiba_lighting.Activity.Member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.AppUtil.PermissionUtil
import com.sttptech.toshiba_lighting.CustomView.LoadingView
import com.sttptech.toshiba_lighting.R
import java.util.jar.Manifest

class MemberActivity : AppCompatActivity() {

    companion object {
        fun isEmailValid(email: CharSequence?): Boolean {
            return Patterns.EMAIL_ADDRESS
                .matcher(email).matches()
        }
    }

    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun checkLoginStatus(): Boolean {
        applicationContext.getSharedPreferences(KeyOfShp.SHP_NAME, MODE_PRIVATE).apply {
            return getBoolean(KeyOfShp.SHP_LOGIN, false) &&
                    getString(KeyOfShp.SHP_ACCOUNT, null) != null &&
                    getString(KeyOfShp.SHP_TOKEN, null) != null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        PermissionUtil.requestPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (checkLoginStatus())
            startMainActivity()

    }
}