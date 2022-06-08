package com.konkuk.walku.src.main.analysis.today

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentTodayBinding
import com.konkuk.walku.src.main.MainActivity
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class TodayFragment : BaseFragment<FragmentTodayBinding>(FragmentTodayBinding::bind, R.layout.fragment_today) {
    val circleBarView: CircleBarView by lazy { binding.customCircleBarView }
    var stepgoal:Int by Delegates.notNull<Int>()
    lateinit var mainActivity: MainActivity
    var step by Delegates.notNull<Int>()
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .addDataType(DataType.TYPE_LOCATION_SAMPLE)
        .build()

    private val step_listener = OnDataPointListener { dataPoint ->
        step +=1
        val progress = (step.toFloat())/stepgoal * 360
        Log.i("asd", "Detected DataPoint value: $step $progress")
        circleBarView.setProgress(progress,"$step / $stepgoal")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("asd","OnCreate!!")
        step = 1230 //파이어베이스에서 해당 사용자의 마지막 걸음수 가져오기
        getDataStep()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("asd","OnCreateView!!")
        stepgoal = 6000
        setGoal()
        circleBarView.setProgress((step.toFloat())/stepgoal.toFloat() * 360,"$step / $stepgoal")
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
                stepgoal = binding.goalInput.text.toString().toInt()
                binding.goalInput.text.clear()
                circleBarView.setProgress((step.toFloat())/stepgoal.toFloat() * 360,"$step / $stepgoal")
            }
        }
    }


    private fun getDataStep() {  //1초간격으로 listener(걸음 데이터 받아옴)
        Fitness.getSensorsClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .add(
                SensorRequest.Builder()
                    //.setDataSource(dataSource) // Optional but recommended for custom
                    // data sets.
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
                    .setSamplingRate(1, TimeUnit.SECONDS)
                    .build(),
                step_listener
            ).addOnSuccessListener {
                Log.i("asd","success listener")
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        Fitness.getSensorsClient(mainActivity, GoogleSignIn.getAccountForExtension(mainActivity, fitnessOptions))
            .remove(step_listener)
            .addOnSuccessListener {
                Log.i("asd", "Listener was removed!")
            }
            .addOnFailureListener {
                Log.i("asd", "Listener was not removed.")
            }

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
        super.onPause()
        Log.i("asd","TodayOnPause!!")

    }


}