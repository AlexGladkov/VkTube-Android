package com.mobiledeveloper.vktube.data.cache

import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.comments.CommentCellModel

object InMemoryCache {

    val clickedVideos: MutableList<VideoCellModel> = mutableListOf()
    val comments: MutableMap<Long, List<CommentCellModel>> = mutableMapOf()
}