package com.konkuk.walku.src.main.analysis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.system.Os.bind
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class AnalysisFragment : BaseFragment<FragmentAnalysisBinding>(FragmentAnalysisBinding::bind, R.layout.fragment_analysis),
    AnalysisFragmentView {

    private val fragmentList = listOf(TodayFragment(),RecordMapFragment(),StatisticsFragment())
//    val fitnessOptions = FitnessOptions.builder()
//        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//        .build()
//    val account = GoogleSignIn.getAccountForExtension(activity!!.applicationContext, fitnessOptions)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSwipeView()
        //init()
    }

//    private fun init() {
//        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
//            GoogleSignIn.requestPermissions(
//                this, // your activity
//                1, // e.g. 1
//                account,
//                fitnessOptions)
//        } else {
//            accessGoogleFit()
//        }
//    }
//
//
//    private fun accessGoogleFit() {
//        val end = LocalDateTime.now()
//        val start = end.minusYears(1)
//        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
//        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()
//
//        val readRequest = DataReadRequest.Builder()
//            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
//            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
//            .bucketByTime(1, TimeUnit.DAYS)
//            .build()
//        val account = GoogleSignIn.getAccountForExtension(activity!!.applicationContext, fitnessOptions)
//        Fitness.getHistoryClient(requireActivity(), account)
//            .readData(readRequest)
//            .addOnSuccessListener { response ->
//                // Use response data here
//
//                Log.i("TAG", "OnSuccess()")
//            }
//            .addOnFailureListener { e -> Log.d("TAG", "OnFailure()", e) }
//        Fitness.getRecordingClient(requireActivity(), GoogleSignIn.getAccountForExtension(activity!!.applicationContext, fitnessOptions))
//            .listSubscriptions()
//            .addOnSuccessListener { subscriptions ->
//                for (sc in subscriptions) {
//                    val dt = sc.dataType
//                    Log.i("TAG", "Active subscription for data type: ${dt!!.name}")
//                }
//            }
//    }
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