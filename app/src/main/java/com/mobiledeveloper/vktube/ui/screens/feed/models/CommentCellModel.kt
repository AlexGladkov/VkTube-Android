package com.mobiledeveloper.vktube.ui.screens.feed.models

data class CommentCellModel(
    val messageId: Int,
    val userId: Long,
    val userName: String,
    val dateAdded: String,
    val avatar: String,
    val text: String
)