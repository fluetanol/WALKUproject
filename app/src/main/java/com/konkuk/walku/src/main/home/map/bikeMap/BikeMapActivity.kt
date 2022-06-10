package com.konkuk.walku.src.main.home.map.bikeMap

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityBikeMapBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.home.map.bikeMap.model.GetBikeResponse
import com.konkuk.walku.src.main.home.map.bikeMap.model.ModelBike
import com.konkuk.walku.src.main.home.map.walkMap.model.ModelPark
import com.konkuk.walku.util.BikeBottomSheetDialog
import com.konkuk.walku.util.ParkBottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

class BikeMapActivity : BaseActivity<ActivityBikeMapBinding>(ActivityBikeMapBinding::inflate),
    BikeMapActivityView, OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private val bikeArr = mutableMapOf<String, ModelBike>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = binding.activityBikeMapMapView
        mapView.onCreate(savedInstanceState)
        BikeMapService(this).tryGetWalk(1, 1000)
//        BikeMapService(this).tryGetWalk(1001, 2000)
//        BikeMapService(this).tryGetWalk(2001, 3000)
        showLoadingDialog(this)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
    }

    override fun onGetBikeSuccess(response: GetBikeResponse) {
        dismissLoadingDialog()
        if (response.rentBikeStatus.RESULT.CODE=="INFO-000") {
//            showCustomToast("따릉이 API 연결 성공")
            response.rentBikeStatus.row.forEach {
                bikeArr[it.stationId] = ModelBike(
                    rackTotalCount = it.rackTotCnt,
                    stationName = it.stationName,
                    parkingBikeTotalCount = it.parkingBikeTotCnt,
                    shared = it.shared,
                    stationLatitude = it.stationLatitude,
                    stationLongitude = it.stationLongitude
                )
            }
            Log.d("okhttp", "$bikeArr")
            mapView.getMapAsync(this)
        }
    }

    override fun onGetBikeFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }

    override fun onMapReady(map: NaverMap) {
        Handler(Looper.getMainLooper()).postDelayed({
            naverMap = map
            // 카메라 초기 위치 설정
            val initialPosition = LatLng(37.542454, 127.076806)
            val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
            naverMap.moveCamera(cameraUpdate)

            // UI Setting
            val uiSetting = naverMap.uiSettings
            uiSetting.isLocationButtonEnabled = false // 현위치 버튼 비활성화
            uiSetting.isZoomControlEnabled = false // 줌 버튼 비활성화

            // 마커를 클릭하면:
            val listener = Overlay.OnClickListener { overlay ->
                val marker = overlay as Marker
                val bikeBottomSheetDialog = BikeBottomSheetDialog(marker.tag.toString(), bikeArr) {
                    when (it) {
                        0 -> {}
                        1 -> {}
                    }
                }
                bikeBottomSheetDialog.show(supportFragmentManager, bikeBottomSheetDialog.tag)
                true
            }

            // 마커 리스트
            bikeArr.forEach {
                val marker = Marker()
                if (it.value.rackTotalCount != "" &&
                    it.value.stationName != "" &&
                    it.value.shared != "" &&
                    it.value.parkingBikeTotalCount != "" &&
                    it.value.stationLatitude != "" &&
                    it.value.stationLongitude != ""
                ) {
                    marker.position =
                        LatLng(it.value.stationLatitude.toDouble(), it.value.stationLongitude.toDouble())
                    marker.map = naverMap

                    when(it.value.shared.toInt()) {
                        in 0..30 -> marker.icon = OverlayImage.fromResource(R.drawable.bike_low_mark)
                        in 30..60 -> marker.icon = OverlayImage.fromResource(R.drawable.bike_medium_marker)
                        in 60..1000-> marker.icon = OverlayImage.fromResource(R.drawable.bike_many_marker)
                    }

                    marker.width = 130
                    marker.height = 130
                    marker.isIconPerspectiveEnabled = true
                    marker.tag = it.key
                    marker.onClickListener = listener
                }
            }
        }, 100)
    }
}