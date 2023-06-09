package com.livechat.util

import android.Manifest
import android.os.Build

/**
 * User: Quang Phúc
 * Date: 2023-04-26
 * Time: 11:38 PM
 */
object PermissionsUtil {

    private val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val storagePermissionsAndroid13 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    private val videoCallPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val cameraPermissionsAndroid10 = arrayOf(
        Manifest.permission.CAMERA
    )

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    fun getStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            storagePermissionsAndroid13
            storagePermissions
        } else {
            storagePermissions
        }
    }

    fun getVideoCallPermissions(): Array<String> {
        return videoCallPermissions
    }

    fun getCameraPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cameraPermissionsAndroid10
        } else {
            cameraPermissions
        }
    }

    fun getLocationPermissions(): Array<String> {
        return locationPermissions
    }
}
