package com.konkuk.walku.src.main.home.map.walkMap.model

import com.google.gson.annotations.SerializedName

data class Row(
    @SerializedName("P_IDX") val P_IDX: String,
    @SerializedName("P_PARK") val P_PARK: String,
    @SerializedName("P_LIST_CONTENT") val P_LIST_CONTENT: String,
    @SerializedName("AREA") val AREA: String,
    @SerializedName("OPEN_DT") val OPEN_DT: String,
    @SerializedName("MAIN_EQUIP") val MAIN_EQUIP: String,
    @SerializedName("MAIN_PLANTS") val MAIN_PLANTS: String,
    @SerializedName("GUIDANCE") val GUIDANCE: String,
    @SerializedName("VISIT_ROAD") val VISIT_ROAD: String,
    @SerializedName("USE_REFER") val USE_REFER: String,
    @SerializedName("P_IMG") val P_IMG: String,
    @SerializedName("P_ZONE") val P_ZONE: String,
    @SerializedName("P_ADDR") val P_ADDR: String,
    @SerializedName("P_NAME") val P_NAME: String,
    @SerializedName("P_ADMINTEL") val P_ADMINTEL: String,
    @SerializedName("G_LONGITUDE") val G_LONGITUDE: String,
    @SerializedName("G_LATITUDE") val G_LATITUDE: String,
    @SerializedName("LONGITUDE") val LONGITUDE: String,
    @SerializedName("LATITUDE") val LATITUDE: String,
    @SerializedName("TEMPLATE_URL") val TEMPLATE_URL: String
)