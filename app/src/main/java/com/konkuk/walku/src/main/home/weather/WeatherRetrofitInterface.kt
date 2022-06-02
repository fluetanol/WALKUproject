package com.konkuk.walku.src.main.home.weather

import com.konkuk.walku.BuildConfig
import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRetrofitInterface {

    // 기상청 초단기예보 실황 API
    @GET("1360000/VilageFcstInfoService_2.0/getUltraSrtFcst")
    fun getWeather(
        @Query("serviceKey", encoded = true) serviceKey: String = BuildConfig.HOME_WEATHER_API_KEY,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String = "json",
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Call<GetWeatherResponse>

}