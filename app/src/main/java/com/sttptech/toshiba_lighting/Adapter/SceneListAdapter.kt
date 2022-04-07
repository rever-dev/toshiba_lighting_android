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
import dev.weiqi.resof.drawableOf

class SceneListAdapter : ListAdapter<Scene, SceneListAdapter.ViewHolder>(DiffCallback()) {
    
    var sceneClickCallback: SceneClickCallback? = null
    var sceneMoveCallback: SceneMoveCallback? = null
    
    interface SceneClickCallback {
        fun onSceneClick(seq: Int, scene: Scene)
    }
    
    interface SceneMoveCallback {
        fun onSceneMoveFinish()
    }
    
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        
        private val cardView = v.findViewById<CardView>(R.id.sceneItem_cardView)
        private val image = v.findViewById<ImageView>(R.id.sceneItem_imageView)
        private val tvName = v.findViewById<TextView>(R.id.sceneItem_tvName)
        
        fun bindView(data: Scene?) {
            if (data == null) return
            
            if (data.image != null) {
                val bitmap = BitmapFactory.decodeByteArray(data.image, 0, data.image!!.size)
                image.setImageBitmap(bitmap)
            } else
                image.setImageDrawable(drawableOf(R.drawable.toshiba))
            
            tvName.text = data.name
            
            itemView.setOnClickListener { sceneClickCallback?.onSceneClick(data.seq!!, data) }
        }
        
        fun unbind() {
            image.setImageBitmap(null)
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
    
    override fun onViewRecycled(holder: ViewHolder) {
        holder.unbind()
    }
    
    fun moveFinish() {
        sceneMoveCallback?.onSceneMoveFinish()
    }
    
    
    private class DiffCallback : DiffUtil.ItemCallback<Scene>() {
        override fun areItemsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return oldItem.uId == newItem.uId
        }
        
        override fun areContentsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return oldItem == newItem &&
                    oldItem.name == newItem.name &&
                    oldItem.order == newItem.order &&
                    oldItem.image.contentEquals(newItem.image) &&
                    oldItem.devUuids == newItem.devUuids
        }
    }
}

