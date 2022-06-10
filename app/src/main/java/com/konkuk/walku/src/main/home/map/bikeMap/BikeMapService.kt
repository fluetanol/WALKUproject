package com.konkuk.walku.src.main.home.map.bikeMap

import com.konkuk.walku.config.ApplicationClass
import com.konkuk.walku.src.main.home.map.bikeMap.model.GetBikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BikeMapService(val view: BikeMapActivityView) {

    fun tryGetWalk(start: Int, end: Int) {
        val getBikeRetrofitInterface = ApplicationClass.sRetrofit[1].create(BikeMapRetrofitInterface::class.java)
        getBikeRetrofitInterface.getBike(start=start, end=end).enqueue(object : Callback<GetBikeResponse> {
            override fun onResponse(call: Call<GetBikeResponse>, response: Response<GetBikeResponse>) {
                view.onGetBikeSuccess(response.body() as GetBikeResponse)
            }

            override fun onFailure(call: Call<GetBikeResponse>, t: Throwable) {
                view.onGetBikeFailure(t.message ?: "통신 오류")
            }
        })
    }

}