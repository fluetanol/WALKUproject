package com.konkuk.walku.src.main.analysis.statistics

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.DatabaseReference
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentStatisticsBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.analysis.model.AnalysisData
import com.konkuk.walku.src.main.analysis.model.LocationList
import com.konkuk.walku.src.main.analysis.model.Step
import com.konkuk.walku.src.main.analysis.model.Walk
import java.time.LocalDate

class StatisticsFragment: BaseFragment<FragmentStatisticsBinding>(FragmentStatisticsBinding::bind, R.layout.fragment_statistics) {
    lateinit var analysisData:AnalysisData
    lateinit var mainActivity: MainActivity
    lateinit var rdb: DatabaseReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAnalysisData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bundleReciever()
        setChartView()
    }

    override fun onResume() {
        super.onResume()
        setChartView()
        Log.i("asdstatistics","onResume()!!")
    }
    private fun setChartView() {
        val chartWeek = binding.chartWeek
        Log.i("asd","in setchartView")
        setWeek(chartWeek)
    }

    private fun initBarDataSet(barDataSet: BarDataSet) {
        Log.i("asd","in initBarDataSet")
        //Changing the color of the bar
        barDataSet.color = Color.parseColor("#3CB371")
        //Setting the size of the form in the legend
        barDataSet.formSize = 15f
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(true)
        //setting the text size of the value of the bar
        barDataSet.valueTextSize = 12f
        barDataSet.valueFormatter=DecimalValueFormatter()

    }

    private fun setWeek(barChart: BarChart) {
        Log.i("asd","in setWeek")

        initBarChart(barChart)

        barChart.setScaleEnabled(false) //Zoom In/Out

        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "걸음 수"

        for(step in analysisData.stepData){
            val barEntry = BarEntry((LocalDate.parse(step.date).dayOfYear-1).toFloat(), step.stepCount.toDouble().toFloat())
            Log.i("asd ",LocalDate.parse(step.date).toString()+"::"+step.stepCount)
            entries.add(barEntry)
        }
        //input data

        val barDataSet = BarDataSet(entries, title)
        initBarDataSet(barDataSet)
        val data = BarData(barDataSet)
        barChart.data = data
        barChart.invalidate()
    }

    private fun initBarChart(barChart: BarChart) {
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false)
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false)
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false)

        //remove the description label text located at the lower right corner
        val description = Description()
        description.isEnabled = false

        //X, Y 바의 애니메이션 효과
        barChart.animateY(1000)
        barChart.animateX(1000)


        //바텀 좌표 값
        val xAxis: XAxis = barChart.xAxis
        //change the position of x-axis to the bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //set the horizontal distance of the grid line
        xAxis.granularity = 0.85f
        xAxis.textColor = Color.RED
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false)

        xAxis.valueFormatter= DateAxisValuewFormat()
        xAxis.setDrawLabels(true)
        xAxis.axisMinimum = (LocalDate.now().dayOfYear-8).toFloat()
        xAxis.axisMaximum = (LocalDate.now().dayOfYear).toFloat()
        Log.i("asdLocaldate",(LocalDate.now().dayOfYear-1).toString())
        //좌측 값 hiding the left y-axis line, default true if not set
        val leftAxis: YAxis = barChart.getAxisLeft()
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.RED
        leftAxis.isEnabled=false

        //우측 값 hiding the right y-axis line, default true if not set
        val rightAxis: YAxis = barChart.getAxisRight()
        rightAxis.setDrawAxisLine(false)
        rightAxis.textColor = Color.RED
        rightAxis.isEnabled=false


        //바차트의 타이틀
        val legend: Legend = barChart.getLegend()
        //setting the shape of the legend form to line, default square shape
        legend.form = Legend.LegendForm.LINE
        //setting the text size of the legend
        legend.textSize = 30f
        legend.textColor = Color.rgb(16,153,114)
        //setting the alignment of legend toward the chart
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        //setting the stacking direction of legend
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false)
    }
    private fun initAnalysisData() {
        val al = ArrayList<Step>()
        al.add(Step(0,0,0.0, LocalDate.now().toString()))
        val al3 = ArrayList<Walk>()
        al3.add(Walk(0.0,0.0))
        val ll= LocationList(al3)
        val al2 =  ArrayList<LocationList>()
        al2.add(ll)
        analysisData= AnalysisData(al, al2)
    }

    private fun bundleReciever() {
        requireActivity().supportFragmentManager.setFragmentResultListener("analysisData2",mainActivity
        ) { requestKey, result ->
            analysisData = result.getParcelable("analysisData2")!!
            Log.i("asdStatistics","bundle 받았습니다 ${analysisData.stepData[0].toString()}")
        }
    }


}