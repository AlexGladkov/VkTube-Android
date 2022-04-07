package com.mobiledeveloper.vktube.ui.screens.feed.models

sealed class FeedEvent {
    object ScreenShown : FeedEvent()
    object ClearAction : FeedEvent()
    data class VideoClicked(val videoCellModel: VideoCellModel) : FeedEvent()
    data class OnScroll(val lastVisibleItemIndex: Int, val screenItemsCount: Int) : FeedEvent()
}