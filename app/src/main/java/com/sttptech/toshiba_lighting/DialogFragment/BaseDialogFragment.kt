package com.sttptech.toshiba_lighting.DialogFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.R
import java.lang.Exception

abstract class BaseDialogFragment(
    private var width: Int,
    private var height: Int,
    private var gravity: Int,
    private var anim: Boolean
) : DialogFragment() {

    fun showLoading() {
        try {
            BaseApplication.loadingView.show(parentFragmentManager, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissLoading() {
        try {
            BaseApplication.loadingView.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 去掉Dialog默認的padding
        val window = this.dialog!!.window
        window!!.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = width
        lp.height = height

        // 設置 Dialog 位置
        lp.gravity = gravity

        if (anim) {
            // 設置動畫
            lp.windowAnimations = R.style.BottomDialogAnimation
        }
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable())

    }
}