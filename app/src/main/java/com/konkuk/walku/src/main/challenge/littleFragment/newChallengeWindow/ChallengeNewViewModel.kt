package com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ChallengeNewViewModel : ViewModel() {
    private val _newdatalist = ArrayList<ChallengeNewData>()

    val newdatalist:MutableLiveData<ArrayList<ChallengeNewData>>by lazy{
        MutableLiveData<ArrayList<ChallengeNewData>>()
    }

}