package com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.walku.databinding.FragmentChallengeMyrecyclerBinding

class ChallengeMyRecyclerAdapter : ListAdapter<ChallengeMyData, ChallengeMyRecyclerAdapter.ViewHolder>(diffUtil) {

    interface OnclickButtonListener {
        fun clickbuttonlistener(pos: Int, text: String)
    }
    interface OnTimeChangeListener{
        fun Timechangelistener(pos: Int, timetext: String, achivetext:String)
    }
    lateinit var onclickbuttonlistener: OnclickButtonListener
    lateinit var ontimechangelistener: OnTimeChangeListener

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChallengeMyData>() {
            override fun areItemsTheSame(oldItem: ChallengeMyData, newItem: ChallengeMyData): Boolean {
                return oldItem.remaintime == newItem.remaintime
            }
            override fun areContentsTheSame(oldItem: ChallengeMyData, newItem: ChallengeMyData): Boolean {
                return oldItem!=newItem
            }
        }
    }

    inner class ViewHolder(binding:FragmentChallengeMyrecyclerBinding): RecyclerView.ViewHolder(binding.root){
        var binding = binding

        fun bind(challengedata: ChallengeMyData){
            binding.textview.text =challengedata.context
            binding.daytextview.text = challengedata.day
            val remaintimesplit = challengedata.remaintime.split(" ")
            binding.timertextview.text = remaintimesplit[0]+"일 "+ remaintimesplit[1]+ "시 " +remaintimesplit[2]+ "분 " + remaintimesplit[3] +"초"
        }

        init{
            binding.button.setOnClickListener {
                onclickbuttonlistener.clickbuttonlistener(adapterPosition,binding.button.text.toString())
            }

            binding.timertextview.addTextChangedListener{
                ontimechangelistener.Timechangelistener(adapterPosition,binding.timertextview.text.toString(),binding.achivetextview.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val challengeview = FragmentChallengeMyrecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(challengeview)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

    }
}
