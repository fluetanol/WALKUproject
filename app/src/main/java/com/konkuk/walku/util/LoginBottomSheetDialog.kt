package com.konkuk.walku.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.walku.R

class LoginBottomSheetDialog(val itemClick: (Int) -> Unit) : BottomSheetDialogFragment() {
    lateinit var googleLoginLayout : LinearLayout
    lateinit var kakaoLoginLayout : LinearLayout
    lateinit var noLoginLayout : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 구글 로그인
        googleLoginLayout = view.findViewById(R.id.login_bottom_sheet_dialog_google_layout)
        googleLoginLayout.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }
        // 카카오 로그인
        kakaoLoginLayout = view.findViewById(R.id.login_bottom_sheet_dialog_kakao_layout)
        kakaoLoginLayout.setOnClickListener {
            itemClick(1)
            dialog?.dismiss()
        }
        // 로그인 전 둘러보기
        noLoginLayout = view.findViewById(R.id.login_bottom_sheet_dialog_no_login_layout)
        noLoginLayout.setOnClickListener {
            itemClick(2)
            dialog?.dismiss()
        }
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}