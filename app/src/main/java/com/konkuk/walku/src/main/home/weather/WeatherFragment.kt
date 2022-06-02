package com.konkuk.walku.src.main.home.weather

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.internal.service.Common
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentWeatherBinding
import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse
import com.konkuk.walku.util.OpenApiCommon
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// 코드가 자고 일어나서 지저분하므로 깔끔하게 변경할 예정 (...)

class WeatherFragment :
    BaseFragment<FragmentWeatherBinding>(FragmentWeatherBinding::bind, R.layout.fragment_weather),
    WeatherFragmentView {

    private var curPoint : Point? = null    // 현재 위치의 격자 좌표를 저장할 포인트 객체입니다.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get permission
        val permissionList = arrayOf(
            // 위치 권한
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        // 권한 요청
        ActivityCompat.requestPermissions(requireActivity(), permissionList, 1)

        requestLocation()

        showLoadingDialog(requireActivity())

    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        try {
            // 나의 현재 위치 요청
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60 * 1000    // 요청 간격(1초)
            }
            val locationCallback = object : LocationCallback() {
                // 요청 결과
                override fun onLocationResult(p0: LocationResult) {
                    p0.let {
                        for (location in it.locations) {


                            // 현재 위치의 위경도를 격자 좌표로 변환
                            curPoint = OpenApiCommon().dfsXyConv(location.latitude, location.longitude)
                            
                            // 기상청 레트로핏 서비스 호출
                            WeatherService(this@WeatherFragment).tryGetWeather(
                                numOfRows = 300,
                                pageNo = 1,
                                base_date = getBaseDate(),
                                base_time = OpenApiCommon().getBaseTime(getBaseTimeHour(), getBaseTimeMinutes()),
                                nx = curPoint!!.x,
                                ny = curPoint!!.y
                            )
                        }
                    }
                }
            }

            // 내 위치 실시간으로 감지
            Looper.myLooper()?.let {
                locationClient.requestLocationUpdates(locationRequest, locationCallback,
                    it)
            }


        } catch (e : SecurityException) {
            e.printStackTrace()
        }
    }

    private fun getBaseDate(): String {
        val currentTime = LocalDateTime.now()
        val formatterForBaseDate = DateTimeFormatter.ofPattern("yyyyMMdd")
        return currentTime.format(formatterForBaseDate)
    }

    private fun getBaseTimeHour(): String {
        val currentTime = LocalDateTime.now()
        val formatterForBaseTimeHour = DateTimeFormatter.ofPattern("HH")
        return currentTime.format(formatterForBaseTimeHour)
    }

    private fun getBaseTimeMinutes(): String {
        val currentTime = LocalDateTime.now()
        val formatterForBaseTimeMinutes = DateTimeFormatter.ofPattern("mm")
        return currentTime.format(formatterForBaseTimeMinutes)
    }

    override fun onGetWeatherSuccess(response: GetWeatherResponse) {
        // 테스트입니다.
        dismissLoadingDialog()

        showCustomToast("날씨 api 연동 성공")
    }

    override fun onGetWeatherFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }

}