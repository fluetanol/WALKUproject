package com.konkuk.walku.src.main.home.map.walkMap

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityWalkMapSplashBinding

@SuppressLint("CustomSplashScreen")
class WalkMapSplashActivity : BaseActivity<ActivityWalkMapSplashBinding>(ActivityWalkMapSplashBinding::inflate){

    // 스플래시 화면에서 텍스트에 애니메이션 효과를 주기 위한 Animation 객체입니다.
    private val textAppear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.text_appear
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            supportActionBar?.hide()
        }

        binding.apply {
            activityWalkMapSplashTitleTextView.startAnimation(textAppear)
            ViewCompat.setTransitionName(activityWalkMapSplashBackgroundImage,"background_image")
        }

        // 2.5초 후에 자동으로 산책로 맵 액티비티로 넘어갑니다. (부드럽게 넘어가는 애니메이션 적용)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, WalkMapActivity::class.java))
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }, 2500)
    }

    override fun onBackPressed() {
        /* 뒤로가기 버튼 막아둠 */
    }

}