package com.konkuk.walku.src.main.analysis.today

import android.os.Bundle
import android.system.Os.bind
import android.view.View
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentStatisticsBinding
import com.konkuk.walku.databinding.FragmentTodayBinding

class TodayFragment : BaseFragment<FragmentTodayBinding>(FragmentTodayBinding::bind, R.layout.fragment_today) {

    val circleBarView: CircleBarView by lazy { binding.customCircleBarView }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        circleBarView.setProgress(50.0f)
    }

}