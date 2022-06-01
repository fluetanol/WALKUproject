package com.konkuk.walku.src.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivitySplashBinding
import com.konkuk.walku.src.main.MainActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }, 2500)

    }

}