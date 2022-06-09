package com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentChallengeMychallengeBinding
import com.konkuk.walku.src.main.challenge.ChallengeData
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView.RecyclerDecoadpater
import java.util.*


class ChallengeNewChallengeFragment: BaseFragment<FragmentChallengeMychallengeBinding>(
    FragmentChallengeMychallengeBinding::bind,
    R.layout.fragment_challenge_mychallenge){
    val database = Firebase.database.getReference()
    val Customer = Firebase.database.getReference("Customer")
    val challengeflag = Firebase.database.getReference("Customer/mike415415/Challenge/flag")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/New")
    val challengemy = Firebase.database.getReference("Customer/mike415415/Challenge/My")
    var data = ArrayList<ChallengeData>()
    var count1 = 0;                        //counttext에 출력될 리스트 목록 갯수
    var count2 = 0;
    lateinit var adapter: ChallengeNewRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialog(requireContext())
        challengeinit()
        //데이터 가져오는 리스너, 프레그먼트가 실행될떄, 재실행(resume)될때, 데이터베이스에 변동이 있을때 모두 작동
        val walk= database.addValueEventListener(object:ValueEventListener{
            //데이터가 바뀔떄 호출하거나 처음에 자동 호출되는 콜백함수
            override fun onDataChange(snapshot: DataSnapshot) {
                val walkchallengelist = snapshot.child("Challenge").child("Challengelist")
                val accountchallengenewcount = snapshot.child("Customer").child("mike415415").child("Challenge").child("New").child("WalkCountChallenge")
                val accountchallengenewdistance = snapshot.child("Customer").child("mike415415").child("Challenge").child("New").child("WalkDistanceChallenge")
                val accountchallengeflag = snapshot.child("Customer").child("mike415415").child("Challenge").child("flag")

                data.clear()
                //챌린지 목록을 처음 꺼내올때, 내 계정의 챌린지 리스트로 정보를 옮긴다. 챌린지 리스트는 데베에 저장되어있다.
                if (accountchallengeflag.value == false) {
                    challengenew.setValue(walkchallengelist.value)
                    challengeflag.setValue(true)
                }
                //이미 내 계정의 챌린지 리스트가 있는 경우, 그 안에서 꺼내옴
                if(accountchallengeflag.value == true){
                    for (i in accountchallengenewdistance.children.iterator()) {
                        if (i.child("context").value.toString() != "null") {
                            val newvalue = ChallengeData(
                                i.key!!.toInt(),
                                "WalkDistanceChallenge",
                                i.child("day").value.toString(),
                                i.child("context").value.toString(),
                                0,
                                0,
                                "00:00:00",
                                "00 00 00",
                                Timer())
                            count1 += 1
                            data.add(newvalue)
                        }
                    }
                    for (j in accountchallengenewcount.children.iterator()) {
                        if (j.child("context").value.toString() != "null") {
                            val newvalue = ChallengeData(j.key!!.toInt(),
                                "WalkCountChallenge",
                                j.child("day").value.toString(),
                                j.child("context").value.toString(),
                                0,
                                0,
                                "00:00:00",
                                "00 00 00",
                                Timer())
                            count2 += 1
                            data.add(newvalue)
                        }
                    }
                }

                //fragment에서 벗어났다가 다시 create될때 view가 생성되기 전에 이 함수가 비동기식으로 빨리 호출되버리는건지
                //counttext같은 뷰객체에 무슨 짓을 하려고 하면 null exception으로 인식 못하는 경우가 있더라구용...
                //그래서 try catch로 잡아줬는데 동작엔 문제 없음
                    binding.counttext.text = "새로운 챌린지: " + data.size.toString()
                    adapter.notifyDataSetChanged()
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
        if(count1!=0)
            binding.counttext.text= "새로운 챌린지: "+data.size.toString()
    }

    //리사이클러뷰 초기화 + 버튼리스너
    fun challengeinit() {
        binding.recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        val decoration = RecyclerDecoadpater()
        binding.recyclerview.addItemDecoration(decoration)
        adapter  = ChallengeNewRecyclerAdapter(data)
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

                        challengenew.child(temp.challengetype).child(temp.num.toString())
                            .removeValue()
                        challengemy.child(temp.challengetype).child(temp.num.toString())
                            .child("challengetype").setValue(temp.challengetype)
                        challengemy.child(temp.challengetype).child(temp.num.toString())
                            .child("day").setValue(temp.day)
                        challengemy.child(temp.challengetype).child(temp.num.toString())
                            .child("context").setValue(temp.context)  //내 챌린지 리스트 추가(챌린지 내용)
                        challengemy.child(temp.challengetype).child(temp.num.toString())
                            .child("achivement").setValue(0)  //내 챌린지 리스트 추가(달성도 내용)
                        challengemy.child(temp.challengetype).child(temp.num.toString())
                            .child("starttime")
                            .setValue("$date $hour $minute $second") //내 챌린지 리스트 추가(목표 시작 시간)
                        challengemy.child(temp.challengetype).child(temp.num.toString())
                            .child("remaintime").setValue("$date $hour $minute $second")
                        count1--
                        binding.counttext.text = "새로운 챌린지: " + data.size.toString()
                        recyclernone()
                        Toast.makeText(context, "추가완료! -> " + temp.context, Toast.LENGTH_SHORT)
                            .show()
                        adapter.notifyDataSetChanged()
                    }
                builder.setNegativeButton("취소") { dialog, which ->
                    Toast.makeText(context,  "취소하였습니다", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        }
        binding.recyclerview.adapter = adapter
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