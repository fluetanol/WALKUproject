package com.konkuk.walku.src.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivitySplashBinding
import com.konkuk.walku.src.login.LoginActivity
import com.konkuk.walku.src.main.MainActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate){

    // 스플래시 화면에서 텍스트에 애니메이션 효과를 주기 위한 Animation 객체입니다.
    private val textAppear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.text_appear
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.activitySplashMainTitleSubTextView.startAnimation(textAppear)

        // 2.5초 후에 자동으로 메인액티비티로 넘어갑니다. (부드럽게 넘어가는 애니메이션 적용)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }, 2500)

    }

}