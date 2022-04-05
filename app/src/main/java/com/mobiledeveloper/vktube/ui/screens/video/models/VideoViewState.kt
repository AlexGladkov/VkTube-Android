package com.mobiledeveloper.vktube.ui.screens.video.models

import com.mobiledeveloper.vktube.data.user.StoredUser
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.comments.CommentCellModel
import com.vk.sdk.api.wall.dto.WallWallComment

data class VideoViewState(
    val video: VideoCellModel? = null,
    val currentUser: StoredUser? = null,
    val comments: List<CommentCellModel> = emptyList()
)