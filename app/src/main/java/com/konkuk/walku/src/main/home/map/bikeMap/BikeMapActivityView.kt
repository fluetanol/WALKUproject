package com.konkuk.walku.src.main.home.map.bikeMap

import com.konkuk.walku.src.main.home.map.bikeMap.model.GetBikeResponse

interface BikeMapActivityView {

    fun onGetBikeSuccess(response: GetBikeResponse)

    fun onGetBikeFailure(message: String)

}