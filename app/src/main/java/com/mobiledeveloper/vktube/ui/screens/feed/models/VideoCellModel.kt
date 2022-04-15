package com.mobiledeveloper.vktube.ui.screens.feed.models

import com.mobiledeveloper.vktube.ui.common.cell.VideoCellGroupInfo

data class VideoCellModel(
    val videoId: Long,
    val title: String,
    val previewUrl: String,
    val viewsCount: Int,
    val dateAdded: Int,
    val likes: Int,
    val likesByMe: Boolean,
    val videoUrl: String,
    val ownerId: Long,
    val groupInfo: VideoCellGroupInfo,
    val groupOrder:Int,
    val formattedVideoInfo: String,
    val videoText: String,
    val subscribersText: String,
    val viewsCountFormatted: String
) {
    val id = "${ownerId}_${videoId}"
}