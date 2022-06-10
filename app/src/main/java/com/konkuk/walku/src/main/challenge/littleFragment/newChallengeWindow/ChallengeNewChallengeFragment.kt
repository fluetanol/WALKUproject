package com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView.RecyclerDecoadpater
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyViewModel
import java.util.*

@SuppressLint("SetTextI18n")
class ChallengeNewChallengeFragment: BaseFragment<FragmentChallengeMychallengeBinding>(
    FragmentChallengeMychallengeBinding::bind,
    R.layout.fragment_challenge_mychallenge){
    val database = Firebase.database.getReference()
    val challengeflag = Firebase.database.getReference("Customer/mike415415/Challenge/flag")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/New")
    val challengemy = Firebase.database.getReference("Customer/mike415415/Challenge/My")
    var data = ArrayList<ChallengeNewData>()
    var callflag = true
    lateinit var adapter: ChallengeNewRecyclerAdapter
    var challengeviewmodel = ChallengeNewViewModel()

    companion object {
        const val HANGUEL_FIRST_UNICODE = '가'.code
        const val HANGUEL_LAST_UNICODE = '힣'.code
        const val HANGUEL_DIFFERENCE_UNICODE = '까'.code-'가'.code
        const val HANGUEL_MIDDLECOUNT_UNICODE = 28

        const val SEARCHFUNC_FLAG_INPUTSIZE_BIGGER = 0
        const val SEARCHFUNC_FLAG_INPUTSIZE_SMALLER = 1
    }

    var initialarray = arrayOf ('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ',
        'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' )
    var middlearray = arrayOf('ㅏ' ,'ㅐ' ,'ㅑ', 'ㅒ', 'ㅓ' ,'ㅔ' ,'ㅕ' ,'ㅖ' ,
        'ㅗ', 'ㅘ' ,'ㅙ' ,'ㅚ' ,'ㅛ', 'ㅜ' ,'ㅝ', 'ㅞ' ,'ㅟ' ,'ㅠ' ,'ㅡ' ,'ㅢ', 'ㅣ')
    var lastarray =arrayOf('ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ',
        'ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ')


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialog(requireContext())
        challengeinit()

        val walk= database.addValueEventListener(object:ValueEventListener{
            //데이터가 바뀔떄 호출하거나 처음에 자동 호출되는 콜백함수
            override fun onDataChange(snapshot: DataSnapshot) {
                challengeviewmodel.newdatalist.value = data
                val walkchallengelist = snapshot.child("Challenge").child("Challengelist")
                val accountchallengenewcount = snapshot.child("Customer").child("mike415415").child("Challenge").child("New").child("WalkCountChallenge")
                val accountchallengenewdistance = snapshot.child("Customer").child("mike415415").child("Challenge").child("New").child("WalkDistanceChallenge")
                val accountchallengeflag = snapshot.child("Customer").child("mike415415").child("Challenge").child("flag")
                //챌린지 목록을 처음 꺼내올때, 내 계정의 챌린지 리스트로 정보를 옮긴다. 챌린지 리스트는 데베에 저장되어있다.
                if (accountchallengeflag.value == false) {
                    Log.i("test","call1")
                    challengenew.setValue(walkchallengelist.value)
                    challengeflag.setValue(true)
                }
                //이미 내 계정의 챌린지 리스트가 있는 경우, 처음 프레그먼트에서 호출시 그 안에서 꺼내옴

                if(accountchallengeflag.value == true && callflag) {
                    Log.i("test","call2")
                    data.clear()
                    for (i in accountchallengenewdistance.children.iterator()) {
                        if (i.child("context").value.toString() != "null") {
                            val newvalue = ChallengeNewData(
                                i.key!!.toInt(),
                                "WalkDistanceChallenge",
                                i.child("day").value.toString(),
                                i.child("context").value.toString(),
                                i.child("achiveamount").value.toString(),
                                Timer())
                            data.add(newvalue)
                        }
                    }
                    for (j in accountchallengenewcount.children.iterator()) {
                        if (j.child("context").value.toString() != "null") {
                            val newvalue = ChallengeNewData(j.key!!.toInt(),
                                "WalkCountChallenge",
                                j.child("day").value.toString(),
                                j.child("context").value.toString(),
                                j.child("achiveamount").value.toString(),
                                Timer())
                            data.add(newvalue)
                        }
                    }
                    callflag = false;
                }
                    binding.counttext.text = "새로운 챌린지: " + data.size.toString()
                    recyclernone()
                    dismissLoadingDialog()
            }
            //모종의 이유로 데베쪽에서 문제가 생겨서 데이터 가져오는 것에 실패했을때 처리할 일
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        callflag = true
        if(data.size!=0)
            binding.counttext.text= "새로운 챌린지: ${data.size}"
    }

    //리사이클러뷰 초기화 + 버튼리스너
    fun challengeinit() {
        binding.recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        val decoration = RecyclerDecoadpater()
        binding.recyclerview.addItemDecoration(decoration)
        var animation = binding.recyclerview.itemAnimator
        (animation as SimpleItemAnimator).supportsChangeAnimations = false

        challengeviewmodel.newdatalist.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        adapter  = ChallengeNewRecyclerAdapter()
        adapter.onclickbuttonlistener=   object: ChallengeNewRecyclerAdapter.OnclickButtonListener {
            //버튼을 누를시 해당 리스트가 "내 챌린지"로 가고 리스트가 삭제된다.
            override fun clickbuttonlistener(pos: Int) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("확인")
                    .setMessage(data[pos].context + ": 이 챌린지를 수락하시겠습니까?")
                    .setPositiveButton("확인") { dialog, which ->
                        val temp = data.removeAt(pos)
                        val day = Calendar.getInstance()
                        val date = day.get(Calendar.DATE).toString()
                        val hour = day.get(Calendar.HOUR_OF_DAY).toString()
                        val minute = day.get(Calendar.MINUTE).toString()
                        val second = day.get(Calendar.SECOND).toString()

                        challengenew.child(temp.challengetype).child(temp.num.toString()).removeValue()
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("challengetype").setValue(temp.challengetype)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("day").setValue(temp.day)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("context").setValue(temp.context)  //내 챌린지 리스트 추가(챌린지 내용)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("achiveamount").setValue(temp.achivementamount)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("achivement").setValue(0)  //내 챌린지 리스트 추가(달성도 내용)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("starttime").setValue("$date $hour $minute $second") //내 챌린지 리스트 추가(목표 시작 시간)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("remaintime").setValue("$date $hour $minute $second")
                        binding.counttext.text = "새로운 챌린지: " + data.size.toString()
                        Toast.makeText(context, "추가완료! -> " + temp.context, Toast.LENGTH_SHORT).show()
                    }
                builder.setNegativeButton("취소") { dialog, which ->
                    Toast.makeText(context,  "취소하였습니다", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        }
        binding.recyclerview.adapter = adapter
        binding.spinner.visibility = View.GONE
        binding.searchwindow.visibility=View.VISIBLE


        val searching = Searching()
        //searching.initarray(data)
        var nowsize=0
        var latesize=-1
        binding.searchwindow.addTextChangedListener {
            val inputstr=it.toString()
            latesize = nowsize
            nowsize = inputstr.length
            if(nowsize>latesize) searching.SearchFunction(inputstr, SEARCHFUNC_FLAG_INPUTSIZE_BIGGER,data)
            else searching.SearchFunction(inputstr, SEARCHFUNC_FLAG_INPUTSIZE_SMALLER,data)
        }
    }



    //리사이클러 뷰에 아무것도 없을때...
    private fun recyclernone(){
        if(data.size==0){
            binding.nothing.visibility = VISIBLE
            binding.recyclerview.visibility =GONE
        }
        else{
            binding.nothing.visibility = GONE
            binding.recyclerview.visibility =VISIBLE
        }
    }
}