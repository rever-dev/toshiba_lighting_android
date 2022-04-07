package com.sttptech.toshiba_lighting.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sttptech.toshiba_lighting.Data.Bean.SceneSchedule
import com.sttptech.toshiba_lighting.R
import dev.weiqi.resof.stringOf
import java.text.DecimalFormat

class SceneScheduleAdapter :
    ListAdapter<SceneSchedule, SceneScheduleAdapter.ViewHolder>(DiffCallback()) {
    
    interface ItemClickCallback {
        
        fun onSwitchChange(taskId: String, onOff: Boolean)
        
        fun onItemEdit(schedule: SceneSchedule)
        
        fun onItemDelete(schedule: SceneSchedule, adapterPos: Int)
    }
    
    var itemCallback: ItemClickCallback? = null
    
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        
        private val tvAmPm = v.findViewById<TextView>(R.id.scheItem_tvAmPm)
        private val tvName = v.findViewById<TextView>(R.id.scheItem_tvName)
        private val tvWeek = v.findViewById<TextView>(R.id.scheItem_tvLoopWeek)
        private val tvTime = v.findViewById<TextView>(R.id.scheItem_tvTime)
        
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private val switch = v.findViewById<Switch>(R.id.scheItem_switch)
        private val fraDelete = v.findViewById<FrameLayout>(R.id.scheItem_frameDelete)
        
        fun setListener(data: SceneSchedule, position: Int) {
            switch.setOnCheckedChangeListener { _, b ->
                currentList[position].statusDB = if (b) "Y" else "N"
                itemCallback?.onSwitchChange(data.taskId!!, b)
            }
            
            itemView.setOnClickListener {
                itemCallback?.onItemEdit(data)
            }
            
            fraDelete.setOnClickListener {
                itemCallback?.onItemDelete(data, position)
            }
        }
        
        fun bindView(data: SceneSchedule?) {
            if (data == null) return
            
            switch.isChecked = data.statusDB.equals("Y")
            
            if (data.payload == null) return
            
            // am.pm & time
            val decimalFormat = DecimalFormat("00")
            val minOfDay = data.payload!![0].minuteOfDay!!
            if (minOfDay < 720) {
                tvAmPm.text = stringOf(R.string.am)
                val strTime: String =
                    decimalFormat.format(minOfDay / 60)
                        .toString() + ":" +
                            decimalFormat.format(minOfDay % 60)
                tvTime.text = strTime
            } else {
                tvAmPm.text = stringOf(R.string.pm)
                val strTime: String =
                    if (minOfDay in 720..779) {
                        12.toString() + ":" + decimalFormat.format(
                            (minOfDay - 720) % 60
                        )
                    } else {
                        decimalFormat.format((minOfDay - 720) / 60)
                            .toString() + ":" +
                                decimalFormat.format(
                                    (minOfDay - 720) % 60
                                )
                    }
                tvTime.text = strTime
            }
            
            // name
            tvName.text = data.sceneName
            
            // week
            val strBuffer = StringBuffer()
            var checker = 0
            
            for (temp in data.payload!!) {
                strBuffer.append(" ")
                when (temp.dayOfWeek) {
                    1 -> {
                        strBuffer.append(itemView.context.getString(R.string.monday))
                        checker++
                    }
                    2 -> {
                        strBuffer.append(itemView.context.getString(R.string.tuesday))
                        checker += 2
                    }
                    3 -> {
                        strBuffer.append(itemView.context.getString(R.string.wednesday))
                        checker += 4
                    }
                    4 -> {
                        strBuffer.append(itemView.context.getString(R.string.thursday))
                        checker += 8
                    }
                    5 -> {
                        strBuffer.append(itemView.context.getString(R.string.friday))
                        checker += 16
                    }
                    6 -> {
                        strBuffer.append(itemView.context.getString(R.string.saturday))
                        checker += 32
                    }
                    7 -> {
                        strBuffer.append(itemView.context.getString(R.string.sunday))
                        checker += 64
                    }
                }
            }
            
            when (checker) {
                31 -> tvWeek.text = itemView.context.getString(R.string.everyWorkingDay)
                96 -> tvWeek.text = itemView.context.getString(R.string.everyWeekend)
                127 -> tvWeek.text = itemView.context.getString(R.string.everyDay)
                else -> tvWeek.text = strBuffer.toString()
            }
            
        }
        
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<SceneSchedule>() {
        override fun areItemsTheSame(oldItem: SceneSchedule, newItem: SceneSchedule): Boolean {
            return oldItem.taskId == newItem.taskId
        }
        
        override fun areContentsTheSame(oldItem: SceneSchedule, newItem: SceneSchedule): Boolean {
            return oldItem.payload == newItem.payload
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_scene_schedule, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheData = currentList[position]
        holder.bindView(scheData)
        holder.setListener(scheData, position)
    }
}