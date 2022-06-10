package com.konkuk.walku.src.main.challenge

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyChallengeFragment
import com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow.ChallengeNewChallengeFragment
import com.konkuk.walku.src.main.challenge.littleFragment.successChallengeWindow.ChallengeSuccessChallengeFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.material.tabs.TabLayoutMediator
import com.konkuk.walku.databinding.FragmentChallengeBinding
import com.konkuk.walku.src.main.analysis.ChallengefragmentAdapter
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyData

class ChallengeFragment : BaseFragment<FragmentChallengeBinding>(FragmentChallengeBinding::bind, R.layout.fragment_challenge),
    ChallengeFragmentView {

    val data = ArrayList<ChallengeMyData>()
    val Customer = Firebase.database.getReference("Customer")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/New")
    val challengemy = Firebase.database.getReference("Customer/mike415415/Challenge/My")
    val challengeflag = Firebase.database.getReference("Customer/mike415415/Challenge/flag")
    val challengeupdateflag = Firebase.database.getReference("Customer/mike415415/Challenge/update")

    private val fragmentList = listOf(ChallengeMyChallengeFragment(),ChallengeNewChallengeFragment(),ChallengeSuccessChallengeFragment())
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengeinit()
        val walk= Customer.addValueEventListener(object: ValueEventListener {
            //데이터가 바뀔떄 호출하거나 처음에 자동 호출되는 콜백함수
            override fun onDataChange(snapshot: DataSnapshot) {
                val today = System.currentTimeMillis()
                val date = Date(today)
                val t_dateFormat = SimpleDateFormat("E", Locale("ko", "KR"))
                val str_date = t_dateFormat.format(date)
                val accountchallengeflag = snapshot.child("mike415415").child("Challenge").child("flag")
                val accountupdateflag = snapshot.child("mike415415").child("Challenge").child("update")

                //갱신날짜 ==월요일
                if(str_date == "월" && accountupdateflag.value==true) {
                    challengeflag.setValue(false)
                    challengenew.removeValue()
                    challengemy.removeValue()
                    challengeupdateflag.setValue(false)
                    Toast.makeText(context,"갱신완료!",Toast.LENGTH_SHORT).show()
                }
                else if (str_date != "월"){
                    challengeupdateflag.setValue(true)
                }
            }
            //모종의 이유로 데베쪽에서 문제가 생겨서 데이터 가져오는 것에 실패했을때 처리할 일
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun challengeinit() {
        val adapter = ChallengefragmentAdapter(requireActivity())
        adapter.fragmentList = fragmentList
        binding.challengeViewpager.adapter = adapter
        val tabTitles = listOf("내 챌린지", "새로운 챌린지", "해낸 챌린지")
        TabLayoutMediator(
            binding.challengetaplayout,
            binding.challengeViewpager
        ) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

}