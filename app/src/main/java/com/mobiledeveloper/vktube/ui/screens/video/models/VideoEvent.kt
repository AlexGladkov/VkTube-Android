package com.mobiledeveloper.vktube.ui.screens.video.models

sealed class VideoEvent {
    data class LaunchVideo(val videoId: Long?) : VideoEvent()
    data class SendComment(val comment: String) : VideoEvent()
    object LikeClick : VideoEvent()
    object CommentsClick : VideoEvent()
    object CloseCommentsClick : VideoEvent()
    object ClearAction : VideoEvent()
    object VideoLoading : VideoEvent()
}