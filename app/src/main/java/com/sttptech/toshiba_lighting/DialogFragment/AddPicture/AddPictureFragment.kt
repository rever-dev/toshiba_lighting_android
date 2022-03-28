package com.sttptech.toshiba_lighting.DialogFragment.AddPicture

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogSceneAddPictureBinding

class AddPictureFragment : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    Gravity.BOTTOM,
    true
), View.OnClickListener {
    
    interface ButtonClickCallback {
        fun onTakePicture()
        
        fun onPickPicture()
    }
    
    var callback: ButtonClickCallback? = null
    
    private lateinit var vm: AddPictureViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    private lateinit var vb: DialogSceneAddPictureBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = DialogSceneAddPictureBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListener()
    }
    
    private fun setListener() {
        vb.addPicturePick.setOnClickListener(this)
        vb.addPictureTake.setOnClickListener(this)
    }
    
    override fun onClick(v: View?) {
        
        when(v?.id) {
            
            R.id.addPicture_take -> {
                callback?.onTakePicture()
            }
            
            R.id.addPicture_pick -> {
                callback?.onPickPicture()
            }
            
        }
    }
}