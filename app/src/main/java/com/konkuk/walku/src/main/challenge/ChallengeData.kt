package com.konkuk.walku.src.main.challenge

import java.util.*

data class ChallengeData(var num:Int, var challengetype:String ,var day:String, var context:String, var achivement:Int, var starttime:String, var remaintime:String,var timer: Timer){
    constructor():this(0,"none","0","context",0,"00:00:00","00 00 00 00",Timer())
}
