package com.konkuk.walku.src.main.analysis.statistics

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class DecimalValueFormatter:ValueFormatter() {
    private val format = DecimalFormat("###,##")

    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
        return format.format(barEntry?.y)
    }

}