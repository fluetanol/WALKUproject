package com.konkuk.walku.src.main.home.weather

import com.konkuk.walku.config.ApplicationClass
import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService(val view: WeatherFragmentView) {

    fun tryGetWeather(
        numOfRows: Int,
        pageNo: Int,
        base_date: String,
        base_time: String,
        nx: Int,
        ny: Int
    ) {
        val getWeatherRetrofitInterface =
            ApplicationClass.sRetrofit[0].create(WeatherRetrofitInterface::class.java)
        getWeatherRetrofitInterface.getWeather(
            numOfRows = numOfRows,
            pageNo = pageNo,
            base_date = base_date,
            base_time = base_time,
            nx = nx,
            ny = ny
        ).enqueue(object :
            Callback<GetWeatherResponse> {
            override fun onResponse(
                call: Call<GetWeatherResponse>,
                response: Response<GetWeatherResponse>
            ) {
                view.onGetWeatherSuccess(response.body() as GetWeatherResponse)
            }

            override fun onFailure(call: Call<GetWeatherResponse>, t: Throwable) {
                view.onGetWeatherFailure(t.message ?: "통신 오류")
            }
        })
    }

}