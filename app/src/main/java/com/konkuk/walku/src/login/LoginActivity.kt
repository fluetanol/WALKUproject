package com.konkuk.walku.src.login

import android.os.Bundle
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityLoginBinding

class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    val viewpagerImages = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    private fun loadImages(): ArrayList<Int> {
        return arrayListOf(

        )
    }


}