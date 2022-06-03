package com.konkuk.walku.src.main.analysis

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentAnalysisBinding
import com.konkuk.walku.src.main.analysis.recordmap.RecordMapFragment
import com.konkuk.walku.src.main.analysis.statistics.StatisticsFragment
import com.konkuk.walku.src.main.analysis.today.TodayFragment
import com.konkuk.walku.src.main.home.HomeAdapter
import com.konkuk.walku.src.main.home.map.MapFragment
import com.konkuk.walku.src.main.home.weather.WeatherFragment

class AnalysisFragment : BaseFragment<FragmentAnalysisBinding>(FragmentAnalysisBinding::bind, R.layout.fragment_analysis),
    AnalysisFragmentView {

    private val fragmentList = listOf(StatisticsFragment(),RecordMapFragment(),TodayFragment())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSwipeView()

    }

    private fun setSwipeView() {
        val adapter = AnalysisAdapter(requireActivity())
        adapter.fragmentList = fragmentList
        binding.fragmentAnalysisViewPager.adapter = adapter
        val tabTitles = listOf("오늘의 걸음", "내가간 경로","일주일 통계")
        TabLayoutMediator(binding.fragmentAnalysisTapLayout, binding.fragmentAnalysisViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}