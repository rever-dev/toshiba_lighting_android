package com.sttptech.toshiba_lighting.Adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sttptech.toshiba_lighting.R
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import java.util.ArrayList

open class BannerAdapter(mData: List<IMGBean?>?) : BannerImageAdapter<BannerAdapter.IMGBean?>(mData) {

    override fun onBindView(holder: BannerImageHolder?, data: IMGBean?, position: Int, size: Int) {
        holder!!.imageView.adjustViewBounds = true
        holder!!.imageView.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(holder.itemView).load(data!!.imageRes).into(holder.imageView)
    }

    data class IMGBean(var imageRes: Int) {

        companion object {
            val data: List<IMGBean>
                get() {
                    val list: MutableList<IMGBean> = ArrayList()
                    list.add(IMGBean(R.drawable.banner01))
                    list.add(IMGBean(R.drawable.banner02))
                    list.add(IMGBean(R.drawable.banner03))
                    list.add(IMGBean(R.drawable.banner04))
                    list.add(IMGBean(R.drawable.banner05))
                    list.add(IMGBean(R.drawable.banner06))
                    list.add(IMGBean(R.drawable.banner07))
                    list.add(IMGBean(R.drawable.banner08))
                    list.add(IMGBean(R.drawable.banner09))
                    list.add(IMGBean(R.drawable.banner10))
                    return list
                }
        }
    }
}