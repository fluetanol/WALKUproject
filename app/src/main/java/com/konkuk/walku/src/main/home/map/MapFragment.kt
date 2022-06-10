package com.konkuk.walku.src.main.home.map

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.konkuk.walku.R
import com.konkuk.walku.databinding.FragmentMapBinding
import com.konkuk.walku.src.main.home.map.bikeMap.BikeMapSplashActivity
import com.konkuk.walku.src.main.home.map.model.MapViewPagerItem
import com.konkuk.walku.src.main.home.map.walkMap.WalkMapActivity
import com.konkuk.walku.src.main.home.map.walkMap.WalkMapSplashActivity

class MapFragment : Fragment() {

    private var binding: FragmentMapBinding? = null
    private var myHandler = MyHandler()
    private val intervalTime = 4000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (4000 = 4.0초)
    private var numItem = 3
    private var currentPosition = 1


    private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    private inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == 0) {
                binding?.fragmentMapViewpager?.setCurrentItemWithDuration(
                    ++currentPosition, +700
                ) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 이어서 한다.
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMapItemViewPager()
    }

    private fun getItemList(): ArrayList<MapViewPagerItem> {
        return arrayListOf(
            MapViewPagerItem(R.drawable.bike_view, "따릉이", "#서울자전거", "#라이딩"),
            MapViewPagerItem(R.drawable.walk_view, "산책로", "#서울산책로", "#힐링"),
            MapViewPagerItem(R.drawable.mountain_view, "등산로", "#서울등산로", "#북한산코스")
        )
    }

    private fun setMapItemViewPager() {
        val mapAdapter = MapAdapter(getItemList())
        mapAdapter.itemClickListener = object : MapAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int) {
                when (position % 3) {
                    0 -> {
                        val intent = Intent(requireActivity(), BikeMapSplashActivity::class.java)
                        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            androidx.core.util.Pair<View, String>(
                                view?.findViewById(R.id.fragment_map_image_view),
                                "background_image"
                            )
                        )
                        ActivityCompat.startActivity(
                            requireActivity(),
                            intent,
                            activityOptions.toBundle()
                        )
                    }
                    1 -> {
                        val intent = Intent(requireActivity(), WalkMapSplashActivity::class.java)
                        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            androidx.core.util.Pair<View, String>(
                                view?.findViewById(R.id.fragment_map_image_view),
                                "background_image"
                            )
                        )
                        ActivityCompat.startActivity(
                            requireActivity(),
                            intent,
                            activityOptions.toBundle()
                        )
                    }
                    2 -> {
                        Toast.makeText(requireActivity(), "등산로", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding?.apply {
            fragmentMapViewpager.adapter = mapAdapter
            fragmentMapViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            fragmentMapViewpager.setPageTransformer(ZoomOutPageTransformer()) // 슬라이딩 애니메이션
            fragmentMapViewpager.setCurrentItem(currentPosition, false) // 현재 위치를 지정
            // 현재 몇 번째 카드뷰인지 보여주는 숫자를 변경함
            fragmentMapViewpager.apply {
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        when (state) {
                            // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                            ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)

                            // 뷰페이저 움직이는 중
                            ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                        }
                    }
                })
            }
            fragmentMapViewpager.setCurrentItem(++currentPosition, true)
        }
    }

    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> {
                        alpha = 0f
                    }
                    position <= 1 -> {
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> {
                        alpha = 0f
                    }
                }
            }
        }
    }

    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0) // 이거 안하면 핸들러가 1개, 2개, 3개 ... n개 만큼 계속 늘어남
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime 만큼 반복해서 핸들러를 실행하게 함
    }

    private fun autoScrollStop() {
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    fun ViewPager2.setCurrentItemWithDuration(
        item: Int,
        duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
    ) {
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        val animator = ValueAnimator.ofInt(0, pxToDrag)
        var previousValue = 0
        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                beginFakeDrag()
            }

            override fun onAnimationEnd(animation: Animator?) {
                endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator?) { /* Ignored */
            }

            override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
            }
        })
        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}