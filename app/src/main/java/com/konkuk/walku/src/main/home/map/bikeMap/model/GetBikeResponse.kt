package com.konkuk.walku.src.main.home.map.bikeMap.model

import com.google.gson.annotations.SerializedName

data class GetBikeResponse(
    @SerializedName("rentBikeStatus") val rentBikeStatus: RentBikeStatus
)