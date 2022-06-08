package com.konkuk.walku.src.main.challenge.Littlefragment.MyChallengeWindow

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.system.Os.bind
import android.text.TextUtils.split
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
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
import com.konkuk.walku.src.main.challenge.ChallengeData
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView
import okhttp3.Challenge
import okhttp3.internal.notify
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


class ChallengeMyChallengeFragment : BaseFragment<FragmentChallengeMychallengeBinding>(FragmentChallengeMychallengeBinding::bind, R.layout.fragment_challenge_mychallenge){
    val database = Firebase.database
    val Customer = Firebase.database.getReference("Customer")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/New")
    val challengemy = Firebase.database.getReference("Customer/mike415415/Challenge/My")
    val challengesuccess = Firebase.database.getReference("Customer/mike415415/Challenge/Success")
    var data =ArrayList<ChallengeData>()
    var flagremove = true
    var successremove = false
    var flagtime = true
    var challengeviewmodel = ChallengeMyViewModel()

    lateinit var adapter: ChallengeMyRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengeinit()
    }

    override fun onResume() {
        super.onResume()
        binding.loadingbar.visibility=View.VISIBLE
        if (data.size != 0)
            binding.counttext.text = "내 챌린지: " + data.size.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (i in 0..data.size-1){
            data[i].timer.cancel()
            data[i].timer.purge()
        }
        data.clear()

    }


    //리사이클러뷰 초기화 + 버튼리스너
    fun challengeinit() {
        binding.recyclerview.layoutManager  = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        val decoration = ChallengeFragmentView.RecyclerDecoadpater()
        binding.recyclerview.addItemDecoration(decoration)
        var animation = binding.recyclerview.itemAnimator
        (animation as SimpleItemAnimator).supportsChangeAnimations = false

        challengeviewmodel.datalist.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
        adapter = ChallengeMyRecyclerAdapter()
        adapter.onclickbuttonlistener =   object: ChallengeMyRecyclerAdapter.OnclickButtonListener {
            //버튼을 누를시 해당 리스트가 "새 챌린지"로 가고 리스트가 삭제된다.
            override fun clickbuttonlistener(pos: Int, str: String) {
                if(str =="삭제") {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("정말로?")
                        .setMessage("정말로 이 챌린지를 그만 두시겠나요?")
                        .setPositiveButton("확인") { dialog, which ->
                            flagremove = true
                            data[pos].timer.cancel()
                            data[pos].timer.purge()
                            val temp = data[pos]
                            challengemy.child(temp!!.challengetype).child(temp?.num.toString()).removeValue()
                            challengenew.child(temp!!.challengetype).child(temp?.num.toString()).child("context").setValue(temp.context)
                            challengenew.child(temp!!.challengetype).child(temp?.num.toString()).child("day").setValue(temp.day)
                            recyclernone()
                        }
                        .setNegativeButton("취소") { _, _ -> }
                        .show()
                }
                else if(str =="시간종료") {
                    flagremove = true
                    val temp = data[pos]
                    challengemy.child(temp.challengetype).child(temp.num.toString()).removeValue()
                    challengenew.child(data[pos].challengetype).child(data[pos].num.toString()).child("context").setValue(data[pos].context)
                    challengenew.child(data[pos].challengetype).child(data[pos].num.toString()).child("day").setValue(data[pos].day)
                    recyclernone()
                    adapter.notifyItemRemoved(pos)
                }
                else if(str =="확인") {
                    Toast.makeText(context,"성공한 챌린지로 기록을 옮겼습니다!",Toast.LENGTH_SHORT).show()
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
        adapter.ontimechangelistener = object:ChallengeMyRecyclerAdapter.OnTimeChangeListener{
            override fun Timechangelistener(pos: Int, timetext: String, achivetext: String) {
                try {
                    var splitremaintime = data[pos].remaintime.split(" ")
                    if (splitremaintime[0].toInt() <= 2 && splitremaintime[1].toInt() <= 23 && splitremaintime[2].toInt() <= 54 && splitremaintime[3].toInt() <= 55) {

                    }
                }catch(e:Exception){}

            }
        }
        binding.recyclerview.adapter = adapter
    }

    //데이터 가져오는 리스너, 프레그먼트가 실행될떄, 재실행(resume)될때, 데이터베이스에 변동이 있을때 모두 작동
    val walk= Customer.addValueEventListener(object:ValueEventListener{
        //데이터가 바뀔떄 호출하거나 처음에 자동 호출되는 콜백함수
        @SuppressLint("SetTextI18n")
        override fun onDataChange(snapshot: DataSnapshot) {
            val accountchallengeWalkcount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkCountChallenge")
            val accountchallengeDistancecount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkDistanceChallenge")
            challengeviewmodel.datalist.value=data
            if(flagremove==true){
            if(data.size>0) {
                for (i in 0..data.size - 1) {
                    Log.i("timer","삭제: "+data[i].timer.toString())
                    data[i].timer.cancel()
                    data[i].timer.purge()
                }
            }
            data.clear()
            if(challengeviewmodel.datalist.value!!.size>0) {
                for (i in 0..   challengeviewmodel.datalist.value!!.size - 1) {
                    challengeviewmodel.datalist.value!![i].timer.cancel()
                    challengeviewmodel.datalist.value!![i].timer.purge()
                }
            }
                for (j in accountchallengeWalkcount.children.iterator()) {
                    if (j.child("context").value.toString() != "null") {
                            val newvalue = ChallengeData(j.key!!.toInt(),
                                j.child("challengetype").value.toString(),
                                j.child("day").value.toString(),
                                j.child("context").value.toString(),
                                j.child("achivement").value.toString().toInt(),
                                j.child("starttime").value.toString(),
                            j.child("remaintime").value.toString(),
                                Timer())
                            data.add(newvalue)
                        data[data.size-1].timer = timerthread(data.size-1)
                    }
                }
                for (j in accountchallengeDistancecount.children.iterator()) {
                    if (j.child("context").value.toString() != "null") {
                        try {
                            val newvalue = ChallengeData(
                                j.key!!.toInt(),
                                j.child("challengetype").value.toString(),
                                j.child("day").value.toString(),
                                j.child("context").value.toString(),
                                j.child("achivement").value.toString().toInt(),
                                j.child("starttime").value.toString(),
                                j.child("remaintime").value.toString(),
                                Timer())
                            data.add(newvalue)
                            data[data.size-1].timer = timerthread(data.size-1)
                        }catch(e:Exception){}
                    }
                }
                flagremove=false
                try {
                    binding.counttext.text = "내 챌린지: "+data.size.toString()
                    recyclernone()
                    binding.loadingbar.visibility=View.GONE
                }catch(e:Exception){
                    Log.e("timer","!!?")
                }
            }
        }
        //모종의 이유로 데베쪽에서 문제가 생겨서 데이터 가져오는 것에 실패했을때 처리할 일
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show()
        }
    })

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
        //총 초수=일수 * 시 * 분 * 초
        var position = position
        var timer= timer(period=1000){
            run{
                //남은 시간 계산
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
                }
                catch(e:Exception) {
                    Log.e("timer","$e")
                }
            }
        }
        Log.i("timer?","생성 완료 -> "+timer.toString())
        return timer
    }

}