package com.konkuk.walku.src.main.home.map.bikeMap.model

import com.google.gson.annotations.SerializedName

data class RentBikeStatus(
    @SerializedName("list_total_count") val list_total_count: Int,
    @SerializedName("RESULT") val RESULT: RESULT,
    @SerializedName("row") val row: ArrayList<Row>
)