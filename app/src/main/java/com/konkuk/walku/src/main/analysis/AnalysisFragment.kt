package com.konkuk.walku.src.main.analysis

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentAnalysisBinding
import com.konkuk.walku.src.main.analysis.statistics.StatisticsFragment
import com.konkuk.walku.src.main.home.HomeAdapter
import com.konkuk.walku.src.main.home.map.MapFragment
import com.konkuk.walku.src.main.home.weather.WeatherFragment

class AnalysisFragment : BaseFragment<FragmentAnalysisBinding>(FragmentAnalysisBinding::bind, R.layout.fragment_analysis),
    AnalysisFragmentView {

    private val fragmentList = listOf(StatisticsFragment())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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