package com.konkuk.walku.src.main.home.map.walkMap.model

data class ModelPark(
    val parkName: String = "",        // 공원명
    val parkDescription: String = "", // 공원개요
    val parkMainEquip: String = "",   // 주요시설
    val parkImage: String = "",       // 공원이미지
    val parkZone: String = "",        // 공원지역 (ex : 중구)
    val parkAddress: String = "",     // 공원주소
    val parkLongitude: String = "",   // 공원위도
    val parkLatitude: String = ""     // 공원경도
)
