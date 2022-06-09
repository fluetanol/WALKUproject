package com.konkuk.walku.src.main.analysis.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
@Parcelize
data class Step(
    var stepCount: Int, // 걸음 수
    var stepGoal: Int, // 목표 걸음 수
    val distance: Double, // 거리
    val date: String //날짜
) : Parcelable