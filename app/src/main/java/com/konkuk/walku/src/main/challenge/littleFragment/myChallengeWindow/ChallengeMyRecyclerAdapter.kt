package com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow

import android.annotation.SuppressLint
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
        fun timechangelistener(pos: Int, timetext: String, achivetext:String)
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

    @SuppressLint("SetTextI18n")
    inner class ViewHolder(binding:FragmentChallengeMyrecyclerBinding): RecyclerView.ViewHolder(binding.root){
        var binding = binding

        fun bind(challengedata: ChallengeMyData){
            binding.textview.text =challengedata.context            //도전내용
            val starttimesplit = challengedata.starttime.split(" ")
            binding.startdaytextview.text = "시작시간: "+starttimesplit[0]+"일 "+starttimesplit[1]+"시 "+starttimesplit[2]+"분 "+starttimesplit[3]+"초"
            //도전 시작날짜
            binding.daytextview.text = challengedata.day            //도전 기간
            val remaintimesplit = challengedata.remaintime.split(" ")
            binding.timertextview.text = remaintimesplit[0]+"일 "+ remaintimesplit[1]+ "시 " +remaintimesplit[2]+ "분 " + remaintimesplit[3] +"초"
            //타이머 (남은시간)
            binding.achivetextview.text = challengedata.achivement.toString() +"/"+ challengedata.achivementamount
            //달성도 기록
            binding.progressbar.progress = challengedata.achivement
            binding.button.text = challengedata.buttontext
            //버튼 텍스트
        }

        init{
            binding.button.setOnClickListener {
                onclickbuttonlistener.clickbuttonlistener(adapterPosition,binding.button.text.toString())
            }

            /*
            binding.timertextview.addTextChangedListener{
                ontimechangelistener.timechangelistener(adapterPosition,binding.timertextview.text.toString(),binding.achivetextview.text.toString())
            }*/
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
