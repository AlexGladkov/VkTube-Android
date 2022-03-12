package com.mobiledeveloper.vktube.ui.screens.video.models

import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.vk.sdk.api.wall.dto.WallWallComment

data class VideoViewState(
    val video: VideoCellModel? = null,
    val comments: List<WallWallComment> = emptyList()
)