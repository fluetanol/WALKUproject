package com.konkuk.walku.src.main.analysis.model

data class Step(
    val stepCount: Int, // 걸음 수
    val stepGoal: Int, // 목표 걸음 수
    val distance: Double // 거리
)