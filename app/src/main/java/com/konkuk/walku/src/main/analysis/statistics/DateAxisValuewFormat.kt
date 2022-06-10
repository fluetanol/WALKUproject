package com.konkuk.walku.src.main.analysis.statistics

import android.annotation.SuppressLint
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateAxisValuewFormat: IndexAxisValueFormatter() {

    @SuppressLint("SimpleDateFormat")
    override fun getFormattedValue(value: Float): String {
        val valueToDate = TimeUnit.DAYS.toMillis(value.toLong())
        val timeDays = Date(valueToDate)
        val formatDays = SimpleDateFormat("MM.dd")

        return formatDays.format(timeDays)
    }
}