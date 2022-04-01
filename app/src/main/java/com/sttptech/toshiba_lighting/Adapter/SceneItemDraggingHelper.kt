package com.sttptech.toshiba_lighting.Adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import java.util.*

class SceneItemDraggingHelper : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN
            or ItemTouchHelper.START or ItemTouchHelper.END, 0
) {
    private val fromPos = IntArray(1)
    private val toPos = IntArray(1)
    
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        
        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition
        
        val adapter = recyclerView.adapter ?: return false
        if (adapter is SceneListAdapter) {
            val list = adapter.currentList.toList()
            
            Logger.i("before list: $list")
            
            Collections.swap(list, fromPos, toPos)
            adapter.submitList(list)
            
            Logger.i("after list: $list")
        } else return false
        
        return true
    }
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val adapter = recyclerView.adapter
        if (adapter is SceneListAdapter) {
            adapter.moveFinish()
        } else return
        
    }
    
    
    
    
}