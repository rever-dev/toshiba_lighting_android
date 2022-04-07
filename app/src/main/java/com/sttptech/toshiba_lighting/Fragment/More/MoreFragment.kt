package com.sttptech.toshiba_lighting.Fragment.More

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.Activity.Member.MemberActivity
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.DialogFragment.VerificationCode.VerificationCodeDialog
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentMoreBinding
import dev.weiqi.resof.colorIntOf
import dev.weiqi.resof.stringOf

class MoreFragment : Fragment(), View.OnClickListener {
    
    private lateinit var vm: MoreViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[MoreViewModel::class.java]
    }
    
    private var _vb: FragmentMoreBinding? = null
    private val vb: FragmentMoreBinding get() = _vb!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vb = FragmentMoreBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListener()
        observerVM()
    }
    
    private fun setListener() {
        vb.moreTvInvite.setOnClickListener(this)
        vb.moreTvAcceptInvite.setOnClickListener(this)
        vb.moreTvUnbind.setOnClickListener(this)
        vb.moreTvLogout.setOnClickListener(this)
        vb.moreTvLogout2.setOnClickListener(this)
    }
    
    private fun observerVM() {
        vm.account.observe(viewLifecycleOwner) {
            val str = stringOf(R.string.signAccount) + it
            vb.moreTvAccount.text = str
            
        }
        
        vm.version.observe(viewLifecycleOwner) {
            val str = stringOf(R.string.currentVersion) + it
            vb.moreTvVersion.text = str
        }
        
        vm.shareStatus.observe(viewLifecycleOwner) {
            if (it) {
                vb.moreScrollView.visibility = View.GONE
                vb.moreLinearLayout.visibility = View.VISIBLE
            } else {
                vb.moreScrollView.visibility = View.VISIBLE
                vb.moreLinearLayout.visibility = View.GONE
            }
        }
        
        vm.shareEmail.observe(viewLifecycleOwner) {
            vb.moreTvShareEmail.text = it ?: "no account"
        }
    }
    
    override fun onClick(v: View?) {
        
        if (vm.touchBlocker.onTouch().not()) return
        
        when (v?.id) {
            
            R.id.more_tvInvite -> {
                val enterEmail = vb.moreEtInviteEmail.text.toString().trim()
                if (MemberActivity.isEmailValid(enterEmail)) {
                    Thread {
                        val result = vm.inviteMember(enterEmail)
                        requireActivity().runOnUiThread {
                            if (result)
                                Snackbar.make(
                                    requireView(),
                                    R.string.invite_success,
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                    .show()
                            else
                                Snackbar.make(
                                    requireView(),
                                    R.string.invite_fail,
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                    .show()
                        }
                    }.start()
                } else
                    Snackbar.make(
                        requireView(),
                        R.string.account_non_email_format,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                        .show()
            }
            
            
            R.id.more_tvAcceptInvite -> {
                VerificationCodeDialog().apply {
                    verifyCallback = object : VerificationCodeDialog.VerifyCallback {
                        override fun onVerify(result: Boolean) {
                            if (result) {
                                val loading = BaseApplication.loadingView
                                loading.show(parentFragmentManager, null)
                                Thread {
                                    val refRes =
                                        (this@MoreFragment.requireActivity() as MainActivity).refreshDataFromServer()
                                    this@MoreFragment.requireActivity().runOnUiThread {
                                        if (refRes)
                                            this@MoreFragment.vm.refresh()
                                        loading.dismiss()
                                    }
                                }.start()
                            } else {
                                Snackbar.make(
                                    requireView(),
                                    R.string.verify_fail,
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                    .show()
                            }
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            R.id.more_tvUnbind -> {
                val loading = BaseApplication.loadingView
                loading.show(parentFragmentManager, null)
                Thread {
                    val result = vm.unbindShare()
                    if (result) {
                        val refRes =
                            (requireActivity() as MainActivity).refreshDataFromServer()
                        requireActivity().runOnUiThread {
                            if (refRes)
                                this@MoreFragment.vm.refresh()
                            loading.dismiss()
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Snackbar.make(
                                requireView(),
                                R.string.unbind_fail,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                .show()
                            loading.dismiss()
                        }
                    }
                }.start()
            }
            
            R.id.more_tvLogout -> {
                (requireActivity() as MainActivity).logout()
            }
            
            R.id.more_tvLogout2 -> {
                (requireActivity() as MainActivity).logout()
            }
        }
    }
}