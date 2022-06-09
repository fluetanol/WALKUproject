package com.konkuk.walku.src.main.analysis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.*
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentAnalysisBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.analysis.model.AnalysisData
import com.konkuk.walku.src.main.analysis.model.LocationList
import com.konkuk.walku.src.main.analysis.model.Step
import com.konkuk.walku.src.main.analysis.model.Walk
import com.konkuk.walku.src.main.analysis.recordmap.RecordMapFragment
import com.konkuk.walku.src.main.analysis.statistics.StatisticsFragment
import com.konkuk.walku.src.main.analysis.today.TodayFragment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream.range


class AnalysisFragment : BaseFragment<FragmentAnalysisBinding>(FragmentAnalysisBinding::bind, R.layout.fragment_analysis),
    AnalysisFragmentView {

    private val fragmentList = listOf(TodayFragment(),RecordMapFragment(),StatisticsFragment())
    lateinit var rdb: DatabaseReference

    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE,FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE,FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .addDataType(DataType.TYPE_LOCATION_SAMPLE)
        .build()

    private val location_listener = OnDataPointListener { dataPoint ->
        for (field in dataPoint.dataType.fields) {
            val value = dataPoint.getValue(field)
            Log.i("asd", "Detected DataPoint field: ${field.name}")
            Log.i("asd", "Detected DataPoint value: $value")
        }
    }

    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDB()

        setSwipeView()
        accessGoogleFit()
    }

    private fun initDB() {
        rdb= Firebase.database.reference
        val userid = "ksho0925"
        rdb.child("Customer").child(userid).get().addOnSuccessListener {
            val analysisData =it.child("analysis")
            val stepData = analysisData.child("stepData")
            val walkData = analysisData.child("walkData")

            //step데이터 얻음
            val stepList = ArrayList<Step>()
            if(stepData.exists()){
                for(data in stepData.children){
                    val stepgoal:Int = if(data.child("stepGoal").exists())
                        data.child("stepGoal").value.toString().toInt()
                    else
                        6000
                    val step:Int = if(data.child("stepCount").exists())
                        data.child("stepCount").value.toString().toInt()
                    else
                        0
                    val distance:Double = if(data.child("distance").exists())
                        data.child("distance").value.toString().toDouble()
                    else
                        0.0
                    val date:String = if(data.child("date").exists())
                        data.child("date").value.toString()
                    else
                        LocalDate.now().toString()
                    stepList.add(Step(step,stepgoal,distance,date))
                    Log.i("asd","$step $stepgoal $distance")
                }
            }else{
                val stepgoal= 6000
                val step=  0
                val distance = 0.0
                val date = LocalDate.now().toString()
                stepList.add(Step(step,stepgoal,distance,date))
                Log.i("asd","$step $stepgoal $distance $date")
            }
            var flag = 0
            for(i in 0 until stepList.size){
                if(stepList[i].date==LocalDate.now().toString()){
                    flag = 1
                }
            }
            if(flag == 0 ){
                val stepgoal= 6000
                val step=  0
                val distance = 0.0
                val date = LocalDate.now().toString()
                stepList.add(Step(step,stepgoal,distance,date))
            }

            //walkdata얻음
            val walkList = ArrayList<LocationList>()
            if(walkData.exists()){
                for(data in walkData.children){
                    val locationList =data.child("locationArrayList").children
                    val ll = ArrayList<Walk>()
                    for(value in locationList){
                        val latitude:Double = if(data.child("latitude").exists())
                            data.child("latitude").value.toString().toDouble()
                        else
                            0.0
                        val longitude:Double = if(data.child("longitude").exists())
                            data.child("longitude").value.toString().toDouble()
                        else
                            0.0
                        ll.add(Walk(latitude,longitude))
                    }
                    walkList.add(LocationList(ll))
                }
            }else{
                val ll = ArrayList<Walk>()
                val latitude:Double = 0.0
                val longitude:Double = 0.0
                ll.add(Walk(latitude,longitude))
                walkList.add(LocationList(ll))
            }
            val anData = AnalysisData(stepList,walkList)
            val bundle = Bundle()
            //to todayFragment
            bundle.putParcelable("analysisData",anData)
            requireActivity().supportFragmentManager.setFragmentResult("analysisData",bundle)

        }.addOnFailureListener {
            Log.i("asd","해당하는 사용자 없음")
        }

    }

    private fun accessGoogleFit() {
//        updateStepData(2000)
//
//        subscribeData()
//
//        dailyStepData()
//
//        //n일전 걸음수 가져오기\
//        getStepData(7)

        //getDataLocation()
    }

    private fun getDataLocation() {  //10초간격으로 listener(위치데이터 받아옴)
        Fitness.getSensorsClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .add(
                SensorRequest.Builder()
                    //.setDataSource(dataSource) // Optional but recommended for custom
                    // data sets.
                    .setDataType(DataType.TYPE_LOCATION_SAMPLE) // Can't be omitted.
                    .setSamplingRate(10, TimeUnit.SECONDS)
                    .build(),
                location_listener
            )
    }
    private fun removeDataLocation() { // 위치데이터 리스너 제거
        Fitness.getSensorsClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .remove(location_listener)
            .addOnSuccessListener {
                Log.i("asd", "Listener was removed!")
            }
            .addOnFailureListener {
                Log.i("asd", "Listener was not removed.")
            }
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
            .subscribe(DataType.TYPE_LOCATION_SAMPLE)
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
        val tabTitles = listOf("오늘의 걸음", "내가간 경로", "일주일 통계")
        TabLayoutMediator(
            binding.fragmentAnalysisTapLayout,
            binding.fragmentAnalysisViewPager
        ) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.fragmentAnalysisViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {


            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                binding.fragmentAnalysisViewPager.isUserInputEnabled = !(state == SCROLL_STATE_DRAGGING && binding.fragmentAnalysisViewPager.currentItem == 1)
            }
        })

    }


    override fun onDetach() {
        super.onDetach()
        Log.i("asd","AnalysisOnDetach!!")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("asd","AnalysisOnDestroy!!")
        for(i:Int in range(0,3)){
            fragmentList[i].onPause()
            fragmentList[i].onStop()
            fragmentList[i].onDestroyView()
            fragmentList[i].onDestroy()
            fragmentList[i].onDetach()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("asd","AnalysisOnDestroyView!!")

    }

    override fun onPause() {
        super.onPause()
        Log.i("asd","AnalysisOnPause!!")

    }
}