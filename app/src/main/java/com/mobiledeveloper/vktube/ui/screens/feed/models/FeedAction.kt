package com.mobiledeveloper.vktube.ui.screens.feed.models

sealed class FeedAction {
    data class OpenVideoDetail(val videoId: Long) : FeedAction()
    data class LoadError(val message: String) : FeedAction()
}