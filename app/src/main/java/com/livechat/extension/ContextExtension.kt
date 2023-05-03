package com.livechat.extension

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.livechat.R

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

fun Context.downloadFile(url: String, fileName: String) {
    val subPath = "${getString(R.string.app_name).replace(" ", "")}/$fileName"
    val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    val downloadManager: DownloadManager =
        getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}