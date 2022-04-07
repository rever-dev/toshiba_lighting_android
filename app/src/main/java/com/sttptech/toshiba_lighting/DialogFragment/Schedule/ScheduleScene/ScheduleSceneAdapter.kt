package com.sttptech.toshiba_lighting.DialogFragment.Schedule.ScheduleScene

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.R

class ScheduleSceneAdapter(val currentScene: Scene?) : ListAdapter<Scene, ScheduleSceneAdapter.ViewHolder>(DiffCallback()) {
    
    interface SceneSelector {
        fun onSceneSelect(scene: Scene, checkBox: CheckBox)
        
        fun unSceneSelect(scene: Scene)
    }
    
    var sceneSelector: SceneSelector? = null
    
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        
        val tvName = v.findViewById<TextView>(R.id.itemScheScene_tvName)
        val checkBox = v.findViewById<CheckBox>(R.id.itemScheScene_cb)
        
        fun bindView(data: Scene) {
            tvName.text = data.name
        }
        
        fun setListener(data: Scene) {
            itemView.setOnClickListener { checkBox.isChecked = checkBox.isChecked.not() }
            checkBox.setOnCheckedChangeListener { _, b ->
                if (b)
                    sceneSelector?.onSceneSelect(data, this.checkBox)
                else
                    sceneSelector?.unSceneSelect(data)
            }
            
            if (currentScene != null) {
                if (currentScene.uId == data.uId)
                    checkBox.isChecked = true
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_scene_sche_scene, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sceneData = currentList[position]
        holder.bindView(sceneData)
        holder.setListener(sceneData)
    }
    
    class DiffCallback : DiffUtil.ItemCallback<Scene>() {
        override fun areItemsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return false
        }
        
        override fun areContentsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return false
        }
        
    }
}