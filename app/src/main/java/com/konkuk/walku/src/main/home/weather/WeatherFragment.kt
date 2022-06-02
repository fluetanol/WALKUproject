package com.konkuk.walku.src.main.home.weather

import android.os.Bundle
import android.view.View
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentWeatherBinding
import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse

class WeatherFragment :
    BaseFragment<FragmentWeatherBinding>(FragmentWeatherBinding::bind, R.layout.fragment_weather),
    WeatherFragmentView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 테스트입니다.
        WeatherService(this).tryGetWeather(
            numOfRows = 300,
            pageNo = 1,
            base_date = "20220603",
            base_time = "0000",
            nx = 50,
            ny = 127
        )
        showLoadingDialog(requireActivity())

    }

    override fun onGetWeatherSuccess(response: GetWeatherResponse) {
        // 테스트입니다.
        dismissLoadingDialog()
        showCustomToast("날씨 api 연동 성공")
    }

    override fun onGetWeatherFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }

}