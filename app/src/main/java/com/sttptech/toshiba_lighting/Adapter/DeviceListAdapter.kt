package com.sttptech.toshiba_lighting.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.R

class DeviceListAdapter :
    ListAdapter<Device, DeviceListAdapter.ViewHolder>(DiffCallback()) {
    
    interface ItemActionListener {
        fun onItemClick(dev: Device)
        
        fun onItemSwitchChange(dev: Device, isChecked: Boolean)
    }
    
    var itemActionListener: ItemActionListener? = null
    
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val tvName: TextView = v.findViewById(R.id.itemDevList_tvName)
        private val tvModel: TextView = v.findViewById(R.id.itemDevList_tvModel)
        private val tvGroup: TextView = v.findViewById(R.id.itemDevList_tvGroup)
        
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch: Switch = v.findViewById(R.id.itemDevList_sw)
        
        fun bindView(data: Device?) {
            if (data == null) return
            
            switch.setOnCheckedChangeListener(null)
            
            tvName.text = data.name
            tvModel.text = data.model
            tvGroup.text = data.group?.groupName
            
            if (data is CeilingLight)
                switch.isChecked = !(data.opMode == 1 && data.mBr == 0)
            
            
            
            itemView.setOnClickListener { itemActionListener?.onItemClick(data) }
            
            switch.setOnCheckedChangeListener { _, isChecked ->
                run {
                    itemActionListener?.onItemSwitchChange(data, isChecked)
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_device_list, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }
    
    class DiffCallback : DiffUtil.ItemCallback<Device>() {
        
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
//            Logger.d("item same result: ${oldItem == newItem}")
//            Logger.d("old item same result: ${oldItem.macId}")
//            Logger.d("new item same result: ${newItem.macId}")
            return oldItem.macId == newItem.macId
        }
        
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
//            Logger.d("content result: ${oldItem == newItem}")
//            Logger.d("content result old item: $oldItem")
//            Logger.d("content result new item: $newItem")
            return oldItem == newItem
        }
    }
}