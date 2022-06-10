package com.konkuk.walku.src.main.home.map.bikeMap

import com.konkuk.walku.BuildConfig
import com.konkuk.walku.src.main.home.map.bikeMap.model.GetBikeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BikeMapRetrofitInterface {

    // 한 번에 1000건 밖에 요청 못함
    // 1/1000
    // 1001/2000
    // 2001/3000 (2022.06.10 기준 2614개의 따릉이 대여소가 있는데, 더 추가 될 수 있으니 일단 이렇게...)

    // 서울특별시 공공자전거 실시간 대여정보 API GET (따릉이)
    @GET("/{KEY}/json/bikeList/{start}/{end}")
    fun getBike(@Path("KEY", encoded = true) KEY: String = BuildConfig.SEOUL_PARK_API_KEY, @Path("start") start: Int, @Path("end") end: Int): Call<GetBikeResponse>

}