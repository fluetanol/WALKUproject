package com.konkuk.walku.config

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import com.konkuk.walku.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 인덱스별 API 정보
// API_URL_LIST
// 0번 인덱스 -> 기상청 초단기예보 API

// 앱이 실행될때 1번만 실행이 됩니다.
class ApplicationClass : Application() {

    // API를 여러 개 사용할 수도 있기 때문에 리스트로 변경하였습니다.

    // 기본 url만 작성해주세요.
    val API_URL_LIST = arrayListOf(
        "http://apis.data.go.kr/",
        "http://openAPI.seoul.go.kr:8080/"
    )

    // 코틀린의 전역변수 문법
    companion object {
        // 만들어져있는 SharedPreferences 를 사용해야합니다. 재생성하지 않도록 유념해주세요
        lateinit var sSharedPreferences: SharedPreferences

        val K_USER_NAME = "kakaoUserName"
        val K_USER_ACCOUNT = "kakaoUserAccount"
        val K_USER_THUMB = "kakaoUserThumbnailImageUrl"

        // JWT Token Header 키 값
        val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"

        // Retrofit 인스턴스, 앱 실행시 한번만 생성하여 사용합니다.
        val sRetrofit = mutableListOf<Retrofit>()
    }

    // 앱이 처음 생성되는 순간, SP를 새로 만들어주고, 레트로핏 인스턴스를 생성합니다.
    override fun onCreate() {
        super.onCreate()
        sSharedPreferences =
            applicationContext.getSharedPreferences("WALKU_APP", MODE_PRIVATE)
        // 레트로핏 인스턴스 생성
        initRetrofitInstance()

        // KakaoSDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }

    // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
    // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
    private fun initRetrofitInstance() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(XAccessTokenInterceptor()) // JWT 자동 헤더 전송
            .build()


        val gson = GsonBuilder().setLenient().create()

        // sRetrofit 이라는 전역변수에 API url, 인터셉터, Gson을 넣어주고 빌드해주는 코드
        // 이 전역변수로 http 요청을 서버로 보내면 됩니다.
        API_URL_LIST.forEach {
            sRetrofit.add(
                Retrofit.Builder()
                    .baseUrl(it)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            )
        }

    }

}