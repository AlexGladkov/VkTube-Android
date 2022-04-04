package com.mobiledeveloper.vktube.ui.screens.feed.models

import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel

sealed class FeedEvent {
    object ScreenShown : FeedEvent()
    object ClearAction : FeedEvent()
    data class VideoClicked(val videoCellModel: VideoCellModel) : FeedEvent()
    data class OnScroll(val lastVisibleItemIndex: Int, val screenItemsCount: Int) : FeedEvent()
}