package com.sttptech.toshiba_lighting.Fragment.Device.DeviceList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.sttptech.toshiba_lighting.Adapter.DeviceListAdapter
import com.sttptech.toshiba_lighting.Adapter.DeviceListAdapter.ItemClickListener
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.DialogFragment.ConnectToWiFi.ConnectToWiFiDialogFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentDeviceListBinding

class DeviceListFragment : Fragment() {

    companion object {

        const val TAG = "DeviceList"

        const val DEVICE_NAME = "dev_name"
        const val DEVICE_UID = "dev_uid"
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
        // button
        vb.devListIbtConWifi.setOnClickListener {
            ConnectToWiFiDialogFragment().show(parentFragmentManager, null)
        }

        // recyclerView
        vb.devListRecyclerView.layoutManager = object : LinearLayoutManager(requireContext()) {}
        vm.deviceList.observe(this, {
            vb.devListRecyclerView.adapter =
                object : DeviceListAdapter(vm.deviceList.value) {}
                    .also {
                        it.itemClickListener = object : ItemClickListener {
                            override fun onItemClick(dev: Device) {
                                val bundle = with(Bundle()) {
                                    putString(DEVICE_UID, dev.uId)
                                    putString(DEVICE_NAME, dev.name)
                                }
//                                Navigation.findNavController(view).navigate(R.id., bundle)
                            }
                        }
                    }
        })
    }

    override fun onStart() {
        super.onStart()
        refreshList()
    }

    fun refreshList() {
        vm.refreshData()
    }
}