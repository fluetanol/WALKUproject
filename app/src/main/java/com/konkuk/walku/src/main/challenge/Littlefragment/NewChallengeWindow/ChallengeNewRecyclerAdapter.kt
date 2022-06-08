package com.konkuk.walku.src.main.challenge.Littlefragment.NewChallengeWindow

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.walku.R
import com.konkuk.walku.databinding.FragmentChallengeNewrecyclerBinding
import com.konkuk.walku.src.main.challenge.ChallengeData


class ChallengeNewRecyclerAdapter(var data:List<ChallengeData>): RecyclerView.Adapter<ChallengeNewRecyclerAdapter.ViewHolder>() {
    interface OnclickButtonListener {
        fun clickbuttonlistener(pos:Int)
    }
    lateinit var onclickbuttonlistener: OnclickButtonListener


    inner class ViewHolder(bind: FragmentChallengeNewrecyclerBinding):RecyclerView.ViewHolder(bind.root){
        var binding = bind
        init {
               binding.button.setOnClickListener {
                   onclickbuttonlistener.clickbuttonlistener(adapterPosition)
               }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int,): ViewHolder {
            val challengeview = FragmentChallengeNewrecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(challengeview)
    }

    fun itemCount():Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.daytextview.text = data[position].day
        holder.binding.textview.text = data[position].context
        if(data[position].challengetype == "WalkDistanceChallenge")
            holder.binding.iconview.setBackgroundResource(R.drawable.ic_baseline_run_circle_24)
        else
            holder.binding.iconview.setBackgroundResource(R.drawable.ic_footprint)
    }

    override fun getItemCount(): Int =data.size






}