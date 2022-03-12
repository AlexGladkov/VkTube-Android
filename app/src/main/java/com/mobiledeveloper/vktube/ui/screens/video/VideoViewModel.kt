package com.mobiledeveloper.vktube.ui.screens.video

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.comments.CommentsRepository
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoAction
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoEvent
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository
): BaseViewModel<VideoViewState, VideoAction, VideoEvent>(
    VideoViewState()
) {

    override fun obtainEvent(viewEvent: VideoEvent) {
        when (viewEvent) {
            is VideoEvent.LaunchVideo -> performVideoLaunch(viewEvent.videoId)
            is VideoEvent.CommentsClick -> showComments()
            is VideoEvent.ClearAction -> clearAction()
        }
    }

    private fun performVideoLaunch(videoId: Long?) {
        if (InMemoryCache.clickedVideos.isEmpty()) throw IllegalStateException("Can't show video without cache")

        viewModelScope.launch {
            val video = InMemoryCache.clickedVideos.first { it.videoId == videoId }

            updateState(viewState.copy(
                video = video
            ))

            val comments = commentsRepository.fetchCommentsForVideo(videoId = video.videoId, count = 20)
            updateState(viewState.copy(
                comments = comments.items
            ))
        }
    }

    private fun showComments() {
        viewModelScope.launch {
            callAction(VideoAction.OpenComments)
        }
    }

    private fun clearAction() {
        viewModelScope.launch {
            callAction(null)
        }
    }
}