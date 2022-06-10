package com.konkuk.walku.src.main.challenge.littleFragment.successChallengeWindow

data class ChallengeSuccessData(
    var num: Int,
    var challengetype: String,
    var day: String,
    var context: String,
) {
    constructor() : this(0, "NAN", "0", "context")
}

