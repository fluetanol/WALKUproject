package com.konkuk.walku.src.main.home.map.bikeMap.model

import com.google.gson.annotations.SerializedName

data class Row(
    @SerializedName("rackTotCnt") val rackTotCnt: String,
    @SerializedName("stationName") val stationName: String,
    @SerializedName("parkingBikeTotCnt") val parkingBikeTotCnt: String,
    @SerializedName("shared") val shared: String,
    @SerializedName("stationLatitude") val stationLatitude: String,
    @SerializedName("stationLongitude") val stationLongitude: String,
    @SerializedName("stationId") val stationId: String,
)