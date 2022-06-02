package com.konkuk.walku.src.main.home.weather

import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse


interface WeatherFragmentView {

    fun onGetWeatherSuccess(response: GetWeatherResponse)

    fun onGetWeatherFailure(message: String)

}