package com.konkuk.walku.src.main.home.model

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("header") val header: Header,
    @SerializedName("body") val body: Body
)