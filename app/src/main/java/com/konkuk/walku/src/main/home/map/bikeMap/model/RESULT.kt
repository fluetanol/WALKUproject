package com.konkuk.walku.src.main.home.map.bikeMap.model

import com.google.gson.annotations.SerializedName

data class RESULT(
    @SerializedName("CODE") val CODE: String,
    @SerializedName("MESSAGE") val MESSAGE: String
)