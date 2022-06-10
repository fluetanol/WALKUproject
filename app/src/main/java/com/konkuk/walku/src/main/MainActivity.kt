package com.konkuk.walku.src.main

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_ACCOUNT
import com.konkuk.walku.config.ApplicationClass.Companion.sSharedPreferences
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityMainBinding
import com.konkuk.walku.src.main.analysis.AnalysisFragment
import com.konkuk.walku.src.main.analysis.model.LocationList
import com.konkuk.walku.src.main.analysis.model.Walk
import com.konkuk.walku.src.main.challenge.ChallengeFragment
import com.konkuk.walku.src.main.home.HomeFragment
import com.konkuk.walku.src.main.settings.SettingsFragment
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    val locationList = ArrayList<Walk>()
    var todayOnPause by Delegates.notNull<Boolean>()
    var recordStart:Boolean=false
    var lat = 0.0
    var lon = 0.0
    private val step_listener = OnDataPointListener { dataPoint ->
        val bundle = Bundle()
        bundle.putInt("step",1)
        if(todayOnPause){
        }else{
            this.supportFragmentManager.setFragmentResult("step",bundle)
            this.supportFragmentManager.setFragmentResult("step2",bundle)
        }
    }
    private val location_listener = OnDataPointListener { dataPoint ->
        for (field in dataPoint.dataType.fields) {
            val value = dataPoint.getValue(field)
            if(field.name=="latitude") lat = value.toString().toDoubleOrNull()!!
            if(field.name=="longitude") lon = value.toString().toDoubleOrNull()!!
            Log.i("asd", "Detected DataPoint field: ${field.name}")
            Log.i("asd", "Detected DataPoint value: $value")
        }
        locationList.add(Walk(lat,lon))
    }

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE,FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE,FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .addDataType(DataType.TYPE_LOCATION_SAMPLE)
        .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
        .addDataType(DataType.TYPE_DISTANCE_DELTA)
        .build()

    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recieveTodayOnPause()
        subscribeData()
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()
        binding.mainBtmFab.setOnClickListener {
            Log.i("asd","in fab btn")
            recordStart = recordStart != true
            if(recordStart){
                getDataLocation()
            }else{
                if(locationList.size>1)
                    insertDB(locationList)
                removeDataLocation()
            }
        }
        binding.mainBtmNav.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.menu_main_btm_nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.menu_main_btm_nav_challenge -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ChallengeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.menu_main_btm_nav_settings-> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SettingsFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.menu_main_btm_nav_analysis -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, AnalysisFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true

                }
                R.id.action_empty -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                }
            }
            false
        }

    }

    private fun recieveTodayOnPause() {
        todayOnPause=false
//        stepC=1
        this.supportFragmentManager.setFragmentResultListener("onPause",this
        ) { requestKey, result ->
            todayOnPause = result.getBoolean("onPause")
            Log.i("asdmain","bundle 받았습니다")
        }

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                99, // e.g. 1
                account,
                fitnessOptions)
        }else {
            getDataStep()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
            Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .remove(step_listener)
                .addOnSuccessListener {
                    Log.i("asd", "Listener was removed!")
                }
                .addOnFailureListener {
                    Log.i("asd", "Listener was not removed.")
                }

        } else {
            showCustomToast("앱을 종료합니다.")
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }

    private fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }

    private fun getDataStep() {  //1초간격으로 listener(걸음 데이터 받아옴)
        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .add(
                SensorRequest.Builder()
                    //.setDataSource(dataSource) // Optional but recommended for custom
                    // data sets.
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
                    .setSamplingRate(1, TimeUnit.SECONDS)
                    .build(),
                step_listener
            ).addOnSuccessListener {
                Log.i("asd","success step listener")
            }
    }
    private fun getDataLocation() {  //10초간격으로 listener(위치데이터 받아옴)
        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .add(
                SensorRequest.Builder()
                    //.setDataSource(dataSource) // Optional but recommended for custom
                    // data sets.
                    .setDataType(DataType.TYPE_LOCATION_SAMPLE) // Can't be omitted.
                    .setSamplingRate(10, TimeUnit.SECONDS)
                    .build(),
                location_listener
            ).addOnSuccessListener {
                Log.i("asd","success location listener")
            }
    }
    private fun removeDataLocation() { // 위치데이터 리스너 제거
        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .remove(location_listener)
            .addOnSuccessListener {
                Log.i("asd", "Listener was removed!")
            }
            .addOnFailureListener {
                Log.i("asd", "Listener was not removed.")
            }
    }
    private fun subscribeData() {//사용자별로 최초 한번 subscribe해야 google fit 이용 가능!! step & distance
        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i("asd2", "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.i("asd", "There was a problem subscribing.", e)
            }
        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
            .subscribe(DataType.TYPE_DISTANCE_DELTA)
            .addOnSuccessListener {
                Log.i("asd2", "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.i("asd", "There was a problem subscribing.", e)
            }
        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    Log.i("asd3", "Active subscription for data type: ${dt?.name}")
                }
            }
    }
    private fun insertDB(locList: ArrayList<Walk>){
        val rdb= Firebase.database.reference
        try {
            val userid = sSharedPreferences.getString(K_USER_ACCOUNT,null)?.split('@')?.get(0)

            val key=rdb.child("Customer/$userid").child("analysis").child("walkData").push().key

            val locLista = LocationList(locList)
            val locListb = locLista.toMap()

            val childUpdate = hashMapOf<String,Any>(
                "/Customer/$userid/analysis/walkData/$key" to locListb
            )
            rdb.updateChildren(childUpdate)
        }catch (e:Exception){
            Log.i("asd", e.message.toString())
        }
    }
}