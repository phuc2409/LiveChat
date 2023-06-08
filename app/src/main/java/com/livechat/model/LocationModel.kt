package com.livechat.model

/**
 * User: Quang Phúc
 * Date: 2023-06-08
 * Time: 10:48 PM
 */
data class LocationModel(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val name: String = "",
    val address: String = ""
)
