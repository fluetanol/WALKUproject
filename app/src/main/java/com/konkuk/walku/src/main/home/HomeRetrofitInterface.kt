package com.konkuk.walku.src.main.home

import com.konkuk.walku.src.main.home.model.GetWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeRetrofitInterface {

    @GET("getUltraSrtNcst")
    fun getWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String = "json",
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Call<GetWeatherResponse>

}