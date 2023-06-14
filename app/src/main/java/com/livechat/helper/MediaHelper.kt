package com.livechat.helper

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.livechat.model.FileModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-27
 * Time: 12:00 AM
 */
class MediaHelper @Inject constructor(private val contentResolver: ContentResolver) {
    // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your app didn't create.

    private val imageCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

    private val videoCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

    private val imageProjection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.DATE_MODIFIED,
    )

    private val videoProjection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.SIZE,
        MediaStore.Images.Media.DURATION,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.DATE_MODIFIED
    )

    private val imageSortOrder = "${MediaStore.Images.Media.DATA} ASC"

    private val videoSortOrder = "${MediaStore.Video.Media.DATA} ASC"

    fun getAllImages(): ArrayList<FileModel> {
        val files = ArrayList<FileModel>()
        try {
            contentResolver.query(imageCollection, imageProjection, null, null, imageSortOrder)
                ?.use { cursor ->
                    // Cache column indices.
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val dateTakenColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given image.
                        val id = cursor.getLong(idColumn)
                        val data = cursor.getString(dataColumn)
                        val size = cursor.getLong(sizeColumn)
                        val dateTaken = cursor.getInt(dateTakenColumn)
                        val dateModified = cursor.getInt(dateModifiedColumn)
                        files.add(
                            FileModel(
                                uriId = id,
                                path = data,
                                size = size,
                                dateTaken = dateTaken,
                                dateModified = dateModified,
                                type = FileModel.Type.IMAGE
                            )
                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return files
    }

    fun getImageFromUri(uri: Uri): FileModel? {
        var file: FileModel? = null
        try {
            contentResolver.query(uri, imageProjection, null, null, imageSortOrder)
                ?.use { cursor ->
                    // Cache column indices.
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val dateTakenColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given image.
                        val id = cursor.getLong(idColumn)
                        val data = cursor.getString(dataColumn)
                        val size = cursor.getLong(sizeColumn)
                        val dateTaken = cursor.getInt(dateTakenColumn)
                        val dateModified = cursor.getInt(dateModifiedColumn)
                        file = FileModel(
                            uriId = id,
                            path = data,
                            size = size,
                            dateTaken = dateTaken,
                            dateModified = dateModified,
                            type = FileModel.Type.IMAGE
                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    fun getAllVideos(): ArrayList<FileModel> {
        val files = ArrayList<FileModel>()
        try {
            contentResolver.query(videoCollection, videoProjection, null, null, videoSortOrder)
                ?.use { cursor ->
                    // Cache column indices.
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val dateTakenColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given image.
                        val id = cursor.getLong(idColumn)
                        val data = cursor.getString(dataColumn)
                        val size = cursor.getLong(sizeColumn)
                        val duration = cursor.getInt(durationColumn)
                        val dateTaken = cursor.getInt(dateTakenColumn)
                        val dateModified = cursor.getInt(dateModifiedColumn)

                        files.add(
                            FileModel(
                                uriId = id,
                                path = data,
                                size = size,
                                duration = duration,
                                dateTaken = dateTaken,
                                dateModified = dateModified,
                                type = FileModel.Type.VIDEO
                            )
                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return files
    }
}