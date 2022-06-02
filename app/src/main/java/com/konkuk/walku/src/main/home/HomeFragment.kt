package com.konkuk.walku.src.main.home

import android.os.Bundle
import android.view.View
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentHomeBinding
import com.konkuk.walku.src.main.home.model.GetWeatherResponse

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
    HomeFragmentView  {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 테스트입니다.
        HomeService(this).tryGetWeather(numOfRows = 300, pageNo = 1, base_date = "20220602", base_time = "1450", nx = 50, ny = 127)
        showLoadingDialog(requireActivity())
    }

    override fun onGetWeatherSuccess(response: GetWeatherResponse) {
        dismissLoadingDialog()
        // 테스트입니다.
        showCustomToast("날씨 api 연동 성공")
    }

    override fun onGetWeatherFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }

}