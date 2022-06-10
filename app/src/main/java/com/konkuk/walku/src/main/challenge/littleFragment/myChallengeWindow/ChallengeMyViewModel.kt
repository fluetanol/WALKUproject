package com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChallengeMyViewModel : ViewModel() {
    private val _datalist = ArrayList<ChallengeMyData>()
    val datalist:MutableLiveData<ArrayList<ChallengeMyData>>by lazy{
        MutableLiveData<ArrayList<ChallengeMyData>>()
    }
}