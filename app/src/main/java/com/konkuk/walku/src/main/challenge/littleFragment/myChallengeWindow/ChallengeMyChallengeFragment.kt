package com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentChallengeMychallengeBinding
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


class ChallengeMyChallengeFragment : BaseFragment<FragmentChallengeMychallengeBinding>(FragmentChallengeMychallengeBinding::bind, R.layout.fragment_challenge_mychallenge){
    val Customer = Firebase.database.getReference("Customer")
    val mike = Firebase.database.getReference("Customer/mike415415")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/New")
    val challengemy = Firebase.database.getReference("Customer/mike415415/Challenge/My")
    val challengesuccess = Firebase.database.getReference("Customer/mike415415/Challenge/Success")
    private var data =ArrayList<ChallengeMyData>()
    private var challengeviewmodel = ChallengeMyViewModel()

    var flagtime = true
    private var flagremove = true
    private var successremove = false
    private var flagstarttimesort = false
    private var flagremaintimesort = false
    private var flagachivementsort = false


    lateinit var adapter: ChallengeMyRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengeinit()

        val walk= Customer.addValueEventListener(object:ValueEventListener{
            //???????????? ????????? ??????????????? ????????? ?????? ???????????? ????????????
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("?????????????","??????")
                val accountchallengeWalkcount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkCountChallenge")
                val accountchallengeDistancecount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkDistanceChallenge")
                challengeviewmodel.datalist.value=data

                if(flagremove==true){
                    if(data.size>=0) {
                        for (i in 0..data.size - 1) {
                            Log.i("timer","??????: "+data[i].timer.toString())
                            data[i].timer.cancel()
                            data[i].timer.purge()
                        }
                    }
                    data.clear()
                    for (j in accountchallengeWalkcount.children.iterator()) {
                        if (j.child("context").value.toString() != "null") {
                            val newvalue = ChallengeMyData(j.key!!.toInt(),
                                j.child("challengetype").value.toString(),
                                j.child("day").value.toString(),
                                j.child("context").value.toString(),
                                j.child("achiveamount").value.toString(),
                                j.child("achivement").value.toString().toInt(),
                                j.child("starttime").value.toString(),
                                j.child("remaintime").value.toString(),
                                Timer(),
                                "??????")
                            data.add(newvalue)
                            data[data.size-1].timer = timerthread(data.size-1)
                        }
                    }
                    for (j in accountchallengeDistancecount.children.iterator()) {
                        if (j.child("context").value.toString() != "null") {
                            try {
                                val newvalue = ChallengeMyData(
                                    j.key!!.toInt(),
                                    j.child("challengetype").value.toString(),
                                    j.child("day").value.toString(),
                                    j.child("context").value.toString(),
                                    j.child("achiveamount").value.toString(),
                                    j.child("achivement").value.toString().toInt(),
                                    j.child("starttime").value.toString(),
                                    j.child("remaintime").value.toString(),
                                    Timer(),
                                    "??????")
                                data.add(newvalue)
                                data[data.size-1].timer = timerthread(data.size-1)
                            }catch(e:Exception){}
                        }
                    }
                        flagremove = false
                        binding.counttext.text = "??? ?????????: " + data.size.toString()
                        recyclernone()
                }
                else if (flagstarttimesort){
                    sortingstarttime()
                    flagstarttimesort = false
                }
                else if (flagremaintimesort){
                    sortingremaintime()
                    flagremaintimesort = false
                }
                else if (flagachivementsort){
                    sortingremainprogress()
                    flagachivementsort = false
                }

                val walkinfo = snapshot.child("mike415415").child("analysis").child("stepData")
                val distanceinfo = snapshot.child("mike415415").child("analysis").child("distance")

                for(j in 0..data.size-1){
                    data[j].achivement=0
                }

                for (i in walkinfo.children.iterator()){
                    val step = i.child("stepCount").value
                    val distance = i.child("distance").value
                    for(j in 0..data.size-1){

                        if(data[j].challengetype=="WalkCountChallenge")
                        data[j].achivement+=step.toString().toInt()

                        if(data[j].challengetype=="WalkDistanceChallenge")
                        data[j].achivement+=distance.toString().toFloat().toInt()

                    }
                }
                //challengeviewmodel.datalist.value=data
            }
            //????????? ????????? ??????????????? ????????? ????????? ????????? ???????????? ?????? ??????????????? ????????? ???
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }

