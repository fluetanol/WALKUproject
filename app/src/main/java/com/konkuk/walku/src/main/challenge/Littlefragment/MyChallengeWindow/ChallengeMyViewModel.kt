package com.konkuk.walku.src.main.challenge.Littlefragment.MyChallengeWindow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.konkuk.walku.src.main.challenge.ChallengeData

class ChallengeMyViewModel : ViewModel() {
    private val _datalist = ArrayList<ChallengeData>()
    val datalist:MutableLiveData<ArrayList<ChallengeData>>by lazy{
        MutableLiveData<ArrayList<ChallengeData>>()
    }
}