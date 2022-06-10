package com.konkuk.walku.src.main.home.map.walkMap

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityWalkMapBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.home.map.walkMap.model.GetWalkResponse
import com.konkuk.walku.src.main.home.map.walkMap.model.ModelPark
import com.konkuk.walku.util.ParkBottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage


class WalkMapActivity : BaseActivity<ActivityWalkMapBinding>(ActivityWalkMapBinding::inflate),
    OnMapReadyCallback, WalkMapActivityView {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private val parkArr = mutableListOf<ModelPark>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = binding.activityWalkMapMapView
        mapView.onCreate(savedInstanceState)
        WalkMapService(this).tryGetWalk()
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
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
    }

    override fun onMapReady(map: NaverMap) {
        Handler(Looper.getMainLooper()).postDelayed({
            naverMap = map
            // 카메라 초기 위치 설정
            val initialPosition = LatLng(37.506855, 127.066242)
            val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
            naverMap.moveCamera(cameraUpdate)

            Log.d("okhttp", "onMapReady $parkArr")

            // UI Setting
            val uiSetting = naverMap.uiSettings
            uiSetting.isLocationButtonEnabled = false // 현위치 버튼 비활성화
            uiSetting.isZoomControlEnabled = false // 줌 버튼 비활성화

            // 마커를 클릭하면:
            val listener = Overlay.OnClickListener { overlay ->
                val marker = overlay as Marker

                val parkBottomSheetDialog = ParkBottomSheetDialog(marker.tag.toString().toInt(), parkArr) {
                    when (it) {
                        0 -> {}
                        1 -> {}
                    }
                }
                parkBottomSheetDialog.show(supportFragmentManager, parkBottomSheetDialog.tag)

                true
            }

            // 마커 리스트
            parkArr.forEach {
                val marker = Marker()
                if (it.parkLatitude != "" && it.parkLongitude != "") {
                    marker.position = LatLng(it.parkLatitude.toDouble(), it.parkLongitude.toDouble())
                    marker.map = naverMap
                    marker.icon = OverlayImage.fromResource(R.drawable.park_icon)
                    marker.width = 100
                    marker.height = 100
                    marker.isIconPerspectiveEnabled = true
                    marker.tag = it.parkTag
//                    marker.captionText = it.parkName
//                    marker.captionOffset = 15
//                    marker.captionMinZoom = 12.0
//                    marker.captionMaxZoom = 16.0
                    marker.onClickListener = listener
                }
            }
        }, 100)
    }

    override fun onGetWalkSuccess(response: GetWalkResponse) {
        dismissLoadingDialog()
        if (response.SearchParkInfoService.RESULT.CODE == "INFO-000") {
//            showCustomToast("공원 API 연결 성공")
            response.SearchParkInfoService.row.forEach {
                parkArr.add(
                    ModelPark(
                        parkTag = it.P_IDX,
                        parkName = it.P_PARK,
                        parkDescription = it.P_LIST_CONTENT,
                        parkMainEquip = it.MAIN_EQUIP,
                        parkImage = it.P_IMG,
                        parkZone = it.P_ZONE,
                        parkAddress = it.P_ADDR,
                        parkLongitude = it.LONGITUDE,
                        parkLatitude = it.LATITUDE,
                        parkArea = it.AREA,
                        parkOpenDate = it.OPEN_DT,
                        parkNumber = it.P_ADMINTEL,
                        parkRoad = it.VISIT_ROAD
                    )
                )
            }
            Log.d("okhttp", "$parkArr")
            mapView.getMapAsync(this)
        }
    }

    override fun onGetWalkFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }

}