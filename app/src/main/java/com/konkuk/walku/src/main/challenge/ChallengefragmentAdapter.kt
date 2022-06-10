package com.konkuk.walku.src.main.analysis

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.konkuk.walku.src.main.challenge.littleFragment.myChallengeWindow.ChallengeMyChallengeFragment
import com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow.ChallengeNewChallengeFragment
import com.konkuk.walku.src.main.challenge.littleFragment.successChallengeWindow.ChallengeSuccessChallengeFragment

class ChallengefragmentAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    var fragmentList = listOf<Fragment>()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        /*
        when (position){
            0->return ChallengeMyChallengeFragment()
            1->return ChallengeNewChallengeFragment()
            2->return ChallengeSuccessChallengeFragment()
            else->{return ChallengeMyChallengeFragment()}
        }*/
        return fragmentList[position]
    }

}