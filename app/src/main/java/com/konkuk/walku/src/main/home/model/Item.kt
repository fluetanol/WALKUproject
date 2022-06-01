package com.konkuk.walku.src.main.home.model

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("baseDate") val baseDate: String,
    @SerializedName("baseTime") val baseTime: String,
    @SerializedName("category") val category: String,
    @SerializedName("nx") val nx: Int,
    @SerializedName("ny") val ny: Int,
    @SerializedName("obsrValue") val obsrValue: String
)