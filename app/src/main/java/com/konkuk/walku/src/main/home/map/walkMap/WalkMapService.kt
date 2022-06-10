package com.konkuk.walku.src.main.home.map.walkMap

import com.konkuk.walku.config.ApplicationClass
import com.konkuk.walku.src.main.home.map.walkMap.model.GetWalkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalkMapService(val view: WalkMapActivityView) {

    fun tryGetWalk() {
        val getWalkRetrofitInterface = ApplicationClass.sRetrofit[1].create(WalkMapRetrofitInterface::class.java)
        getWalkRetrofitInterface.getWalk().enqueue(object : Callback<GetWalkResponse> {
            override fun onResponse(call: Call<GetWalkResponse>, response: Response<GetWalkResponse>) {
                view.onGetWalkSuccess(response.body() as GetWalkResponse)
            }

            override fun onFailure(call: Call<GetWalkResponse>, t: Throwable) {
                view.onGetWalkFailure(t.message ?: "통신 오류")
            }
        })
    }

}