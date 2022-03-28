package com.sttptech.toshiba_lighting.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.R
import dev.weiqi.resof.stringOf

class SceneDeviceAdapter : ListAdapter<Device, SceneDeviceAdapter.Viewholder>(DiffCallback()) {
    
    interface DeviceSelectedCallback {
        
        fun onSelected(dev: Device)
        
        fun onUnselected(dev: Device)
    }
    
    var callback: DeviceSelectedCallback? = null
    
    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbGroup = itemView.findViewById<CheckBox>(R.id.sceneDevice_cbGroup)
        val tvName = itemView.findViewById<TextView>(R.id.sceneDevice_tvName)
        
        fun bindView(data: Device) {
            cbGroup.text = data.group?.groupName
            tvName.text = data.name
            
            cbGroup.setOnCheckedChangeListener { _, isSelected ->
                if (isSelected)
                    callback?.onSelected(data)
                else
                    callback?.onUnselected(data)
            }
            
            itemView.setOnClickListener {
                Navigation.findNavController(itemView)
                    .navigate(
                        R.id.action_sceneCreateFragment_to_deviceControlFragment,
                        bundleOf(
                            Pair(AppKey.DEVICE_UID, data.uId),
                            Pair(AppKey.FROM_PAGE_NAME, stringOf(R.string.createScene))
                        )
                    )
            }
        }
    }
    
    @SuppressLint("DiffUtilEquals")
    class DiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.macId == newItem.macId
        }
        
        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem == newItem
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_scene_device, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.bindView(currentList[position])
    }
    
}