    //resume??? ????????? ??????
    override fun onResume() {
        super.onResume()
        flagremove = true

        val walk= Customer.addValueEventListener(object:ValueEventListener{
            //???????????? ????????? ??????????????? ????????? ?????? ???????????? ????????????
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("?????????????","??????")
                val accountchallengeWalkcount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkCountChallenge")
                val accountchallengeDistancecount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkDistanceChallenge")
                challengeviewmodel.datalist.value=data

                if(flagremove==true){
                    if(data.size>=0) {
                        for (i in 0..data.size - 1) {
                            Log.i("timer","??????: "+data[i].timer.toString())
                            data[i].timer.cancel()
                            data[i].timer.purge()
                        }
                    }
                    data.clear()
                    for (j in accountchallengeWalkcount.children.iterator()) {
                        if (j.child("context").value.toString() != "null") {
                            val newvalue = ChallengeMyData(j.key!!.toInt(),
                                j.child("challengetype").value.toString(),
                                j.child("day").value.toString(),
                                j.child("context").value.toString(),
                                j.child("achiveamount").value.toString(),
                                j.child("achivement").value.toString().toInt(),
                                j.child("starttime").value.toString(),
                                j.child("remaintime").value.toString(),
                                Timer(),
                                "??????")
                            data.add(newvalue)
                            data[data.size-1].timer = timerthread(data.size-1)
                        }
                    }
                    for (j in accountchallengeDistancecount.children.iterator()) {
                        if (j.child("context").value.toString() != "null") {
                            try {
                                val newvalue = ChallengeMyData(
                                    j.key!!.toInt(),
                                    j.child("challengetype").value.toString(),
                                    j.child("day").value.toString(),
                                    j.child("context").value.toString(),
                                    j.child("achiveamount").value.toString(),
                                    j.child("achivement").value.toString().toInt(),
                                    j.child("starttime").value.toString(),
                                    j.child("remaintime").value.toString(),
                                    Timer(),
                                    "??????")
                                data.add(newvalue)
                                data[data.size-1].timer = timerthread(data.size-1)
                            }catch(e:Exception){}
                        }
                    }
                    flagremove = false
                    binding.counttext.text = "??? ?????????: " + data.size.toString()
                    recyclernone()
                }
                else if (flagstarttimesort){
                    sortingstarttime()
                    flagstarttimesort = false
                }
                else if (flagremaintimesort){
                    sortingremaintime()
                    flagremaintimesort = false
                }
                else if (flagachivementsort){
                    sortingremainprogress()
                    flagachivementsort = false
                }

                val walkinfo = snapshot.child("mike415415").child("analysis").child("stepData")
                val distanceinfo = snapshot.child("mike415415").child("analysis").child("distance")

                for(j in 0..data.size-1){
                    data[j].achivement=0
                }

                for (i in walkinfo.children.iterator()){
                    val step = i.child("stepCount").value
                    val distance = i.child("distance").value
                    for(j in 0..data.size-1){

                        if(data[j].challengetype=="WalkCountChallenge")
                            data[j].achivement+=step.toString().toInt()

                        if(data[j].challengetype=="WalkDistanceChallenge")
                            data[j].achivement+=distance.toString().toFloat().toInt()

                    }
                }
                //challengeviewmodel.datalist.value=data
            }
            //????????? ????????? ??????????????? ????????? ????????? ????????? ???????????? ?????? ??????????????? ????????? ???
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
        when (binding.spinner.selectedItemPosition) {
            0-> flagremove = true
            1-> { flagstarttimesort = true }
            2->{ flagremaintimesort=true }
            3->{ flagachivementsort=true }
            else->{}
         }
        if (data.size != 0)
            binding.counttext.text = "??? ?????????: " + data.size.toString()

    }





    //???????????? ?????? ??????????????? ???????????? ??????????????? ?????? ????????? ???????????? ????????? ????????? ??????.
    override fun onDestroyView() {
        super.onDestroyView()
        for (i in 0..data.size-1){
            data[i].timer.cancel()
            data[i].timer.purge()
        }
        data.clear()
    }


    fun challengeinit() {
        //?????????????????? ????????? ?????????
        binding.recyclerview.layoutManager  = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        val decoration = ChallengeFragmentView.RecyclerDecoadpater()
        binding.recyclerview.addItemDecoration(decoration)
        var animation = binding.recyclerview.itemAnimator
        (animation as SimpleItemAnimator).supportsChangeAnimations = false

        //?????? ????????? ????????? ????????? ???????????? ??????
        challengeviewmodel.datalist.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        //?????????????????? ????????? ?????????
        adapter = ChallengeMyRecyclerAdapter()
        adapter.onclickbuttonlistener =   object: ChallengeMyRecyclerAdapter.OnclickButtonListener {
            //????????? ????????? ?????? ???????????? "??? ?????????"??? ?????? ???????????? ????????????.
            override fun clickbuttonlistener(pos: Int, str: String) {
                if(str =="??????") {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("??????????")
                        .setMessage("????????? ??? ???????????? ?????? ????????????????")
                        .setPositiveButton("??????") { dialog, which ->
                            showLoadingDialog(requireContext())
                            flagremove = true
                            data[pos].timer.cancel()
                            data[pos].timer.purge()
                            val temp = data[pos]

                            challengemy.child(temp!!.challengetype).child(temp?.num.toString()).removeValue()
                            challengenew.child(temp!!.challengetype).child(temp?.num.toString()).child("context").setValue(temp.context)
                            challengenew.child(temp!!.challengetype).child(temp?.num.toString()).child("achiveamount").setValue(temp.achivementamount)
                            challengenew.child(temp!!.challengetype).child(temp?.num.toString()).child("day").setValue(temp.day)
                            recyclernone()
                            dismissLoadingDialog()
                        }
                        .setNegativeButton("??????") { _, _ -> }
                        .show()
                }
                else if(str =="????????????") {
                    flagremove = true
                    val temp = data[pos]
                    challengemy.child(temp.challengetype).child(temp.num.toString()).removeValue()
                    challengenew.child(data[pos].challengetype).child(data[pos].num.toString()).child("context").setValue(data[pos].context)
                    challengenew.child(data[pos].challengetype).child(data[pos].num.toString()).child("day").setValue(data[pos].day)
                    recyclernone()
                    adapter.notifyItemRemoved(pos)
                }
                else if(str =="??????") {
                    Toast.makeText(context,"????????? ???????????? ????????? ???????????????!",Toast.LENGTH_SHORT).show()
                    flagremove = true
                    successremove =true
                    val temp = data[pos]
                    challengemy.child(temp.challengetype).child(temp.num.toString()).removeValue()
                    challengesuccess.child(temp.challengetype).child(temp.num.toString()).child("context").setValue(temp.context)
                    challengesuccess.child(temp.challengetype).child(temp.num.toString()).child("challengetype").setValue(temp.challengetype)
                    challengesuccess.child(temp.challengetype).child(temp.num.toString()).child("successcount").setValue(1)
                    recyclernone()
                }
            }
        }

        binding.recyclerview.adapter = adapter

        //????????? ?????? ????????? ?????????
        binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    //????????? ??? ????????? ?????? ????????? datachange???????????? ???????????????
                    0->  flagremove = true
                    //?????? ?????? ??????
                    1-> flagstarttimesort = true
                    //?????? ?????? ???
                    2->  flagremaintimesort = true
                    //?????? ?????? ???
                    3->  flagachivementsort = true
                    //????????? ???
                    else->{}
                    //??? ???????????? ????????? ?????? ??????
                }
                Toast.makeText(context,"?????? ??????!",Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun recyclernone(){
        if(data.size==0){
            binding.nothing.visibility = View.VISIBLE
            binding.recyclerview.visibility = View.GONE
        }
        else{
            binding.nothing.visibility = View.GONE
            binding.recyclerview.visibility = View.VISIBLE
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun timerthread(position:Int):Timer{
        val splitstarttime = data[position].starttime.split(" ")
        val startdate = splitstarttime[0].toInt()
        val starthour = splitstarttime[1].toInt()
        val startminute = splitstarttime[2].toInt()
        val startsecond = splitstarttime[3].toInt()
        //??? ??????=?????? * ??? * ??? * ???
        var position = position
        var timer= timer(period=1000){
            run{
                //?????? ?????? ??????
                val day = Calendar.getInstance()
                var date = day.get(Calendar.DATE)
                var hour = day.get(Calendar.HOUR_OF_DAY)
                var minute = day.get(Calendar.MINUTE)
                var second =day.get(Calendar.SECOND)
                var difsecond = 60 - (1+second-startsecond)
                if(second<startsecond) {
                    difsecond = 60 - (61 + second - startsecond)
                    minute-=1
                }
                var difminute = 60 - (1+minute-startminute)
                if(minute<startminute) {
                    difminute = 60 - (61 + minute - startminute)
                    hour -=1
                }
                var difhour =  23 - (hour - starthour)
                if(hour<starthour) {
                    difhour = 23 - (24 + hour - starthour)
                    date-=1
                }
                try {
                    var difday = data[position].day.toInt() - (date - startdate) - 1
                    flagtime=true
                    data[position].remaintime= "$difday $difhour $difminute $difsecond"
                    challengemy.child(data[position].challengetype).child(data[position].num.toString()).child("remaintime").setValue("$difday $difhour $difminute $difsecond")

                    if (difday<=0 && difhour<=0 && difminute<=0 && difsecond <=0) {
                        data[position].remaintime= "0 0 0 0"
                        data[position].buttontext = "????????????"
                        data[position].timer.cancel()
                        data[position].timer.purge()
                    }

                    if (data[position].achivement > data[position].achivementamount.toInt()){
                        data[position].achivement =  data[position].achivementamount.toInt()
                        data[position].buttontext = "??????"
                        data[position].timer.cancel()
                        data[position].timer.purge()
                    }
                }
                catch(e:Exception) {
                    Log.e("timer","$e")
                }
            }
        }
        return timer
    }




    // ??? ??????????????? sorting????????????????????????
    // ????????? ?????? ??????????????? ?????? ???????????? ?????? ???????????? ??? ????????? ?????? ?????? ?????? ??????????????? ??????????????? ?????????
    // ???????????? ?????????????????? ????????????????????????...
    // ?????? ?????? ????????? ???????????? ???????????? ??????????????? ?????? ?????? ?????? ????????? ???????????????.

    fun sortingstarttime(){
        for(i in data.size-1 downTo 0){
            var maxday = -1
            var maxtime =-1
            var maxminute = -1
            var maxsecond= -1
            var maxindex = -1
            for(j in 0..i) {
                val starttimesplit = data[j].starttime.split(" ")
                val day = starttimesplit[0].toInt()
                val time = starttimesplit[1].toInt()
                val minute = starttimesplit[2].toInt()
                val second = starttimesplit[3].toInt()

                if (day>maxday){                    //?????? ??????, ????????? ?????? = ??? ?????? ????????? ????????????
                    maxday  = day
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j                    //?????? ????????? ????????? ???????????? ?????? ????????? ?????? ??????
                    Log.i("sort","1")
                }
                else if (time >maxtime && day==maxday){            //??? ???????????? ????????? ?????? ?????? ????????????
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (minute >maxminute && day==maxday && time == maxtime){        //??? ??????
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (second > maxsecond && day==maxday && time == maxtime && minute == maxminute){       //??? ??????
                    maxsecond = second
                    maxindex = j
                }
            }
            if(maxindex != -1) {
                var temp = data[i]
                data[i] = data[maxindex]            //????????? ?????? ????????? ???????????? ??? ?????? ????????????
                data[maxindex] = temp
            }
        }
    }

    fun sortingremaintime(){
        for(i in data.size-1 downTo 0){
            var maxday = -1
            var maxtime =-1
            var maxminute = -1
            var maxsecond= -1
            var maxindex = -1
            for(j in 0..i) {
                val starttimesplit = data[j].remaintime.split(" ")
                val day = starttimesplit[0].toInt()
                val time = starttimesplit[1].toInt()
                val minute = starttimesplit[2].toInt()
                val second = starttimesplit[3].toInt()

                if (day>maxday){                    //?????? ??????, ????????? ?????? = ??? ?????? ????????? ????????????
                    maxday  = day
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j                    //?????? ????????? ????????? ???????????? ?????? ????????? ?????? ??????
                    Log.i("sort","1")
                }
                else if (time >maxtime && day==maxday){            //??? ???????????? ????????? ?????? ?????? ????????????
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (minute >maxminute && day==maxday && time == maxtime){        //??? ??????
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (second > maxsecond && day==maxday && time == maxtime && minute == maxminute){       //??? ??????
                    maxsecond = second
                    maxindex = j
                }
            }
            if(maxindex != -1) {
                var temp = data[i]
                data[i] = data[maxindex]            //????????? ?????? ????????? ???????????? ??? ?????? ????????????
                data[maxindex] = temp
            }
        }
    }

    fun sortingremainprogress(){
        for(i in data.size-1 downTo 0){
            var maxachivement = -1
            var maxindex = -1
            for(j in 0..i) {
                val achivement =  data[j].achivement
                val entireachivement = data[j].achivementamount.toInt()
                val rateachivement  =  achivement / entireachivement
                if (entireachivement>=maxachivement) {                    //?????? ??????, ????????? ?????? = ??? ?????? ????????? ????????????
                    maxachivement = entireachivement
                    maxindex = j                    //?????? ????????? ????????? ???????????? ?????? ????????? ?????? ??????
                }
            }
            if(maxindex != -1) {
                var temp = data[i]
                data[i] = data[maxindex]            //????????? ?????? ????????? ???????????? ??? ?????? ????????????
                data[maxindex] = temp
            }
        }
    }


}