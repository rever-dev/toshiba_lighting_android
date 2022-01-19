package com.sttptech.toshiba_lighting.DialogFragment.PairDevice

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.Adapter.PairDeviceAdapter
import com.sttptech.toshiba_lighting.Adapter.PairDeviceAdapter.ButtonCallback
import com.sttptech.toshiba_lighting.Adapter.PairDeviceAdapter.CheckBoxCallback
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.DialogFragment.BaseDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.GroupsSelectedDialogFragment
import com.sttptech.toshiba_lighting.DialogFragment.GroupsSelectedDialogFragment.GroupSelectedCallback
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.DialogfragPairDeviceBinding

class PairDeviceDialogFragment(var dev: Device) : BaseDialogFragment(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.MATCH_PARENT,
    Gravity.CENTER,
    true
) {

    interface PairFinishCallback {
        fun pairFinish()
    }

    var callbackList: MutableList<PairFinishCallback> = mutableListOf()

    fun addCallback(callback: PairFinishCallback) {
        callbackList.add(callback)
    }

    companion object {

        private const val TAG: String = "PairDevice"
    }

    private lateinit var vm: PairDeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[PairDeviceViewModel::class.java]
        vm.espList.value!!.add(dev)
    }

    private lateinit var vb: DialogfragPairDeviceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        vb = DialogfragPairDeviceBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PairDeviceAdapter(vm.espList.value!!)
        adapter.btnCallback = object : ButtonCallback {
            override fun onBtnClick(dev: Device) {
                vm.mqttIdentify(dev)
            }
        }
        adapter.checkBoxCallback = object : CheckBoxCallback {
            override fun onCheck(isChecked: Boolean, dev: Device, position: Int) {

                if (isChecked) { // if checkBox is checked, show the GroupSelectorDialog
                    GroupsSelectedDialogFragment().apply {
                        this.groupSelCallback = object : GroupSelectedCallback {
                            override fun onGroupSelected(group: String?) {
                                if (group != null)
                                    dev.group = Group(group)
                                else
                                    dev.group = null

                                adapter.data[position] = dev
                                vb.pairDevRecyclerView.adapter!!.notifyItemChanged(position)
                                if (group != null)
                                    vm.selDev(true, dev)
                            }
                        }
                    }.show(parentFragmentManager, null)

                } else { // if not cancel checkBox select
                    dev.group = null
                    adapter.data[position] = dev
                    vb.pairDevRecyclerView.adapter!!.notifyItemChanged(position)

                    vm.selDev(false, dev)
                }
            }
        }
        vb.pairDevRecyclerView.layoutManager = object : LinearLayoutManager(context) {}
        vb.pairDevRecyclerView.adapter = adapter

        vb.pairDevTvPair.setOnClickListener {
            if (vm.pairList.value!!.size != 0) {
                dismissLoading()
                (activity as MainActivity).esptouchStop()
                showLoading()
                vm.startPair()
            } else {
                Toast.makeText(context, getString(R.string.pleasSelectDevice), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        vb.pairDevTvCancel.setOnClickListener {
            dismissLoading()
            (activity as MainActivity).esptouchStop()
            showLoading()
            vm.mqttNetworkResetAll()
        }
    }

    override fun onStart() {
        super.onStart()
        vm.signupStatus.observe(this, {
            when (it) {
                // default
                0 -> {}

                // sign & network reset finish
                1 -> {
                    dismissLoading()
                    dismiss()
                }

                // fail
                2 -> {
                    dismissLoading()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (callback in callbackList) {
            callback.pairFinish()
        }
    }

    fun addDevice(dev: Device) {
        (vb.pairDevRecyclerView.adapter as PairDeviceAdapter).addData(dev)
        vm.espList.value?.add(dev)
    }

}