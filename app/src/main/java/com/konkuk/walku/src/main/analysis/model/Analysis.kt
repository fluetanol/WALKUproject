package com.konkuk.walku.src.main.analysis.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalysisData(
    val stepData: ArrayList<Step>, // 걸음 수 데이터, 일주일치 정보
    //val bodyData: Body, // 나의 신체 정보
    val walkData: ArrayList<LocationList> // 나의 산책 기록 데이터, 방문 지점 위,경도 배열
) : Parcelable





