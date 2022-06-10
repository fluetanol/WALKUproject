package com.konkuk.walku.src.main.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentHomeBinding
import com.konkuk.walku.src.main.home.map.MapFragment
import com.konkuk.walku.src.main.home.weather.WeatherFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private val fragmentList = listOf(WeatherFragment(), MapFragment())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 탭 레이아웃과 뷰페이저2를 연결해주는 함수입니다.
        setSwipeView()

        // 오른쪽 탭으로 갔을 때 왼쪽으로 스와이프 이동되는거 막기
        binding.apply {
            fragmentHomeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    fragmentHomeViewPager.isUserInputEnabled = fragmentHomeViewPager.currentItem != 1
                }
            })
        }

    }

    private fun setSwipeView() {
        val adapter = HomeAdapter(requireActivity())
        adapter.fragmentList = fragmentList
        binding.fragmentHomeViewPager.adapter = adapter
        val tabTitles = listOf("오늘의 날씨", "지도")
        TabLayoutMediator(binding.fragmentHomeTapLayout, binding.fragmentHomeViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }


}