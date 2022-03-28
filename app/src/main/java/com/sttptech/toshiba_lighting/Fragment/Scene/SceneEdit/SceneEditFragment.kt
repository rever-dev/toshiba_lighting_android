package com.sttptech.toshiba_lighting.Fragment.Scene.SceneEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.databinding.FragmentSceneEditBinding

class SceneEditFragment : Fragment() {
    
    companion object {
        fun newInstance() = SceneEditFragment()
    }
    
    private lateinit var viewModel: SceneEditViewModel
    
    
    private lateinit var vb: FragmentSceneEditBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentSceneEditBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[SceneEditViewModel::class.java]
        // TODO: Use the ViewModel
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.sceneEditTv.text = arguments?.getString(AppKey.SCENE_NAME, "No found scene name")
    }
    
}