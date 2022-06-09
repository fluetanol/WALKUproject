package com.konkuk.walku.src.main

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat.finishAffinity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityMainBinding
import com.konkuk.walku.src.main.analysis.AnalysisFragment
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
            Log.i("asd", "Detected DataPoint field: ${field.name}")
            Log.i("asd", "Detected DataPoint value: $value")
        }
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
        getDataLocation()
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()

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
                R.id.main_btm_fab -> {
                    Log.i("asd","in fab btn")
                    recordStart = recordStart != true
                    if(recordStart){

                    }else{
                        removeDataLocation()
                    }
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
}