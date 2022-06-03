package com.konkuk.walku.src.main.analysis.today

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleBarView: View {

    // 생성자
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    // ARC(호)의 각도값을 관리할 변수
    var numProgress: Float = 0.0f

    // 뷰 그리기
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint()

        // 1. 회색 원(배경)
        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 40f
        canvas?.drawArc(40f, 100f, 700f, 760f, 0f, 360f, false, paint)


        val textPaint = Paint()
        textPaint.textSize= 60F
        canvas?.drawText("3000/5000",220.0f,440.0f,textPaint)

        // 2. 파란 원(프로그레스)
        paint.color = Color.GREEN
        paint.strokeCap = Paint.Cap.ROUND
        // sweepAngle 매개변수 위치에 위에서 선언한 numProgress 변수를 넣어준다.
        canvas?.drawArc(40f, 100f, 700f, 760f, -90f, numProgress, false, paint)

    }


    // 함수: 프로그레스바의 각도값을 변경하는 함수
    fun setProgress(num: Float){

        // numProgress 값을 변경한다.
        numProgress = num

        // 뷰 갱신: 변경된 numProgress 값을 적용하여 뷰를 다시 그린다.
        invalidate()
    }


}
