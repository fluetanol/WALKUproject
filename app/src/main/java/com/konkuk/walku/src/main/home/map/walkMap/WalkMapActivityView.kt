package com.konkuk.walku.src.main.home.map.walkMap

import com.konkuk.walku.src.main.home.map.walkMap.model.GetWalkResponse

interface WalkMapActivityView {

    fun onGetWalkSuccess(response: GetWalkResponse)

    fun onGetWalkFailure(message: String)

}