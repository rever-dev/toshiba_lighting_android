package com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleScene

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogScheduleSelectSceneBinding

class ScheduleSceneDialogFragment(val currentScene: Scene?) : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.MATCH_PARENT,
    Gravity.BOTTOM,
    true
), View.OnClickListener {
    
    interface SceneSelectCallback {
        fun onSceneSelect(scene: Scene?)
    }
    
    var sceneSelectCallback: SceneSelectCallback? = null
    
    private lateinit var vm: ScheduleSceneViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[ScheduleSceneViewModel::class.java]
    }
    
    private lateinit var vb: DialogScheduleSelectSceneBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogScheduleSelectSceneBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    private val sceneAdapter = ScheduleSceneAdapter(currentScene).apply {
        sceneSelector = object : ScheduleSceneAdapter.SceneSelector {
            override fun onSceneSelect(scene: Scene, checkBox: CheckBox) {
                vm.selector.onSelectScene(scene, checkBox)
            }
    
            override fun unSceneSelect(scene: Scene) {
                vm.selector.unSelectScene(scene)
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        vb.scheSceneRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
            adapter = sceneAdapter
        }
        
        setListener()
        observerVM()
    }
    
    private fun observerVM() {
        vm.sceneList.observe(viewLifecycleOwner) {
            sceneAdapter.submitList(it)
    
//            if (currentScene != null) {
//                var scenePos: Int? = null
//                for (i in it.indices ) {
//                    if (it[i].uId == currentScene.uId) {
//                        scenePos = i
//                        break
//                    }
//                }
//
//                if (scenePos == null) return@observe
//
//                val viewHolder = vb.scheSceneRecyclerView.findViewHolderForAdapterPosition(scenePos)
//                (viewHolder as ScheduleSceneAdapter.ViewHolder).checkBox.isChecked= true
//            }
        }
    }
    
    private fun setListener() {
    vb.scheSceneTvBack.setOnClickListener(this)
    }
    
    override fun onClick(v: View?) {
        
        when (v?.id) {
            
            R.id.scheScene_tvBack -> {
                sceneSelectCallback?.onSceneSelect(vm.selector.selectScene)
                dismiss()
            }
        }
    }
}