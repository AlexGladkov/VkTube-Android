package com.mobiledeveloper.vktube.ui.screens.feed.models

data class VideoCellModel(
    val videoId: Long, val subscribers: Int,
    val title: String, val previewUrl: String, val userImage: String, val userName: String,
    val viewsCount: Int, val dateAdded: Int,
    val likes: Int, val likesByMe: Boolean, val videoUrl: String, val ownerId: Long
) {
    val id = "${ownerId}_${videoId}"
}