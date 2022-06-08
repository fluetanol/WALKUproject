package com.konkuk.walku.src.main.challenge.Littlefragment.SuccessChallengeWindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.walku.R
import com.konkuk.walku.databinding.FragmentChallengeNewrecyclerBinding
import com.konkuk.walku.databinding.FragmentChallengeSuccessrecyclerBinding
import com.konkuk.walku.src.main.challenge.ChallengeData


class ChallengeSuccessRecyclerAdapter(var data:ArrayList<ChallengeData>): RecyclerView.Adapter<ChallengeSuccessRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(bind: FragmentChallengeSuccessrecyclerBinding):RecyclerView.ViewHolder(bind.root){
        var binding = bind
        init {
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int,): ViewHolder {
            val challengeview = FragmentChallengeSuccessrecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(challengeview)
    }

    fun itemCount():Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textview.text = data[position].context
        if(data[position].challengetype=="WalkDistanceChallenge")
        holder.binding.iconview.setBackgroundResource(R.drawable.ic_baseline_run_circle_24)
        else
        holder.binding.iconview.setBackgroundResource(R.drawable.ic_footprint)
    }

    override fun getItemCount(): Int =data.size


}