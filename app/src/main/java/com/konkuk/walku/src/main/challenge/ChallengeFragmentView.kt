package com.konkuk.walku.src.main.challenge

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

interface ChallengeFragmentView {
    companion object{
        const val FLAG_SUCCESS_CHALLENGELIST =0
        const val FLAG_NEW_CHALLENGELIST = 1
        const val FLAG_MY_CHALLENGELIST = 2
    }


    //그냥 챌린지 리스트 리사이클러뷰 꾸미려고 만들어 놓은 클래스, 너무 딱 달라붙어있을때 위아래로 간격 두는 작업함
    //스타일을 적용시키고 싶은 리사이클러 뷰에
    //        val decoration = RecyclerDecoadpater()
    //        binding.recyclerview.addItemDecoration(decoration)
    //를 넣으면 됨.
    class RecyclerDecoadpater : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildAdapterPosition(view)
            val count = state.itemCount
            val offset =10
            outRect.top = offset
            outRect.bottom = offset
        }
    }

    //challenge프레그먼트 초기화 작업
    fun challengeinit()


}