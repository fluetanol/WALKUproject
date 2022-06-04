package com.konkuk.walku.src.main.analysis


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.material.tabs.TabLayoutMediator
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentAnalysisBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.analysis.recordmap.RecordMapFragment
import com.konkuk.walku.src.main.analysis.statistics.StatisticsFragment
import com.konkuk.walku.src.main.analysis.today.TodayFragment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit


class AnalysisFragment : BaseFragment<FragmentAnalysisBinding>(FragmentAnalysisBinding::bind, R.layout.fragment_analysis),
    AnalysisFragmentView {

    private val fragmentList = listOf(TodayFragment(),RecordMapFragment(),StatisticsFragment())

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE,FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE,FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .build()

    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeView()
        accessGoogleFit()
    }

    private fun accessGoogleFit() {
        updateStepData(2000)

        subscribeData()

        dailyStepData()

        //n일전 걸음수 가져오기\
        getStepData(7)

        getDataLocation()
    }

    private fun getDataLocation() {
        // Read the data that's been collected throughout the past week.
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        Log.i("asd", "Range Start: $startTime")
        Log.i("asd", "Range End: $endTime")

        val readRequest =
            DataReadRequest.Builder()
                // The data request can specify multiple data types to return,
                // effectively combining multiple data queries into one call.
                // This example demonstrates aggregating only one data type.
                .aggregate(DataType.TYPE_LOCATION_SAMPLE)
                // Analogous to a "Group By" in SQL, defines how data should be
                // aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession allows
                // bucketing by <a href="/fit/android/using-sessions">sessions</a>.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()
    }

    private fun getStepData(n:Long) {
        val startTime = LocalDate.now().minusDays(n).atStartOfDay(ZoneId.systemDefault())
        val endTime = LocalDate.now().minusDays(n-1).atStartOfDay(ZoneId.systemDefault())
        //val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())

        val datasource = DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build()

        val request = DataReadRequest.Builder()
            .aggregate(datasource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .readData(request)
            .addOnSuccessListener { response ->
                val totalSteps = response.buckets
                    .flatMap { it.dataSets }
                    .flatMap { it.dataPoints }
                    .sumBy { it.getValue(Field.FIELD_STEPS).asInt() }
                Log.i("asd $n 일전", "Total steps: $totalSteps")
            }
    }

    private fun dailyStepData() {
        Fitness.getHistoryClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { result ->
                val totalSteps =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                // Do something with totalSteps
                Log.i("asd5","\tEnd: $totalSteps")
                bundleOf(Pair("key",totalSteps))
            }
            .addOnFailureListener { e ->
                Log.i("TAG", "There was a problem getting steps.", e)
            }
    }

    private fun subscribeData() {
        Fitness.getRecordingClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i("asd2", "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.i("asd", "There was a problem subscribing.", e)
            }
        Fitness.getRecordingClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    Log.i("asd3", "Active subscription for data type: ${dt?.name}")
                }
            }
    }

    private fun updateStepData(stepCountCumulative:Int) {
        // Declare that the historical data was collected during the past 50 minutes.
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        //val time = LocalDate.now().minusDays(7).atStartOfDay()
        val startTime = endTime.minusMinutes(50)

        // Create a data source
        val dataSource  = DataSource.Builder()
            .setAppPackageName(mainActivity)
            .setDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .setStreamName("asd - step count")
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create a data set
        // For each data point, specify a start time, end time, and the
        // data value -- in this case, 1000 new steps.


        val dataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .setField(Field.FIELD_STEPS, stepCountCumulative)
            .build()

        val dataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()
        val request = DataUpdateRequest.Builder()
            .setDataSet(dataSet)
            .setTimeInterval(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .updateData(request)
            .addOnSuccessListener {
                Log.i("asdUPDATE", "DataSet updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("asdUPDATE", "There was an error updating the DataSet", e)
            }
    }

    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()

    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
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