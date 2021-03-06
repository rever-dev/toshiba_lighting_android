package com.sttptech.toshiba_lighting.Fragment.Scene.SceneList

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.Adapter.SceneItemDraggingHelper
import com.sttptech.toshiba_lighting.Adapter.SceneListAdapter
import com.sttptech.toshiba_lighting.Adapter.SceneSelectAdapter
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.AppUtil.AppKey.SCENE_CREATE_FINISH
import com.sttptech.toshiba_lighting.AppUtil.AppKey.SCENE_EDIT_FINISH
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentSceneListBinding
import dev.weiqi.resof.colorIntOf
import dev.weiqi.resof.colorStateListOf
import dev.weiqi.resof.stringOf

class SceneListFragment : Fragment(), View.OnClickListener {
    
    private lateinit var vm: SceneListViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[SceneListViewModel::class.java]
    }
    
    private lateinit var vb: FragmentSceneListBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentSceneListBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    private var defAdapter = SceneListAdapter()
    private var selectAdapter = SceneSelectAdapter()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        defAdapter.apply {
            sceneClickCallback = object : SceneListAdapter.SceneClickCallback {
                override fun onSceneClick(seq: Int, scene: Scene) {
                    Thread {
                        vm.triggerScene(seq, scene)
                    }.start()
                }
            }
        
            sceneMoveCallback = object : SceneListAdapter.SceneMoveCallback {
                override fun onSceneMoveFinish() {
                    Logger.i("item move finish")
                    
                    BaseApplication.loadingView.show(parentFragmentManager, null)
                    Thread {
                        val result = vm.updateSceneOrder(defAdapter.currentList)
                        requireActivity().runOnUiThread{
                            if (result) {
                                selectAdapter.submitList(currentList)
                                
                                Snackbar.make(requireView(), R.string.sceneSortSuccess, Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                    .show()
                            } else {
                                Snackbar.make(requireView(), R.string.sceneSortFail, Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                    .show()
                            }
                            BaseApplication.loadingView.dismiss()
                        }
                    }.start()
                }
            }
        }
    
        if (vb.sceneListRecyclerView.layoutManager == null)
            vb.sceneListRecyclerView.layoutManager = GridLayoutManager(context, 2)
    
        vb.sceneListRecyclerView.apply {
            adapter = defAdapter
            ItemTouchHelper(SceneItemDraggingHelper()).attachToRecyclerView(this)
        }
    
        observerVM()
        setListener()
    }
    
    override fun onResume() {
        super.onResume()
        if (vm.mode.value != false)
            vm.mode.value = false
        
        vm.refresh()
    }
    
    private fun observerVM() {
    
        /** receive from create & edit callback */
        findNavController()
            .currentBackStackEntry!!
            .savedStateHandle.apply {
                getLiveData<Boolean>(SCENE_CREATE_FINISH)
                    .observe(viewLifecycleOwner) {
                        if (!it) return@observe
                        vm.mode.value = false
                        vm.refresh()
                    }
            
                getLiveData<Boolean>(SCENE_EDIT_FINISH)
                    .observe(viewLifecycleOwner) {
                        if (!it) return@observe
                        vm.mode.value = false
                        vm.refresh()
                    }
            }
        
        vm.sceneList.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            defAdapter.submitList(it)
            selectAdapter.submitList(it)
        }
        
        vm.mode.observe(viewLifecycleOwner) {
            
            val tv = vb.sceneListTvEditMode
            
            if (it) {
                tv.text = stringOf(R.string.cancelEdit)
                tv.setTextColor(colorStateListOf(R.color.text_color_red, null))
                vb.sceneListConslayBottomEditMode.visibility = View.VISIBLE
                vb.sceneListRecyclerView.adapter = selectAdapter
//                selectAdapter.submitList(vm.sceneList.value)
            } else {
                tv.text = stringOf(R.string.edit)
                tv.setTextColor(colorStateListOf(R.color.ios_blue, null))
                vb.sceneListConslayBottomEditMode.visibility = View.GONE
                vb.sceneListRecyclerView.adapter = defAdapter
//                defAdapter.submitList(vm.sceneList.value)
            }
        }
    }
    
    private fun setListener() {
        vb.sceneListTvEditMode.setOnClickListener(this)
        vb.sceneListTvEdit.setOnClickListener(this)
        vb.sceneListIbtAdd.setOnClickListener(this)
        vb.sceneListTvDelete.setOnClickListener(this)
    }
    
    override fun onClick(v: View?) {
        
        when (v?.id) {
            
            /** edit mode */
            R.id.sceneList_tvEditMode -> {
                vm.mode.value = vm.mode.value?.not()
            }
            
            /** create scene */
            R.id.sceneList_ibtAdd -> {
                findNavController()
                    .navigate(R.id.action_sceneListFragment_to_sceneCreateFragment)
            }
            
            /** edit scene */
            R.id.sceneList_tvEdit -> {
                val scene = selectAdapter.getSelectScene()
                findNavController()
                    .navigate(
                        R.id.action_sceneListFragment_to_sceneEditFragment, bundleOf(
                            Pair(AppKey.SCENE_SEQ, scene?.seq),
                            Pair(AppKey.FROM_PAGE_NAME, stringOf(R.string.scene))
                        )
                    )
            }
            
            /** delete scene */
            R.id.sceneList_tvDelete -> {
                
                val selScene = selectAdapter.getSelectScene()
                
                if (selScene == null) {
                    Snackbar.make(requireView(), R.string.mustSelectAScene, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                        .show()
                    return
                }
                
                AlertDialog.Builder(context)
                    .setTitle(R.string.deleteTitle)
                    .setMessage(R.string.deleteMessage)
                    .setPositiveButton(
                        R.string.confirm
                    ) { dialog, _ ->
                        Thread {
                            
                            val result = vm.delete(selScene.uId)
                            requireActivity().runOnUiThread {
                                if (result) {
                                    Snackbar.make(
                                        requireView(),
                                        R.string.sceneDeleteSuccess,
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                        .show()
                                    
                                    vm.refresh()
                                    dialog.dismiss()
                                } else {
                                    Snackbar.make(
                                        requireView(),
                                        R.string.sceneDeleteFail,
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                        .show()
                                    
                                    dialog.dismiss()
                                }
                            }
                        }.start()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
            
        }
    }
}