package com.konkuk.walku.src.main.challenge

import android.os.Bundle
import android.view.View
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseFragment
import com.konkuk.walku.databinding.FragmentChallengeBinding

class ChallengeFragment : BaseFragment<FragmentChallengeBinding>(FragmentChallengeBinding::bind, R.layout.fragment_challenge),
    ChallengeFragmentView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}