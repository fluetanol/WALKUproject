package com.konkuk.walku.src.main.home.model

import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String
)