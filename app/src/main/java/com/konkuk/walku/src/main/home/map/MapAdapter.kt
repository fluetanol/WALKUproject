package com.konkuk.walku.src.main.home.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.walku.databinding.FragmentMapItemBinding
import com.konkuk.walku.src.main.home.map.model.MapViewPagerItem

class MapAdapter(itemList: ArrayList<MapViewPagerItem>): RecyclerView.Adapter<MapAdapter.Holder>() {

    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    var item = itemList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = FragmentMapItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = item[position%3]
        holder.setData(data)
    }

    inner class Holder(val binding: FragmentMapItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.fragmentMapImageView.setOnClickListener {
                itemClickListener?.OnItemClick(adapterPosition)
            }
        }
        fun setData(data: MapViewPagerItem) {
            binding.fragmentMapImageView.setImageResource(data.image)
            binding.fragmentMapTitleTextView.text = data.title
            binding.fragmentMapFirstHashTag.text = data.hashtagFirst
            binding.fragmentMapSecondHashTag.text = data.hashtagSecond
        }
    }

}