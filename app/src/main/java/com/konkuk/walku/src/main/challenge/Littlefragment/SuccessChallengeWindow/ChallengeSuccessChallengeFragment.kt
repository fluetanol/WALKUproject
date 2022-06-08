package com.konkuk.walku.src.main.challenge.Littlefragment.SuccessChallengeWindow

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
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView.RecyclerDecoadpater
import com.konkuk.walku.src.main.challenge.Littlefragment.NewChallengeWindow.ChallengeNewRecyclerAdapter
import kotlinx.coroutines.NonCancellable.children
import java.util.*


class ChallengeSuccessChallengeFragment : BaseFragment<FragmentChallengeMychallengeBinding>(
    FragmentChallengeMychallengeBinding::bind,
    R.layout.fragment_challenge_mychallenge){
    val Customer = Firebase.database.getReference("Customer")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/Success")

    var data = ArrayList<ChallengeData>()
    var count = 0                        //counttext에 출력될 리스트 목록 갯수
    var count2 = 0
    lateinit var adapter: ChallengeSuccessRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        challengeinit()
    }

    override fun onResume() {
        super.onResume()
        if (count != 0)
            binding.counttext.text = "새로운 챌린지: $count"
    }

    //리사이클러뷰 초기화 + 버튼리스너
     fun challengeinit() {
        binding.recyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val decoration = RecyclerDecoadpater()
        binding.recyclerview.addItemDecoration(decoration)
        adapter = ChallengeSuccessRecyclerAdapter(data)
        binding.recyclerview.adapter = adapter
    }

    //데이터 가져오는 리스너, 프레그먼트가 실행될떄, 재실행(resume)될때, 데이터베이스에 변동이 있을때 모두 작동
    val walk = Customer.addValueEventListener(object : ValueEventListener {
        //데이터가 바뀔떄 호출하거나 처음에 자동 호출되는 콜백함수
        override fun onDataChange(snapshot: DataSnapshot) {
            val accountchallengesuccess = snapshot.child("mike415415").child("Challenge").child("Success")
            val accountchallengeflag = snapshot.child("mike415415").child("Challenge").child("flag")
            //챌린지 목록을 처음 꺼내올때, 내 계정의 챌린지 리스트로 정보를 옮긴다. 챌린지 리스트는 데베에 저장되어있다.
            //이미 내 계정의 챌린지 리스트가 있는 경우, 그 안에서 꺼내옴
             if (data.size == 0) {
                for (j in accountchallengesuccess.child("WalkDistanceChallenge").children.iterator()) {
                    if (j.child("context").value.toString() != "null") {
                        val newvalue = ChallengeData(
                            j.key!!.toInt(),
                            j.child("challengetype").value.toString(),
                            j.child("day").value.toString(),
                            j.child("context").value.toString(),
                            0,
                            "00:00:00",
                            "00 00 00",
                            Timer())
                        count += 1
                        data.add(newvalue)
                    }
                }
                 for (j in accountchallengesuccess.child("WalkCountChallenge").children.iterator()) {
                     if (j.child("context").value.toString() != "null") {
                         val newvalue = ChallengeData(j.key!!.toInt(),
                             j.child("challengetype").value.toString(),
                             j.child("day").value.toString(),
                             j.child("context").value.toString(),
                             0,
                             "00:00:00",
                             "00 00 00",
                            Timer())
                         count2 += 1
                         data.add(newvalue)
                     }
                 }
            }
            try {
                binding.counttext.text = "성공한 챌린지: "+(count+count2).toString()
                adapter.notifyDataSetChanged()
                recyclernone()
            } catch (e: Exception) {
            }
        }
        //모종의 이유로 데베쪽에서 문제가 생겨서 데이터 가져오는 것에 실패했을때 처리할 일
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
        }
    })

    //리사이클러 뷰에 아무것도 없을때...
    private fun recyclernone() {
        if (count == 0 && count2==0) {
            binding.nothing.visibility = VISIBLE
            binding.recyclerview.visibility = GONE
        } else {
            binding.nothing.visibility = GONE
            binding.recyclerview.visibility = VISIBLE
        }
    }
}