package com.mobiledeveloper.vktube.ui.screens.feed.models

import com.mobiledeveloper.vktube.data.user.StoredUser
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel

data class FeedState(
    val items: List<VideoCellModel> = emptyList(),
    val currentUser: StoredUser? = null,
    val loading: Boolean = true
)