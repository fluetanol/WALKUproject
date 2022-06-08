package com.konkuk.walku.src.main.home.weather.model

import com.google.gson.annotations.SerializedName

// 날씨 정보를 담는 데이터 클래스
data class ModelWeather (
    @SerializedName("rainType") var rainType: String = "",      // 강수 형태
    @SerializedName("humidity") var humidity: String = "",      // 습도
    @SerializedName("sky") var sky: String = "",           // 하능 상태
    @SerializedName("temp") var temp: String = "",          // 기온
    @SerializedName("fcstTime") var fcstTime: String = "",      // 예보시각
    @SerializedName("maxTemp") var maxTemp: String = "",  // 최고 기온
    @SerializedName("minTemp") var minTemp: String = "",   // 최저 기온
    @SerializedName("rainPossibility") var rainPossibility: String = "", // 강수 확률
)