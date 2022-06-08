package com.konkuk.walku.src.main.home.weather

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentWeatherBinding
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.main.home.weather.model.GetWeatherResponse
import com.konkuk.walku.src.main.home.weather.model.ModelWeather
import com.konkuk.walku.util.OpenApiCommon
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO 코드가 지저분하므로 깔끔하게 리팩토링할 예정 (...)

// 기상청 단기예보 API 규칙 정리
// TMX 값은 fcstTime 1500 일때에만 받아옴.


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
                                numOfRows = 1000,
                                pageNo = 1,
                                base_date = getBaseDate(getBaseTimeHour(), getBaseTimeMinutes()),
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


    private fun getBaseDate(h: String, m: String): String {
        val cal = Calendar.getInstance()
        return if (h == "00" && OpenApiCommon().getBaseTime(h, m) == "2330") {
            cal.add(Calendar.DATE, -1)
            SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        } else SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
    }

    private fun getBaseTimeHour(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, -3)
        return SimpleDateFormat("HH", Locale.getDefault()).format(cal.time)
    }

    private fun getBaseTimeMinutes(): String {
        val currentTime = LocalDateTime.now()
        val formatterForBaseTimeMinutes = DateTimeFormatter.ofPattern("mm")
        return currentTime.format(formatterForBaseTimeMinutes)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetWeatherSuccess(response: GetWeatherResponse) {
        dismissLoadingDialog()
        Log.d("okhttp", "onGetWeatherSuccess")
        showCustomToast("날씨 api 연동 성공")
        if(response.response.header.resultCode=="00") {
            val it = response.response.body.items.item
            // 현재 시각부터 1시간 뒤 마다의 날씨 100개를 담을 배열 (100개는 넉넉하게 만들었습니다.)
            val weatherArr = mutableListOf<ModelWeather>()
            for (i in 0..100) {
                weatherArr.add(ModelWeather())
            }
            var index = 0
            val totalCount = response.response.body.totalCount - 1
            for (i in 0..totalCount) {
                when(it[i].category) {
                    "TMP" -> weatherArr[index].temp = it[i].fcstValue         // 기온
                    "SKY" -> weatherArr[index].sky = it[i].fcstValue          // 하늘 상태
                    "PTY" -> weatherArr[index].rainType = it[i].fcstValue     // 강수 형태
                    "REH" -> weatherArr[index].humidity = it[i].fcstValue     // 습도
                    "POP" -> weatherArr[index].rainPossibility = it[i].fcstValue // 강수 확률
                    "TMX" -> {
                        weatherArr[index].maxTemp = it[i].fcstValue
                        index++
                        continue
                    }
                    "TMN" -> {
                        weatherArr[index].minTemp = it[i].fcstValue
                        index++
                        continue
                    }
                    "SNO" -> {
                        index++
                    }
                }
            }

            Log.d("okhttp", "$weatherArr")

            binding.apply {
                // 현재 기온 설정
                fragmentWeatherCurrentWeatherTemperatureTextView.text = "${weatherArr[0].temp}°"
                // 현재 하늘 상태 애니메이션 설정
                Glide.with(this@WeatherFragment).load(getWeatherImage(weatherArr[0].sky)).into(fragmentWeatherCurrentWeatherStateAnimView)
                // 현재 내 위치 주소 (한글 주소로 표시)
                // TODO 00구 00동 까지만 나오도록 변경할 예정
                fragmentWeatherAddressTextView.text = getAddress(latitude, longitude)
                fragmentWeatherCurrentWeatherStateTextView.visibility = View.VISIBLE
                fragmentWeatherCurrentPossibilityRainTextView.visibility = View.VISIBLE
                fragmentWeatherCurrentPossibilityRainValueTextView.text = "${weatherArr[0].rainPossibility}%"
                fragmentWeatherCurrentWeatherStateValueTextView.text = getWeatherString(weatherArr[0].sky)
                with(weatherArr) {
                    forEach {
                        if(it.maxTemp!="") {
                            fragmentWeatherMaxTempIcon.visibility = View.VISIBLE
                            fragmentWeatherMaxTempTextView.text = "${it.maxTemp.toDouble().toInt()}°"
                        }
                    }
                    forEach {
                        if (it.minTemp != "") {
                            fragmentWeatherMinTempIcon.visibility = View.VISIBLE
                            fragmentWeatherMinTempTextView.text = "${it.minTemp.toDouble().toInt()}°"
                        }
                    }
                }
                fragmentWeatherHumidityTitleTextView.visibility = View.VISIBLE
                fragmentWeatherHumidityTitleTextValueView.text = "${weatherArr[0].humidity}%"
                fragmentWeatherHumidityProgressView.progress = weatherArr[0].humidity.toFloat()
                fragmentWeatherHumidityProgressView.animate()

                // 알파값 조정으로 fade in 구현하였습니다.
                ObjectAnimator.ofFloat(this.fragmentWeatherIsItGoodToGoOutsideLayout, View.ALPHA, 0f,1f).apply {
                    duration = 2000
                    start()
                }



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
        val endIndex = addressResult.indexOf("구 ")
        Log.d("okhttp", "$endIndex")
        Log.d("okhttp", "${addressResult.replace("대한민국 ", "").substring(0, endIndex)}")
        return addressResult.substring(0, endIndex+1).replace("대한민국", "")
    }

}