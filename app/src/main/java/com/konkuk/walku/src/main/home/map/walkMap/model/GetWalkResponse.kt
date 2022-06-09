package com.konkuk.walku.src.main.home.map.walkMap.model

import com.google.gson.annotations.SerializedName

data class GetWalkResponse(
    @SerializedName("SearchParkInfoService") val SearchParkInfoService: SearchParkInfoService
)