package com.mobiledeveloper.vktube.ui.screens.video.models

import com.mobiledeveloper.vktube.data.user.StoredUser
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.CommentCellModel

data class VideoViewState(
    val video: VideoCellModel? = null,
    val isLoadingVideo: Boolean? = null,
    val currentUser: StoredUser? = null,
    val comments: List<CommentCellModel> = emptyList()
)