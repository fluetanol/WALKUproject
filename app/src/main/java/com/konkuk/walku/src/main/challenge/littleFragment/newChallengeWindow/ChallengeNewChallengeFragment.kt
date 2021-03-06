package com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.SystemClock.sleep
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
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialog(requireContext())
        challengeinit()
        val walk= database.addValueEventListener(object:ValueEventListener{
            //???????????? ????????? ??????????????? ????????? ?????? ???????????? ????????????
            override fun onDataChange(snapshot: DataSnapshot) {
                val walkchallengelist = snapshot.child("Challenge").child("Challengelist")
                val accountchallengenewcount = snapshot.child("Customer").child("mike415415").child("Challenge").child("New").child("WalkCountChallenge")
                val accountchallengenewdistance = snapshot.child("Customer").child("mike415415").child("Challenge").child("New").child("WalkDistanceChallenge")
                val accountchallengeflag = snapshot.child("Customer").child("mike415415").child("Challenge").child("flag")
                //????????? ????????? ?????? ????????????, ??? ????????? ????????? ???????????? ????????? ?????????. ????????? ???????????? ????????? ??????????????????.
                if (accountchallengeflag.value == false) {
                    challengeflag.setValue(true)
                    challengenew.setValue(walkchallengelist.value)
                }
                //?????? ??? ????????? ????????? ???????????? ?????? ??????, ?????? ????????????????????? ????????? ??? ????????? ?????????

                if(accountchallengeflag.value == true && callflag) {
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
                            Log.i("test","2 child: "+ newvalue.toString())
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
                    callflag = false
                }
                    challengeviewmodel.newdatalist.value = data
                    binding.counttext.text = "????????? ?????????: " + data.size.toString()
                    recyclernone()
                    dismissLoadingDialog()
            }
            //????????? ????????? ??????????????? ????????? ????????? ????????? ???????????? ?????? ??????????????? ????????? ???
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        callflag = true
        if(data.size!=0)
            binding.counttext.text= "????????? ?????????: ${data.size}"
    }

    //?????????????????? ????????? + ???????????????
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
            //????????? ????????? ?????? ???????????? "??? ?????????"??? ?????? ???????????? ????????????.
            override fun clickbuttonlistener(pos: Int) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("??????")
                    .setMessage(data[pos].context + ": ??? ???????????? ?????????????????????????")
                    .setPositiveButton("??????") { dialog, which ->
                        val temp = data[pos]
                        callflag= true
                        val day = Calendar.getInstance()
                        val date = day.get(Calendar.DATE).toString()
                        val hour = day.get(Calendar.HOUR_OF_DAY).toString()
                        val minute = day.get(Calendar.MINUTE).toString()
                        val second = day.get(Calendar.SECOND).toString()

                        challengenew.child(temp.challengetype).child(temp.num.toString()).removeValue()
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("challengetype").setValue(temp.challengetype)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("day").setValue(temp.day)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("context").setValue(temp.context)  //??? ????????? ????????? ??????(????????? ??????)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("achiveamount").setValue(temp.achivementamount)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("achivement").setValue(0)  //??? ????????? ????????? ??????(????????? ??????)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("starttime").setValue("$date $hour $minute $second") //??? ????????? ????????? ??????(?????? ?????? ??????)
                        challengemy.child(temp.challengetype).child(temp.num.toString()).child("remaintime").setValue("$date $hour $minute $second")
                        binding.counttext.text = "????????? ?????????: " + data.size.toString()
                        Toast.makeText(context, "????????????! -> " + temp.context, Toast.LENGTH_SHORT).show()
                    }
                builder.setNegativeButton("??????") { dialog, which ->
                    Toast.makeText(context,  "?????????????????????", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        }
        binding.recyclerview.adapter = adapter
        binding.spinner.visibility = View.GONE

    }



    //??????????????? ?????? ???????????? ?????????...
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