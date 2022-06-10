package com.konkuk.walku.util

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.walku.R
import com.konkuk.walku.src.main.home.map.bikeMap.model.ModelBike

class BikeBottomSheetDialog(private val key: String, private val bikeArr: MutableMap<String, ModelBike>, val itemClick: (Int) -> Unit) : BottomSheetDialogFragment() {

    lateinit var lowLayout : ConstraintLayout
    lateinit var mediumLayout : ConstraintLayout
    lateinit var highLayout : ConstraintLayout
    lateinit var stationTextView : TextView
    lateinit var howManyTextView : TextView
    lateinit var howLeftTextView : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bike_bottom_sheet_dialog, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lowLayout = view.findViewById(R.id.bike_bottom_sheet_low_layout)
        mediumLayout = view.findViewById(R.id.bike_bottom_sheet_medium_layout)
        highLayout = view.findViewById(R.id.bike_bottom_sheet_high_layout)
        stationTextView = view.findViewById(R.id.bike_bottom_sheet_station_name_text_view)
        howManyTextView = view.findViewById(R.id.bike_bottom_sheet_dialog_how_many_text_view)
        howLeftTextView = view.findViewById(R.id.bike_bottom_sheet_dialog_left_value_text_view)

        stationTextView.text = bikeArr[key]?.stationName
        howLeftTextView.text = "따릉이가 ${bikeArr[key]?.parkingBikeTotalCount}대 남았어요!"

        when(bikeArr[key]?.shared?.toInt()) {
            in 0..30 -> {
                lowLayout.visibility = View.VISIBLE
                mediumLayout.visibility = View.INVISIBLE
                highLayout.visibility = View.INVISIBLE
                howManyTextView.text = "부족"
            }
            in 30..60 -> {
                lowLayout.visibility = View.INVISIBLE
                mediumLayout.visibility = View.VISIBLE
                highLayout.visibility = View.INVISIBLE
                howManyTextView.text = "보통"
            }
            in 60..1000-> {
                lowLayout.visibility = View.INVISIBLE
                mediumLayout.visibility = View.INVISIBLE
                highLayout.visibility = View.VISIBLE
                howManyTextView.text = "충분"
            }
        }

    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}