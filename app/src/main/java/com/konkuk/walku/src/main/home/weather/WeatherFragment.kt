package com.konkuk.walku.src.main.home.weather

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentWeatherBinding
import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse
import com.konkuk.walku.src.main.home.weather.model.ModelWeather
import com.konkuk.walku.util.OpenApiCommon
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO 코드가 지저분하므로 깔끔하게 리팩토링할 예정 (...)

class WeatherFragment :
    BaseFragment<FragmentWeatherBinding>(FragmentWeatherBinding::bind, R.layout.fragment_weather),
    WeatherFragmentView {

    private var curPoint : Point? = null    // 현재 위치의 격자 좌표를 저장할 포인트 객체입니다.
    
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                interval = 60 * 10000   // 요청 간격(10초)
            }
            val locationCallback = object : LocationCallback() {
                // 요청 결과
                override fun onLocationResult(p0: LocationResult) {
                    p0.let {
                        for (location in it.locations) {

                            // 현재 위치의 위경도를 격자 좌표로 변환
                            curPoint = OpenApiCommon().dfsXyConv(location.latitude, location.longitude)

                            // 전역 변수에 저장
                            latitude = location.latitude
                            longitude = location.longitude

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

    @SuppressLint("SetTextI18n")
    override fun onGetWeatherSuccess(response: GetWeatherResponse) {
        // 테스트입니다.
        dismissLoadingDialog()
        showCustomToast("날씨 api 연동 성공")
        if(response.response.header.resultCode=="00") {
            val it = response.response.body.items.item
            // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
            val weatherArr = arrayOf(ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather())
            var index = 0
            val totalCount = response.response.body.totalCount - 1
            for (i in 0..totalCount) {
                index %= 6
                when(it[i].category) {
                    "PTY" -> weatherArr[index].rainType = it[i].fcstValue     // 강수 형태
                    "REH" -> weatherArr[index].humidity = it[i].fcstValue     // 습도
                    "SKY" -> weatherArr[index].sky = it[i].fcstValue          // 하늘 상태
                    "T1H" -> weatherArr[index].temp = it[i].fcstValue         // 기온
                }
                index++
            }

            binding.apply {
                // 현재 기온 설정
                fragmentWeatherCurrentWeatherTemperatureTextView.text = "${weatherArr[0].temp}°C"
                // 현재 하늘 상태 애니메이션 설정
                Glide.with(this@WeatherFragment).load(getWeatherImage(weatherArr[0].sky)).into(fragmentWeatherCurrentWeatherStateAnimView)
                // 현재 내 위치 주소 (한글 주소로 표시)
                // TODO 00구 00동 까지만 나오도록 변경할 예정
                fragmentWeatherAddressTextView.text = getAddress(latitude, longitude)
                fragmentWeatherCurrentWeatherStateTextView.text = getWeatherString(weatherArr[0].sky)
            }
        }
    }

    override fun onGetWeatherFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
        Log.d("okhttp", "$message")
    }


    private fun getWeatherImage(skyCode : String) : Int {
        // 하늘 상태
        return when(skyCode) {
            "1" -> R.drawable.sunny_anim                // 맑음
            "3" ->  R.drawable.medium_cloud_anim        // 구름 많음
            "4" -> R.drawable.heavy_cloud_anim          // 흐림
            else -> R.drawable.ic_launcher_foreground   // 오류
        }
    }

    private fun getWeatherString(skyCode: String) : String {
        return when(skyCode) {
            "1" -> "맑음"      
            "3" -> "구름 많음"
            "4" -> "흐림"
            else -> "알 수 없음"
        }
    }

    // 좌표 -> 주소 변환
    private fun getAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult = "주소를 가져 올 수 없습니다."
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
            if (address.size > 0) {
                // 주소 받아오기
                val currentLocationAddress = address[0].getAddressLine(0)
                    .toString()
                addressResult = currentLocationAddress
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressResult.replace("대한민국 ", "")
    }

}