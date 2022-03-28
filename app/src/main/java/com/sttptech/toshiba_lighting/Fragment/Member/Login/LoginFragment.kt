package com.sttptech.toshiba_lighting.Fragment.Member.Login

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sttptech.toshiba_lighting.Activity.Member.MemberActivity
import com.sttptech.toshiba_lighting.Adapter.BannerAdapter
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var vm: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private var _vb: FragmentLoginBinding? = null
    private val vb: FragmentLoginBinding get() = _vb!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vb = FragmentLoginBinding.inflate(inflater, container, false)
        return vb.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // banner
        vb.fragLoginBanner.adapter = BannerAdapter(BannerAdapter.IMGBean.data)

        // editText account
        vb.fragLoginEtAccount.doOnTextChanged { text, _, _, _ ->
            vb.fragLoginTvAccountError.visibility = View.VISIBLE
            errorMsgCheck(vb.fragLoginTvAccountError, !MemberActivity.isEmailValid(text))
        }

        // editText password
        vb.fragLoginEtPassword.doOnTextChanged { text, _, _, _ ->
            vb.fragLoginTvPasswordError.visibility = View.VISIBLE
            errorMsgCheck(vb.fragLoginTvPasswordError, text.toString().length < 8)
        }

        // password visibility
        vb.fragLoginImgPasswordShowHide.setOnClickListener {
            vm.passwordVisibility.value = !vm.passwordVisibility.value!!
        }

        // login
        vb.fragLoginBtnLogin.setOnClickListener {
            vm.login(
                vb.fragLoginEtAccount.text.toString(),
                vb.fragLoginEtPassword.text.toString()
            )
        }
    }

    override fun onStart() {
        super.onStart()

        // Password visibility
        vm.passwordVisibility.observe(viewLifecycleOwner) {
            val eye = vb.fragLoginImgPasswordShowHide
            val pwd = vb.fragLoginEtPassword
            if (it) {
                pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                eye.setImageResource(R.drawable.ic_visibility_24)
            } else {
                pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                eye.setImageResource(R.drawable.ic_visibility_off_24)
            }
        }
    
        // Loading
        vm.loadStatus.observe(this) {
            val loadingView = BaseApplication.loadingView
    
            if (it)
                loadingView.show(parentFragmentManager, null)
            else
                loadingView.dismiss()
        }
    
        // Login status
        vm.loginStatus.observe(this) {
            when (it) {
                // success -> start to MainActivity
                1 -> {
//                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show()
//                    Snackbar.make(requireContext(), requireView(), getString(R.string.login_success), Snackbar.LENGTH_SHORT).show()
                    (activity as MemberActivity).startMainActivity()
                }
                // fail -> show toast
                2 -> {
//                    Toast.makeText(context, R.string.login_fail, Toast.LENGTH_SHORT).show()
                    Snackbar.make(requireView(), R.string.login_fail, Snackbar.LENGTH_SHORT).show()
                    vm.loginStatus.value = 0
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _vb = null
    }

    /**
     * check input 'Account' & 'Password'.
     * if it doesn't fit the format, show the error message.
     */
    private fun errorMsgCheck(errorView: TextView, error: Boolean) {
        if (error) {
            errorView.setTextColor(resources.getColor(R.color.text_color_red, null))
            setTextViewDrawableColor(errorView, R.color.text_color_red)
            errorView.paintFlags = errorView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        } else {
            errorView.setTextColor(resources.getColor(R.color.edittext_bg_gray, null))
            setTextViewDrawableColor(errorView, R.color.edittext_bg_gray)
            errorView.paintFlags = errorView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(textView.context, color),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }
}