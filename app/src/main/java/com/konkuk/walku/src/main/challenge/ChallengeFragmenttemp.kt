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
import com.konkuk.walku.databinding.FragmentChallengeBinding
import com.konkuk.walku.databinding.FragmentChallengetempBinding
import com.konkuk.walku.src.main.challenge.Littlefragment.MyChallengeWindow.ChallengeMyChallengeFragment
import com.konkuk.walku.src.main.challenge.Littlefragment.NewChallengeWindow.ChallengeNewChallengeFragment
import com.konkuk.walku.src.main.challenge.Littlefragment.SuccessChallengeWindow.ChallengeSuccessChallengeFragment
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import com.konkuk.walku.src.main.challenge.ChallengeFragmentView.Companion.FLAG_MY_CHALLENGELIST
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView.Companion.FLAG_NEW_CHALLENGELIST
import com.konkuk.walku.src.main.challenge.ChallengeFragmentView.Companion.FLAG_SUCCESS_CHALLENGELIST


class ChallengeFragmenttemp : BaseFragment<FragmentChallengetempBinding>(FragmentChallengetempBinding::bind, R.layout.fragment_challengetemp), ChallengeFragmentView {
    val data = ArrayList<ChallengeData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengeinit()
    }

    val Customer = Firebase.database.getReference("Customer")
    val challengenew = Firebase.database.getReference("Customer/mike415415/Challenge/New")
    val challengemy = Firebase.database.getReference("Customer/mike415415/Challenge/My")
    val challengeflag = Firebase.database.getReference("Customer/mike415415/Challenge/flag")
    val challengeupdateflag = Firebase.database.getReference("Customer/mike415415/Challenge/update")

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


    override fun challengeinit() {

        ReplaceChallengeFragment(FLAG_NEW_CHALLENGELIST)
        binding.radiogroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId){
                R.id.radio1-> ReplaceChallengeFragment(FLAG_SUCCESS_CHALLENGELIST)
                R.id.radio2->ReplaceChallengeFragment(FLAG_NEW_CHALLENGELIST)
                R.id.radio3->ReplaceChallengeFragment(FLAG_MY_CHALLENGELIST)
            }
        }
    }


    fun ReplaceChallengeFragment(flag:Int){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.framelayout, if(flag==FLAG_NEW_CHALLENGELIST) ChallengeNewChallengeFragment()
            else if(flag==FLAG_MY_CHALLENGELIST)   ChallengeMyChallengeFragment()
            else ChallengeSuccessChallengeFragment())
            .commit()
    }

}