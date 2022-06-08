package com.konkuk.walku.src.main.analysis.recordmap

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.UiThread
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentRecordMapBinding
import com.konkuk.walku.databinding.FragmentStatisticsBinding
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource

class RecordMapFragment : BaseFragment<FragmentRecordMapBinding>(FragmentRecordMapBinding::bind, R.layout.fragment_record_map) {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val mapView = binding.fragmentMapMapView

        mapView.getMapAsync {
            it.locationSource = locationSource
            it.locationTrackingMode = LocationTrackingMode.Follow

            it.addOnLocationChangeListener { location ->
                //Log.i("asd","${location.latitude}, ${location.longitude}")
            }
            naverMap = it
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


}