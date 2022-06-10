package com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow

import java.util.*

data class ChallengeMyData(
    var num: Int,
    var challengetype: String,
    var day: String,
    var context: String,
    var achivementamount: String,
    var achivement: Int,
    var starttime: String,
    var remaintime: String,
    var timer: Timer,
    var buttontext :String
) {
    constructor() : this(0, "NAN", "0", "context", "0", 0, "00:00:00", "00 00 00 00", Timer(),"삭제")
}
//챌린지 번호, 챌린지 타입(종류), 챌린지 기간, 챌린지 내용, 챌린지 목표수치 (EX:1000걸음, 1KM등), 챌린지 달성도(EX: 1000걸음중 750걸음, 1KM중 480M), 챌린지 시작시간, 챌린지 남은 시간, 남은시간을 재기위한 타이머객체)