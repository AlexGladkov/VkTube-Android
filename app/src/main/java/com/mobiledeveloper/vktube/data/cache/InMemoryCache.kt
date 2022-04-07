package com.mobiledeveloper.vktube.data.cache

import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.CommentCellModel

object InMemoryCache {

    val clickedVideos: MutableList<VideoCellModel> = mutableListOf()
    val comments: MutableMap<Long, List<CommentCellModel>> = mutableMapOf()
}