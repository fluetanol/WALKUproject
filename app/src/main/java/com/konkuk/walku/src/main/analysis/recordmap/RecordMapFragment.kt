package com.konkuk.walku.src.main.analysis.recordmap

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentRecordMapBinding
import com.konkuk.walku.databinding.FragmentStatisticsBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.analysis.model.LocationList
import com.konkuk.walku.src.main.analysis.model.Walk
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import java.util.ResourceBundle.getBundle

class RecordMapFragment : BaseFragment<FragmentRecordMapBinding>(FragmentRecordMapBinding::bind, R.layout.fragment_record_map) {
    lateinit var rdb: DatabaseReference
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    lateinit var mainActivity: MainActivity
    lateinit var walkList : ArrayList<LocationList>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locList = ArrayList<Walk>()
        locList.add(Walk(37.5410,127.0793))
        locList.add(Walk(37.5418,127.0753))
        locList.add(Walk(37.5400,127.0773))
        locList.add(Walk(37.5438,127.0723))
        walkList =ArrayList<LocationList>()
        getBundle()
        //insertDB(locList)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundle()
        init()
    }

    override fun onResume() {
        super.onResume()
        getBundle()
        init()
    }
    private fun getBundle() {
        requireActivity().supportFragmentManager.setFragmentResultListener("walkData",mainActivity
        ) { _, result ->
            walkList= result.getParcelableArrayList("walkData")!!
            Log.i("asd recordMap", "get walkList!$walkList")
        }
    }

    private fun init(){
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val mapView = binding.fragmentMapMapView
        Log.i("asdmap",walkList.toString())
        mapView.getMapAsync {
            it.locationSource = locationSource
            it.locationTrackingMode = LocationTrackingMode.Follow

            it.addOnLocationChangeListener { location ->
                //Log.i("asd","${location.latitude}, ${location.longitude}")
            }
            naverMap = it

            val polyline =ArrayList<PolylineOverlay>()
            val startMarker =ArrayList<Marker>()
            val endMarker =ArrayList<Marker>()
            var index = 0
            for(list in walkList){
                val tmpList = ArrayList<LatLng>()
                for(lc in list.locationArrayList){
                    tmpList.add(LatLng(lc.latitude,lc.longitude))
                }
                if(tmpList.size>=2){
                    polyline.add(PolylineOverlay())
                    polyline[index].coords = tmpList.toList()
                    polyline[index].width=10
                    polyline[index].color= Color.rgb(16,163,114)
                    polyline[index].capType = PolylineOverlay.LineCap.Round
                    polyline[index].joinType = PolylineOverlay.LineJoin.Round
                    polyline[index].map = naverMap

                    startMarker.add(Marker())
                    startMarker[index].position = LatLng(polyline[index].coords[0].latitude,polyline[index].coords[0].longitude)
                    startMarker[index].captionText = "산책 시작"
                    startMarker[index].map = naverMap
                    endMarker.add(Marker())
                    val size = polyline[index].coords.size
                    endMarker[index].position = LatLng(polyline[index].coords[size-1].latitude,polyline[index].coords[size-1].longitude)
                    endMarker[index].captionText = "산책 끝"
                    endMarker[index].map = naverMap
                    index += 1
                }

            }
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