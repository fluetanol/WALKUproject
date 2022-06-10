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
        showLoadingDialog(requireContext())
        challengeinit()
        val walk= Customer.addValueEventListener(object:ValueEventListener{
            //데이터가 바뀔떄 호출하거나 처음에 자동 호출되는 콜백함수
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val accountchallengeWalkcount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkCountChallenge")
                val accountchallengeDistancecount = snapshot.child("mike415415").child("Challenge").child("My").child("WalkDistanceChallenge")
                challengeviewmodel.datalist.value=data

                Log.i("test","test!")
                if(flagremove==true){
                    Log.i("sort","remove")
                    if(data.size>0) {
                        for (i in 0..data.size - 1) {
                            Log.i("timer","삭제: "+data[i].timer.toString())
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
                                "삭제")
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
                                    "삭제")
                                data.add(newvalue)
                                data[data.size-1].timer = timerthread(data.size-1)
                            }catch(e:Exception){}
                        }
                    }
                        flagremove = false
                        dismissLoadingDialog()
                        binding.counttext.text = "내 챌린지: " + data.size.toString()
                        recyclernone()
                }
                else if (flagstarttimesort){
                    sortingstarttime()
                    flagstarttimesort = false
                }
                else if (flagremaintimesort){
                    sortingremaintime()
                    flagremaintimesort = false
                    Log.i("toast","remian")
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
            //모종의 이유로 데베쪽에서 문제가 생겨서 데이터 가져오는 것에 실패했을때 처리할 일
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
        dismissLoadingDialog()
    }

    //resume시 초기화 작업
    override fun onResume() {
        super.onResume()
        showLoadingDialog(requireContext())
        if(data.size==0)
        flagremove = true
        when (binding.spinner.selectedItemPosition) {
            0-> flagremove = true
            1-> {
                flagstarttimesort = true
            }
            2->{
                flagremaintimesort=true
            }
            3->{
                flagachivementsort=true
            }
            else->{}
         }
        if (data.size != 0)
            binding.counttext.text = "내 챌린지: " + data.size.toString()
    }

    //안해주면 다른 프레그먼트 옮겨갈때 타이머들이 계속 뒤에서 작동해서 메모리 누수가 난다.
    override fun onDestroyView() {
        super.onDestroyView()
        for (i in 0..data.size-1){
            data[i].timer.cancel()
            data[i].timer.purge()
        }
        data.clear()
    }


    fun challengeinit() {
        //리사이클러뷰 디자인 초기화
        binding.recyclerview.layoutManager  = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        val decoration = ChallengeFragmentView.RecyclerDecoadpater()
        binding.recyclerview.addItemDecoration(decoration)
        var animation = binding.recyclerview.itemAnimator
        (animation as SimpleItemAnimator).supportsChangeAnimations = false

        //계속 추적할 데이터 리스트 뷰모델에 등록
        challengeviewmodel.datalist.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        //리사이클러뷰 어뎁터 초기화
        adapter = ChallengeMyRecyclerAdapter()
        adapter.onclickbuttonlistener =   object: ChallengeMyRecyclerAdapter.OnclickButtonListener {
            //버튼을 누를시 해당 리스트가 "새 챌린지"로 가고 리스트가 삭제된다.
            override fun clickbuttonlistener(pos: Int, str: String) {
                if(str =="삭제") {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("정말로?")
                        .setMessage("정말로 이 챌린지를 그만 두시겠나요?")
                        .setPositiveButton("확인") { dialog, which ->
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

        binding.recyclerview.adapter = adapter

        //정렬을 위한 스피너 리스너
        binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    //정렬은 이 플래그 값에 따라서 datachange함수에서 처리합니다
                    0->  flagremove = true
                    //그냥 일반 순서
                    1-> flagstarttimesort = true
                    //시작 시간 순
                    2->  flagremaintimesort = true
                    //남은 시간 순
                    3->  flagachivementsort = true
                    //달성도 순
                    else->{}
                    //그 외의것은 아무런 처리 안함
                }
                Toast.makeText(context,"정렬 완료!",Toast.LENGTH_SHORT).show()
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

                    if (difday<=0 && difhour<=0 && difminute<=0 && difsecond <=0) {
                        data[position].remaintime= "0 0 0 0"
                        data[position].buttontext = "시간종료"
                        data[position].timer.cancel()
                        data[position].timer.purge()
                    }

                    if (data[position].achivement > data[position].achivementamount.toInt()){
                        data[position].achivement =  data[position].achivementamount.toInt()
                        data[position].buttontext = "확인"
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




    // 이 아래부터는 sorting알고리즘들입니다
    // 그런데 저는 알고리즘에 너무 쥐약이니 시간 복잡도를 더 줄일수 있는 좋은 소팅 알고리즘을 생각하시는 분들은
    // 여기에서 수정해주시면 감사드리겠습니다...
    // 밑의 소팅 방법은 최대값을 찾아내어 순차적으로 뒤에 갖다 넣는 방식의 소팅입니다.

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

                if (day>maxday){                    //날짜 비교, 날짜가 크다 = 더 늦게 선택한 챌린지다
                    maxday  = day
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j                    //가장 최근에 신청한 챌린지가 담긴 인덱스 번호 저장
                    Log.i("sort","1")
                }
                else if (time >maxtime && day==maxday){            //위 논리대로 날짜가 같은 경우 시간비교
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (minute >maxminute && day==maxday && time == maxtime){        //분 비교
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (second > maxsecond && day==maxday && time == maxtime && minute == maxminute){       //초 비교
                    maxsecond = second
                    maxindex = j
                }
            }
            if(maxindex != -1) {
                var temp = data[i]
                data[i] = data[maxindex]            //그래서 가장 오래된 데이터는 맨 뒤로 보내준다
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

                if (day>maxday){                    //날짜 비교, 날짜가 크다 = 더 늦게 선택한 챌린지다
                    maxday  = day
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j                    //가장 최근에 신청한 챌린지가 담긴 인덱스 번호 저장
                    Log.i("sort","1")
                }
                else if (time >maxtime && day==maxday){            //위 논리대로 날짜가 같은 경우 시간비교
                    maxtime = time
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (minute >maxminute && day==maxday && time == maxtime){        //분 비교
                    maxminute = minute
                    maxsecond = second
                    maxindex = j
                }
                else if (second > maxsecond && day==maxday && time == maxtime && minute == maxminute){       //초 비교
                    maxsecond = second
                    maxindex = j
                }
            }
            if(maxindex != -1) {
                var temp = data[i]
                data[i] = data[maxindex]            //그래서 가장 오래된 데이터는 맨 뒤로 보내준다
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
                if (entireachivement>=maxachivement) {                    //날짜 비교, 날짜가 크다 = 더 늦게 선택한 챌린지다
                    maxachivement = entireachivement
                    maxindex = j                    //가장 최근에 신청한 챌린지가 담긴 인덱스 번호 저장
                }
            }
            if(maxindex != -1) {
                var temp = data[i]
                data[i] = data[maxindex]            //그래서 가장 오래된 데이터는 맨 뒤로 보내준다
                data[maxindex] = temp
            }
        }
    }


}