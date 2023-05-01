package com.livechat.extension

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 10:09 PM
 */
fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.checkPermissions(permissions: Array<String>): Boolean {
    for (i in permissions) {
        if (checkSelfPermission(i) == PackageManager.PERMISSION_DENIED) {
            return false
        }
    }
    return true
}