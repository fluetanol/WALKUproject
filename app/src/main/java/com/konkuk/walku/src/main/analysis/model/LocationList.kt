package com.konkuk.walku.src.main.analysis.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationList(
    val locationArrayList: ArrayList<Walk>
) : Parcelable{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "locationArrayList" to locationArrayList
        )
    }
}


