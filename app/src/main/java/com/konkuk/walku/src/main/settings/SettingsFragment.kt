package com.konkuk.walku.src.main.settings

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.konkuk.walku.R
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_ACCOUNT
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_NAME
import com.konkuk.walku.config.ApplicationClass.Companion.K_USER_THUMB
import com.konkuk.walku.config.ApplicationClass.Companion.sSharedPreferences
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::bind, R.layout.fragment_settings),
    SettingsFragmentView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            Glide.with(this@SettingsFragment)
                .load(sSharedPreferences.getString(K_USER_THUMB, null))
                .circleCrop()
                .into(fragmentSettingsProfileImage)
            fragmentSettingsNameTextView.text = sSharedPreferences.getString(K_USER_NAME, null)
            fragmentSettingsEmailTextView.text = spiltUserIdFromEmail(sSharedPreferences.getString(K_USER_ACCOUNT, null))
        }

    }

    private fun spiltUserIdFromEmail(email: String?): String {
        val index = email?.indexOf("@")
        return email!!.substring(0, index!!)
    }

}