package com.konkuk.walku.src.main.analysis.today

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Parcelable
import android.provider.Contacts.SettingsColumns.KEY
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentTodayBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.analysis.model.AnalysisData
import com.konkuk.walku.src.main.analysis.model.LocationList
import com.konkuk.walku.src.main.analysis.model.Step
import com.konkuk.walku.src.main.analysis.model.Walk
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class TodayFragment : BaseFragment<FragmentTodayBinding>(FragmentTodayBinding::bind, R.layout.fragment_today) {
    val circleBarView: CircleBarView by lazy { binding.customCircleBarView }
    var todayIndex by Delegates.notNull<Int>()
    lateinit var mainActivity: MainActivity
    lateinit var rdb: DatabaseReference
    private lateinit var analysisData:AnalysisData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("asd","OnCreate!!")
        initAnalysisData()
        todayIndex=0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("asd","OnCreateView!!")
        bundleReciever()
        setGoal()
        circleBarDraw()
    }

    private fun initAnalysisData() {
        val al = ArrayList<Step>()
        al.add(Step(0,6000,0.0,LocalDate.now().toString()))
        val al3 = ArrayList<Walk>()
        al3.add(Walk(0.0,0.0))
        val ll= LocationList(al3)
        val al2 =  ArrayList<LocationList>()
        al2.add(ll)
        analysisData= AnalysisData(al, al2)
    }

    private fun bundleReciever() {
        requireActivity().supportFragmentManager.setFragmentResultListener("analysisData",mainActivity
        ) { requestKey, result ->
            analysisData.stepData.clear()
            analysisData.walkData.clear()
            analysisData = result.getParcelable("analysisData")!!
            Log.i("asd","bundle 받았습니다")
            searchTodayIndex()
            circleBarDraw()
        }
        requireActivity().supportFragmentManager.setFragmentResultListener("step3",mainActivity
        ) { requestKey, result ->
            analysisData.stepData[todayIndex].stepCount = result.get("step3").toString().toInt()
            circleBarDraw()
            insertDB()
        }
        requireActivity().supportFragmentManager.setFragmentResultListener("distance",mainActivity
        ) { requestKey, result ->
            analysisData.stepData[todayIndex].distance = result.get("distance").toString().toDouble()
            circleBarDraw()
            insertDB()
        }
    }

    private fun circleBarDraw(){
        val progress = (analysisData.stepData[todayIndex].stepCount.toFloat())/analysisData.stepData[todayIndex].stepGoal.toFloat() * 360
        circleBarView.setProgress(progress,analysisData.stepData[todayIndex].stepCount.toString()+"/"+analysisData.stepData[todayIndex].stepGoal.toString())
    }
    private fun searchTodayIndex(){
        for(i in 0 until analysisData.stepData.size){
            if(analysisData.stepData[i].date==LocalDate.now().toString()){
                todayIndex = i
                val todaydate = analysisData.stepData[i].date
                Log.i("asd","$todayIndex today :$todaydate ")
            }
        }
    }
    private fun setGoal() {
        binding.setgoal.isEnabled= false
        binding.goalInput.addTextChangedListener{
            binding.setgoal.isEnabled = !it.isNullOrBlank()
        }
        binding.setgoal.setOnClickListener {
            //goalInput의 목표 걸음수 파이어베이스에 저장
            if(binding.goalInput.textSize.toInt()!=0){
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
                analysisData.stepData[todayIndex].stepGoal = binding.goalInput.text.toString().toInt()
                binding.goalInput.text.clear()
                circleBarDraw()
            }
        }
    }

    private fun insertDB(){
        rdb= Firebase.database.reference
        try {
            rdb.child("Customer/ksho0925").child("analysis").setValue(analysisData).addOnSuccessListener {
                Log.i("asd","Data insert success")
            }.addOnFailureListener {
                Log.i("asd","Data insert fail")
            }
        }catch (e:Exception){
            Log.i("asd", e.message.toString())
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        //insertDB()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("asd","TodayOnDestroy!!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("asd","TodayOnDestroyView!!")
    }

    override fun onPause() {
        val bundle = Bundle()
        bundle.putBoolean("onPause",true)
        mainActivity.supportFragmentManager.setFragmentResult("onPause",bundle)
        super.onPause()
        Log.i("asd","TodayOnPause!!")

        //to statisticsFragment
        val bundle2 = Bundle()
        bundle2.putParcelable("analysisData2",analysisData)
        requireActivity().supportFragmentManager.setFragmentResult("analysisData2",bundle2)
    }

    override fun onResume() {
        super.onResume()
        val bundle = Bundle()
        bundle.putBoolean("onPause",false)
        mainActivity.supportFragmentManager.setFragmentResult("onPause",bundle)
    }


}