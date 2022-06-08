package com.konkuk.walku.src.main.challenge.Littlefragment.MyChallengeWindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.walku.databinding.FragmentChallengeMyrecyclerBinding
import com.konkuk.walku.src.main.challenge.ChallengeData

class ChallengeMyRecyclerAdapter : ListAdapter<ChallengeData, ChallengeMyRecyclerAdapter.ViewHolder>(diffUtil) {

    interface OnclickButtonListener {
        fun clickbuttonlistener(pos: Int, text: String)
    }
    interface OnTimeChangeListener{
        fun Timechangelistener(pos: Int, timetext: String, achivetext:String)
    }
    lateinit var onclickbuttonlistener: OnclickButtonListener
    lateinit var ontimechangelistener: OnTimeChangeListener

    companion object {
        lateinit var challengeview:FragmentChallengeMyrecyclerBinding

        val diffUtil = object : DiffUtil.ItemCallback<ChallengeData>() {
            override fun areItemsTheSame(oldItem: ChallengeData, newItem: ChallengeData): Boolean {
                return oldItem.remaintime == newItem.remaintime
            }
            override fun areContentsTheSame(oldItem: ChallengeData, newItem: ChallengeData): Boolean {
                return oldItem!=newItem
            }
        }
    }
    inner class ViewHolder(binding:FragmentChallengeMyrecyclerBinding): RecyclerView.ViewHolder(binding.root){
        var binding = binding
        fun bind(challengedata:ChallengeData){
            binding.textview.text =challengedata.context
            binding.daytextview.text = challengedata.day
            val remaintimesplit = challengedata.remaintime.split(" ")
            binding.timertextview.text = remaintimesplit[0]+"일 "+ remaintimesplit[1]+ "시 " +remaintimesplit[2]+ "분 " + remaintimesplit[3] +"초"
        }
        fun nowposition(challengedata:ChallengeData)=challengedata
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
        challengeview = FragmentChallengeMyrecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(challengeview)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

    }
}
