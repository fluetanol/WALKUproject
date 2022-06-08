package com.konkuk.walku.src.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.konkuk.walku.R
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_ACCOUNT
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_NAME
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_THUMB
import com.konkuk.walku.config.ApplicationClass.Companion.sSharedPreferences
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityLoginBinding
import com.konkuk.walku.src.login.adapter.LoginAdapter
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.permission.PermissionActivity
import com.konkuk.walku.util.LoginBottomSheetDialog
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.coroutines.*

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    lateinit var viewPager: ViewPager2
    lateinit var dotsIndicator: WormDotsIndicator
    lateinit var adapter: LoginAdapter
    lateinit var editor: SharedPreferences.Editor
    private val TAG = LoginActivity::class.java.simpleName

    private var viewpagerImages = mutableListOf<Int>()

    // 슬라이딩 마지막 화면에서 시작하기 버튼에 애니메이션 효과를 주기 위한 Animation 객체입니다.
    private val buttonAppear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.button_appear
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰페이저 초기 설정 함수입니다.
        setViewpager()

        binding.apply {
            // 건너뛰기 버튼을 누르면 뷰페이저2 마지막 슬라이드로 이동합니다.
            activityLoginSkipAllTextView.setOnClickListener {
                viewPager.currentItem = viewpagerImages.size - 1
            }
            activityLoginStartLayout.setOnClickListener {

                val loginBottomSheetDialog = LoginBottomSheetDialog {
                    when (it) {
                        0 -> {
                            showCustomToast("구글 로그인")
                        }
                        1 -> {
                            kakaoLogin()
                        }
                        2 -> {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    PermissionActivity::class.java
                                )
                            )
                            overridePendingTransition(
                                R.anim.activity_fade_in,
                                R.anim.activity_fade_out
                            )
                        }
                    }
                }
                loginBottomSheetDialog.show(supportFragmentManager, loginBottomSheetDialog.tag)
            }
        }

    }

    private fun kakaoLogin() {
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                showCustomToast("카카오계정으로 로그인 실패")
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                showCustomToast("카카오계정으로 로그인 성공")
                loginMain()
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    showCustomToast("카카오톡으로 로그인 실패")

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        this@LoginActivity,
                        callback = callback
                    )
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    showCustomToast("카카오톡으로 로그인 성공")
                    loginMain()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
        }
    }

    private fun loginMain() {
        runBlocking {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    showCustomToast("사용자 정보 요청 실패")
                } else if (user != null) {
                    showCustomToast("사용자 정보 요청 성공")
                    showCustomToast("${user.kakaoAccount?.profile?.nickname}님 환영합니다!")
                    editor = sSharedPreferences.edit()
                    editor.putString(K_USER_NAME, user.kakaoAccount?.profile?.nickname)
                    editor.putString(K_USER_ACCOUNT, user.kakaoAccount?.email)
                    //user?.kakaoAccount?.profile?.profileImageUrl
                    editor.putString(K_USER_THUMB, user.kakaoAccount?.profile?.thumbnailImageUrl)
                    editor.apply()
                }
            }
        }
        val intent = Intent(this, PermissionActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
    }

    private fun setViewpager() {
        viewpagerImages = loadImages()
        binding.apply {
            dotsIndicator = activityLoginDotsIndicator
            viewPager = activityLoginViewPager
        }
        adapter = LoginAdapter(this, viewpagerImages)
        viewPager.adapter = adapter
        val pageCallBack = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == viewpagerImages.size - 1) {
                    with(binding) {
                        activityLoginSkipAllTextView.visibility = View.GONE
                        with(activityLoginStartLayout) {
                            visibility = View.VISIBLE
                            startAnimation(buttonAppear)
                        }
                    }
                } else {
                    with(binding) {
                        activityLoginSkipAllTextView.visibility = View.VISIBLE
                        activityLoginStartLayout.visibility = View.GONE
                    }
                }
            }
        }
        viewPager.registerOnPageChangeCallback(pageCallBack)
        dotsIndicator.setViewPager2(viewPager)
        binding.activityLoginViewPager.adapter = adapter
    }

    // TODO 앱을 설명할 수 있는 슬라이드 이미지 2장 이상 추가하기
    // 아직 슬라이드 이미지 제작을 하나밖에 못해서 임시로 첫 번째 사진만 넣었습니다.
    private fun loadImages(): MutableList<Int> {
        return arrayListOf(
            R.drawable.walku_main_template_1,
            R.drawable.walku_main_template_1,
            R.drawable.walku_main_template_1,
        )
    }

}