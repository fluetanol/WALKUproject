package com.konkuk.walku.src.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentHomeBinding
import com.konkuk.walku.src.main.home.map.MapFragment
import com.konkuk.walku.src.main.home.model.GetWeatherResponse
import com.konkuk.walku.src.main.home.weather.WeatherFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
    HomeFragmentView  {

    private val fragmentList = listOf(WeatherFragment(), MapFragment())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()

        // 테스트입니다.
        HomeService(this).tryGetWeather(numOfRows = 300, pageNo = 1, base_date = "20220602", base_time = "1530", nx = 50, ny = 127)
        showLoadingDialog(requireActivity())
    }

    private fun setViewPager() {
        val adapter = HomeAdapter(requireActivity())
        adapter.fragmentList = fragmentList
        binding.fragmentHomeViewPager.adapter = adapter
        val tabTitles = listOf("오늘의 날씨", "지도")
        TabLayoutMediator(binding.fragmentHomeTapLayout, binding.fragmentHomeViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
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