package com.konkuk.walku.src.main.home.map.walkMap

import com.konkuk.walku.BuildConfig
import com.konkuk.walku.src.main.home.map.walkMap.model.GetWalkResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WalkMapRetrofitInterface {

    // 서울시 주요 공원현황 API
    @GET("/{KEY}/json/SearchParkInfoService/1/150/")
    fun getWalk(@Path("KEY", encoded = true) KEY: String = BuildConfig.SEOUL_PARK_API_KEY): Call<GetWalkResponse>

}