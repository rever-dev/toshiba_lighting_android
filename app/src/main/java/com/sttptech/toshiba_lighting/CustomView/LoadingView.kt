package com.sttptech.toshiba_lighting.CustomView

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.sttptech.toshiba_lighting.R
import kotlin.math.log

class LoadingView : DialogFragment() {

    companion object {
        private const val TAG = "LoadingView"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.dialog!!.setCancelable(false)
        val window = this.dialog!!.window
        window!!.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable())
        return inflater.inflate(R.layout.dialog_loading, null)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag);
        } catch (e: Exception) {
            //同一实例使用不同的tag会异常,这里捕获一下
            e.printStackTrace()
        }

        Log.d(TAG, "＊＊＊＊＊ Show ＊＊＊＊＊")
    }

    override fun dismiss() {
        super.dismiss()

        Log.d(TAG, "＊＊＊＊＊＊ Dismiss ＊＊＊＊＊")
    }
}