package com.konkuk.walku.src.main.home.map.walkMap

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.core.view.ViewCompat
import com.konkuk.walku.R
import com.konkuk.walku.config.BaseActivity
import com.konkuk.walku.databinding.ActivityWalkMapBinding
import com.konkuk.walku.src.main.MainActivity
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class WalkMapActivity : BaseActivity<ActivityWalkMapBinding>(ActivityWalkMapBinding::inflate),
    OnMapReadyCallback {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            supportActionBar?.hide()
        }
        mapView = binding.activityWalkMapMapView
        mapView.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
    }

    override fun onMapReady(map: NaverMap) {
        showCustomToast("onMapReady")
    }

}