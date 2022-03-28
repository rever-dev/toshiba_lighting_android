package com.sttptech.toshiba_lighting.Adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.R

class SceneListAdapter : ListAdapter<Scene, SceneListAdapter.ViewHolder>(DiffCallback()) {
    
    var sceneClickCallback: SceneClickCallback? = null
    
    interface SceneClickCallback {
        fun onSceneClick(seq: Int, scene: Scene)
    }
    
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        
        private val caedView = v.findViewById<CardView>(R.id.sceneItem_cardView)
        private val image = v.findViewById<ImageView>(R.id.sceneItem_imageView)
        private val tvName = v.findViewById<TextView>(R.id.sceneItem_tvName)
        
        fun bindView(data: Scene?) {
            if (data == null) return
            
            if (data.image != null) {
                val bitmap = BitmapFactory.decodeByteArray(data.image, 0, data.image!!.size)
                image.setImageBitmap(bitmap)
            }
            tvName.text = data.name
            itemView.setOnClickListener {
                if (sceneClickCallback == null) return@setOnClickListener
                
                sceneClickCallback!!.onSceneClick(data.seq!!, data)
            }
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_scene_list, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(currentList[position])
    }
    
    
    private class DiffCallback : DiffUtil.ItemCallback<Scene>() {
        override fun areItemsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return oldItem.uId == newItem.uId
        }
        
        override fun areContentsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return oldItem == newItem
        }
    }
}

