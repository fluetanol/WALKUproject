package com.konkuk.walku.src.main.home

import com.konkuk.walku.src.main.home.model.GetWeatherResponse

interface HomeFragmentView {

    fun onGetWeatherSuccess(response: GetWeatherResponse)

    fun onGetWeatherFailure(message: String)

}