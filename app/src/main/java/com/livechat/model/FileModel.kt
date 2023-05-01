package com.livechat.model

/**
 * User: Quang Phúc
 * Date: 2023-04-27
 * Time: 12:01 AM
 */
data class FileModel(
    val uriId: Long,
    val path: String,
    val size: Int,
    val duration: Int = 0,
    val dateTaken: Int,
    val dateModified: Int,
    val type: Type,
    var isSelected: Boolean = false
) {

    enum class Type {
        IMAGE,
        VIDEO
    }
}
