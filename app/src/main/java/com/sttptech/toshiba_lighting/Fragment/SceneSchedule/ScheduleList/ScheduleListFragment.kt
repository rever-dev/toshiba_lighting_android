package com.sttptech.toshiba_lighting.Fragment.SceneSchedule.ScheduleList

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sttptech.toshiba_lighting.Adapter.DividerItemDecorator
import com.sttptech.toshiba_lighting.Adapter.SceneScheduleAdapter
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule
import com.sttptech.toshiba_lighting.DialogFragment.Schedule.AddSchedule.AddScheduleDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.Schedule.EditSchedule.EditScheduleDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentScheduleListBinding
import dev.weiqi.resof.colorIntOf
import dev.weiqi.resof.drawableOf

class ScheduleListFragment : Fragment(), View.OnClickListener {
    
    private lateinit var vm: ScheduleListViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[ScheduleListViewModel::class.java]
        vm.init()
    }
    
    private lateinit var vb: FragmentScheduleListBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentScheduleListBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    private val scheAdapter = SceneScheduleAdapter().apply {
        itemCallback = object : SceneScheduleAdapter.ItemClickCallback {
            override fun onSwitchChange(taskId: String, onOff: Boolean) {
                val loading = BaseApplication.loadingView
                loading.show(parentFragmentManager, null)
                Thread {
                    val result = vm.scheduleOnOff(taskId, onOff)
                    requireActivity().runOnUiThread {
                        if (result) {
                            Snackbar.make(
                                requireView(),
                                R.string.scheduleOnSuccess,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                .show()
                        } else {
                            Snackbar.make(
                                requireView(),
                                R.string.scheduleOnFail,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                .show()
                        }
                        loading.dismiss()
                    }
                }.start()
            }
            
            override fun onItemEdit(schedule: SceneSchedule) {
                EditScheduleDialogFragment(schedule).apply {
                    editScheduleCallback = object : EditScheduleDialogFragment.EditScheduleCallback {
                        override fun onEditDone() {
                            this@ScheduleListFragment.vm.init()
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
            override fun onItemDelete(schedule: SceneSchedule, adapterPos: Int) {
                AlertDialog.Builder(context)
                    .setTitle(R.string.deleteSchedule)
                    .setMessage(R.string.sureDeleteSchedule)
                    .setPositiveButton(
                        R.string.confirm
                    ) { dialog, _ ->
                        val loading = BaseApplication.loadingView
                        loading.show(parentFragmentManager, null)
                        
                        Thread {
                            val result = vm.scheduleDelete(schedule.taskId!!)
                            requireActivity().runOnUiThread {
                                if (result) {
                                    vm.init()
                                    
                                    Snackbar.make(
                                        requireView(),
                                        R.string.deleteScheduleSuccess,
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                        .show()
                                } else {
                                    Snackbar.make(
                                        requireView(),
                                        R.string.deleteScheduleFail,
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                        .show()
                                }
                                
                                dialog.dismiss()
                                loading.dismiss()
                            }
                        }.start()
                        
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        vb.sceneScheRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecorator(
                    drawableOf(R.drawable.divider_device_list)
                )
            )
            adapter = scheAdapter
        }
        
        setListener()
        observerVM()
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    private fun observerVM() {
        vm.loadingStatus.observe(viewLifecycleOwner) {
            val loading = BaseApplication.loadingView
            if (it)
                loading.show(parentFragmentManager, null)
            else
                loading.dismiss()
        }
    
        vm.scheList.observe(viewLifecycleOwner) {
        
            scheAdapter.submitList(it)
        }
    }
    
    private fun setListener() {
        vb.sceneScheIbtAdd.setOnClickListener(this)
    }
    
    override fun onClick(view: View?) {
        if (view == null) return
        if (vm.touchBlocker.onTouch().not()) return
    
        when (view.id) {
            R.id.sceneSche_ibtAdd -> {
                AddScheduleDialogFragment().apply {
                    addScheduleCallback = object : AddScheduleDialogFragment.AddScheduleCallback {
                        override fun onAddSchedule(sche: SceneSchedule) {
                            this@ScheduleListFragment.vm.init()
                        }
                    }
                }.show(parentFragmentManager, null)
            }
        }
    }
}