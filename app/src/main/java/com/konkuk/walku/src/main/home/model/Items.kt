package com.konkuk.walku.src.main.home.model

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("item") val item: ArrayList<Item>
)