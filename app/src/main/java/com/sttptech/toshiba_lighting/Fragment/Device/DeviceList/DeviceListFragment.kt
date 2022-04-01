package com.sttptech.toshiba_lighting.Fragment.Device.DeviceList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sttptech.toshiba_lighting.Adapter.DeviceListAdapter
import com.sttptech.toshiba_lighting.Adapter.DeviceListAdapter.ItemActionListener
import com.sttptech.toshiba_lighting.Adapter.DividerItemDecorator
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.DialogFragment.ConnectToWiFi.ConnectToWiFiDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentDeviceListBinding
import dev.weiqi.resof.drawableOf
import dev.weiqi.resof.stringOf


class DeviceListFragment : Fragment() {
    
    companion object {
        
        const val TAG = "DeviceList"
    }
    
    private lateinit var vm: DeviceListViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[DeviceListViewModel::class.java]
    }
    
    private lateinit var vb: FragmentDeviceListBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentDeviceListBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        /** adapter item callback */
        adapter.itemActionListener = object : ItemActionListener {
            override fun onItemClick(dev: Device) {
                val bundle = Bundle().apply {
                    putString(AppKey.DEVICE_UID, dev.uId)
                    putString(AppKey.DEVICE_NAME, dev.name)
                    putString(
                        AppKey.FROM_PAGE_NAME,
                        vb.devListTvTitle.text.toString()
                    )
                }
                Navigation.findNavController(view).navigate(
                    R.id.action_deviceListFragment_to_deviceControlFragment,
                    bundle
                )
            }
            
            override fun onItemSwitchChange(dev: Device, isChecked: Boolean) {
                vm.devOnOff(dev, isChecked)
            }
        }
        
        /** adapter divider */
        vb.devListRecyclerView.addItemDecoration(
            DividerItemDecorator(
                drawableOf(R.drawable.divider_device_list)
            )
        )
        
        observerVM()
        setListener()
    }
    
    override fun onResume() {
        super.onResume()
        vm.getStatus()
    }
    
    override fun onStop() {
        super.onStop()
        vm.unListenerTopic()
    }
    
    private val adapter = DeviceListAdapter()
    
    private fun observerVM() {
        
        /** tab layout */
        vm.groupList.observe(viewLifecycleOwner) {
            
            vb.devListTabLayout.run {
                
                // remove all tabs, add default tab ("all")
                this.removeAllTabs()
                val newTab = this.newTab()
                newTab.text = getString(R.string.all)
                this.addTab(newTab)
                
                // add tab -> every one group
                if (it != null)
                    for (group in it) {
                        if (group.devUuids!!.isNotEmpty()) {
                            val tabView = vb.devListTabLayout.newTab()
                            tabView.text = group.groupName
                            vb.devListTabLayout.addTab(tabView)
                        }
                    }
                
            }
        }
        
        vm.deviceList.observe(viewLifecycleOwner) { data ->
            
            /** adapter */
            if (vb.devListRecyclerView.adapter == null)
                vb.devListRecyclerView.adapter = adapter
            
            if (vb.devListRecyclerView.layoutManager == null)
                vb.devListRecyclerView.layoutManager = LinearLayoutManager(context)
            
            
            val selectedText = vb.devListTabLayout.run {
                val tab = getTabAt(selectedTabPosition)
                tab?.text.toString()
            }
            
            val filterData = if (selectedText == stringOf(R.string.all))
                data?.toList()
            else
                data?.filter { it.group?.groupName == selectedText }?.toList()
            
            adapter.submitList(filterData)
        }
        
        vm.updateStatus.observe(viewLifecycleOwner) {
            val list = adapter.currentList
        }
    }
    
    private fun setListener() {
        
        /** button */
        vb.devListIbtConWifi.setOnClickListener {
            ConnectToWiFiDialogFragment().show(parentFragmentManager, null)
        }
        
        /** tab layout */
        vb.devListTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == null) return
                
                val filterData = if (tab.text == stringOf(R.string.all))
                    vm.deviceList.value
                else
                    vm.deviceList.value?.filter { it.group?.groupName == tab.text }
                
                adapter.submitList(filterData)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    fun refreshList() {
        vm.refreshData()
    }
}
