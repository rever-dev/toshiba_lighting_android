package com.sttptech.toshiba_lighting.DialogFragment.VerificationCode

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogVerificationCodeBinding
import dev.weiqi.resof.colorIntOf

class VerificationCodeDialog : BaseDialogFragment(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.CENTER,
    true
), View.OnClickListener {
    
    interface VerifyCallback {
        fun onVerify(result: Boolean)
    }
    
    var verifyCallback: VerifyCallback? = null
    
    lateinit var vm: VerificationCodeViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[VerificationCodeViewModel::class.java]
    }
    
    lateinit var vb: DialogVerificationCodeBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = DialogVerificationCodeBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListener()
        observerVM()
    }
    
    private fun observerVM() {
    
    }
    
    private fun setListener() {
        vb.inviVeriTvConfirm.setOnClickListener(this)
        vb.inviVeriTvCancel.setOnClickListener(this)
    }
    
    override fun onClick(v: View?) {
        
        when (v?.id) {
            
            R.id.inviVeri_tvConfirm -> {
                val vCode = vb.inviVeriEtVerifyCode.text.toString()
                
                if (vCode.length != 6) {
                    Snackbar.make(
                        requireView(),
                        R.string.verification_code_is_six_digits,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                        .show()
                    return
                }
                
                val loading = BaseApplication.loadingView
                loading.show(parentFragmentManager, null)
                Thread {
                    val result = vm.verify(vCode)
                    requireActivity().runOnUiThread {
                        verifyCallback?.onVerify(result)
                        loading.dismiss()
                        dismiss()
                    }
                }.start()
            }
            
            R.id.inviVeri_tvCancel -> {
                dismiss()
            }
        }
    }
}