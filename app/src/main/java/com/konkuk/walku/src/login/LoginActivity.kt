package com.konkuk.walku.src.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityLoginBinding
import com.konkuk.walku.src.login.adapter.LoginAdapter
import com.konkuk.walku.src.main.MainActivity
import com.konkuk.walku.src.permission.PermissionActivity
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    lateinit var viewPager: ViewPager2
    lateinit var dotsIndicator: WormDotsIndicator
    lateinit var adapter: LoginAdapter

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
            // TODO 카카오로그인 bottomDialogSheet 띄울 것
            // 시작하기 버튼 누르면 권한액티비티로 이동합니다. (임시)
            activityLoginStartLayout.setOnClickListener {
                startActivity(Intent(this@LoginActivity, PermissionActivity::class.java))
                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
            }
        }

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
                if(position==viewpagerImages.size-1) {
                    with(binding) {
                        activityLoginSkipAllTextView.visibility = View.GONE
                        with(activityLoginStartLayout) {
                            visibility = View.VISIBLE
                            startAnimation(buttonAppear)
                        }
                    }
                }
                else {
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