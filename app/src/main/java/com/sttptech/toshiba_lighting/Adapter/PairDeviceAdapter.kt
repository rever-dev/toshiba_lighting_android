package com.sttptech.toshiba_lighting.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.R

class PairDeviceAdapter(var data: MutableList<Device>) :
    RecyclerView.Adapter<PairDeviceAdapter.ViewHolder>() {

    interface ButtonCallback {
        fun onBtnClick(dev: Device)
    }

    interface CheckBoxCallback {
        fun onCheck(isChecked: Boolean, dev: Device, position: Int)
    }

    var btnCallback: ButtonCallback? = null
    var checkBoxCallback: CheckBoxCallback? = null

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cardView: CardView = v.findViewById(R.id.itemPairDev_cardView)
        val tvName: TextView = v.findViewById(R.id.itemPairDev_tvName)
        val tvModel: TextView = v.findViewById(R.id.itemPairDev_tvModel)
        val tvGroup: TextView = v.findViewById(R.id.itemPairDev_tvGroup)
        val btnIdan: TextView = v.findViewById(R.id.itemPairDev_btnIdentify)
        val checkBox: CheckBox = v.findViewById(R.id.itemPairDev_cb)
    }

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.itemview_pair_device, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = data[position].name
        // TODO: 2022/1/11 macId -> model name
        holder.tvModel.text = data[position].macId

        holder.checkBox.setOnCheckedChangeListener(null)
        if (data[position].group == null) {
            holder.tvGroup.text = holder.itemView.context.getString(R.string.pressToSelectGroup)
            holder.checkBox.isChecked = false
        } else {
            holder.tvGroup.text = data[position].group!!.groupName
            holder.checkBox.isChecked = true
        }

        holder.btnIdan.setOnClickListener { btnCallback?.onBtnClick(data[position]) }
        holder.cardView.setOnClickListener { holder.checkBox.isChecked = !holder.checkBox.isChecked }
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkBoxCallback?.onCheck(isChecked, data[position], position)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return data.size
    }

    fun addData(dev: Device) {
        notifyItemInserted(data.size)
    }
}