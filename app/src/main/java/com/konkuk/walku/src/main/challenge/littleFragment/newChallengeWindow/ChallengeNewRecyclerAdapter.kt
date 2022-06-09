package com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.walku.R
import com.konkuk.walku.databinding.FragmentChallengeMyrecyclerBinding
import com.konkuk.walku.databinding.FragmentChallengeNewrecyclerBinding
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyData
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyRecyclerAdapter
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyRecyclerAdapter.Companion.diffUtil


class ChallengeNewRecyclerAdapter():  ListAdapter<ChallengeNewData, ChallengeNewRecyclerAdapter.ViewHolder>(ChallengeNewRecyclerAdapter.diffUtil) {
    interface OnclickButtonListener {
        fun clickbuttonlistener(pos:Int)
    }
    lateinit var onclickbuttonlistener: OnclickButtonListener

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChallengeNewData>() {
            override fun areItemsTheSame(oldItem: ChallengeNewData, newItem: ChallengeNewData): Boolean {
                return oldItem.context == oldItem.context
            }
            override fun areContentsTheSame(oldItem: ChallengeNewData, newItem: ChallengeNewData): Boolean {
                return oldItem==newItem
            }
        }
    }

    inner class ViewHolder(bind: FragmentChallengeNewrecyclerBinding):RecyclerView.ViewHolder(bind.root){
        var binding = bind
        fun bind(challengedata: ChallengeNewData){
            binding.daytextview.text = challengedata.day
            binding.textview.text = challengedata.context
            if(challengedata.challengetype == "WalkDistanceChallenge")
                binding.iconview.setBackgroundResource(R.drawable.ic_baseline_run_circle_24)
            else
                binding.iconview.setBackgroundResource(R.drawable.ic_footprint)
        }

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}