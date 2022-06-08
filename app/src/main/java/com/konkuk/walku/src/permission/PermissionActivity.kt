package com.konkuk.walku.src.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivityForPermission
import com.konkuk.walku.databinding.ActivityPermissionBinding
import com.konkuk.walku.src.main.MainActivity

class PermissionActivity : BaseActivityForPermission<ActivityPermissionBinding>(ActivityPermissionBinding::inflate){

    // 위치 권한 배열입니다.
    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 모든 권한이 설정되었는지 확인하는 변수
        val isAllPermissionGranted = permissionList.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }

        // 권한 설정이 안되었을 때에만 권한 액티비티가 보이도록
        if(!isAllPermissionGranted) {
            binding.activityPermissionCheckButton.setOnClickListener {
                requirePermissions(
                    permissionList, PERMISSION_REQUEST_CODE
                )
            }

        }

        // 권한 설정이 최초에 한 번 완료되었다면, 로그인 액티비티(뷰페이저 슬라이드)에서 권한 액티비티가 아니라 메인 액티비티로 바로 이동합니다.
        else {
            requirePermissions(
                permissionList, PERMISSION_REQUEST_CODE
            )
        }

    }

    override fun permissionGranted(requestCode: Int) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val intent = Intent(this@PermissionActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }
    }

    override fun permissionDenied(requestCode: Int) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val intent = Intent(this@PermissionActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 99
    }

}