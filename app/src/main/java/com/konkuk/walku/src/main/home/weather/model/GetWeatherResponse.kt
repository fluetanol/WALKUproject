package com.konkuk.walku.src.main.home.weather.model

import com.google.gson.annotations.SerializedName

data class GetWeatherResponse(
    @SerializedName("response") val response: Response
)