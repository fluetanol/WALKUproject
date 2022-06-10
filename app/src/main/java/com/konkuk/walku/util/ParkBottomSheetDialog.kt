package com.konkuk.walku.util

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.walku.R
import com.konkuk.walku.src.main.home.map.walkMap.model.ModelPark

class ParkBottomSheetDialog(private val itemId: Int, private val parkArr: MutableList<ModelPark>, val itemClick: (Int) -> Unit) : BottomSheetDialogFragment() {

    lateinit var parkImageView : AppCompatImageView
    lateinit var titleTextView : TextView
    lateinit var addressTextView : TextView
    lateinit var areaAndOpenDateTextView : TextView
    lateinit var tellTextView : TextView
    lateinit var descriptionTextView : TextView
    lateinit var equipTextView : TextView
    lateinit var roadTextView : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.park_bottom_sheet_dialog, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parkImageView = view.findViewById(R.id.park_bottom_sheet_dialog_image_view)
        titleTextView = view.findViewById(R.id.park_bottom_sheet_dialog_title_text_view)
        addressTextView = view.findViewById(R.id.park_bottom_sheet_dialog_address_text_view)
        areaAndOpenDateTextView = view.findViewById(R.id.park_bottom_sheet_dialog_area_and_open_date_text_view)
        tellTextView = view.findViewById(R.id.park_bottom_sheet_dialog_tell_text_view)
        descriptionTextView = view.findViewById(R.id.park_bottom_sheet_dialog_description_content_text_view)
        equipTextView = view.findViewById(R.id.park_bottom_sheet_dialog_equip_content_text_view)
        roadTextView = view.findViewById(R.id.park_bottom_sheet_dialog_road_content_text_view)

        Glide.with(view).load(parkArr[itemId-1].parkImage).placeholder(R.drawable.placeholder).into(parkImageView)
        titleTextView.text = parkArr[itemId-1].parkName
        addressTextView.text = parkArr[itemId-1].parkAddress
        areaAndOpenDateTextView.text = "${parkArr[itemId-1].parkArea}  ·  ${parkArr[itemId-1].parkOpenDate} 개원"
        tellTextView.text = parkArr[itemId-1].parkNumber
        descriptionTextView.text = if(parkArr[itemId-1].parkDescription=="") {
            "정보가 존재하지 않습니다."
        } else {
            parkArr[itemId-1].parkDescription
        }
        equipTextView.text = if(parkArr[itemId-1].parkMainEquip=="") {
            "정보가 존재하지 않습니다."
        } else {
            parkArr[itemId-1].parkDescription
        }
        roadTextView.text = if(parkArr[itemId-1].parkRoad=="") {
            "정보가 존재하지 않습니다."
        } else {
            parkArr[itemId-1].parkDescription
        }
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}