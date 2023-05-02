package com.livechat.repo

import android.net.Uri
import com.google.firebase.storage.StorageReference
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-05-02
 * Time: 09:47 PM
 */
@Singleton
class FileRepo @Inject constructor(private val storageReference: StorageReference) {

    fun uploadFile(
        path: String,
        newFileName: String,
        onSuccess: (url: String) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val file = Uri.fromFile(File(path))
        val storageReference = storageReference.child(newFileName)
        val uploadTask = storageReference.putFile(file)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            storageReference.downloadUrl.addOnSuccessListener {
                onSuccess(it.toString())
            }.addOnFailureListener {
                onError(it)
            }
        }.addOnFailureListener {
            onError(it)
        }
    }
}