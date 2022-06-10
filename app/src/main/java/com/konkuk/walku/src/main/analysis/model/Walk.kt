package com.konkuk.walku.src.main.analysis.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Walk(
    val latitude: Double,
    val longitude: Double
) : Parcelable