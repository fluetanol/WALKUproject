package com.konkuk.walku.src.main.home.map.bikeMap.model

data class ModelBike(
    val rackTotalCount: String = "",        // 거치대 개수
    val stationName: String = "",           // 대여소이름
    val parkingBikeTotalCount: String = "", // 자전거주차총건수
    val shared: String = "",                // 거치율
    val stationLatitude: String = "",       // 위도
    val stationLongitude: String = ""      // 경도
)
