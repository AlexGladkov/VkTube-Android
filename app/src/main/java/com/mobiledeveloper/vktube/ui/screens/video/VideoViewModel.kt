package com.mobiledeveloper.vktube.ui.screens.video

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.comments.CommentsRepository
import com.mobiledeveloper.vktube.data.like.LikeRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.comments.CommentCellModel
import com.mobiledeveloper.vktube.ui.screens.comments.mapToCommentCellModel
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoAction
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoEvent
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
) : BaseViewModel<VideoViewState, VideoAction, VideoEvent>(
    VideoViewState()
) {

    private var videoId: Long? = null

    override fun obtainEvent(viewEvent: VideoEvent) {
        when (viewEvent) {
            is VideoEvent.LaunchVideo -> performVideoLaunch(viewEvent.videoId)
            is VideoEvent.SendComment -> performSendComment(viewEvent.comment)
            is VideoEvent.LikeClick -> performLike()
            is VideoEvent.CommentsClick -> showComments()
            is VideoEvent.ClearAction -> clearAction()
            is VideoEvent.VideoLoading -> performVideoLoading()
        }
    }

    private fun performVideoLoading() {
        viewModelScope.launch {
            delay(500)
            when(viewState.isLoadingVideo){
                true -> viewState = viewState.copy(
                    isLoadingVideo = false
                )
                false -> {}
                null -> viewState = viewState.copy(
                    isLoadingVideo = true
                )
            }
        }
    }

    private fun performLike() {
        viewModelScope.launch{
            val video: VideoCellModel = viewState.video ?: return@launch

            if (video.likesByMe) {
                viewState = viewState.copy(
                    video = video.copy(
                        likesByMe = false,
                        likes = video.likes - 1
                    ),
                )
                likeRepository.unlike(video.videoId, video.ownerId)
            }
            else {
                viewState = viewState.copy(
                    video = video.copy(
                        likesByMe = true,
                        likes = video.likes + 1
                    ),
                )
                likeRepository.like(video.videoId, video.ownerId)
            }
            val indexVideoInCache = InMemoryCache.clickedVideos.indexOf(video)
            InMemoryCache.clickedVideos.add(indexVideoInCache, viewState.video!!)
        }
    }

    private fun performVideoLaunch(videoId: Long?) {
        this.videoId = videoId
        if (InMemoryCache.clickedVideos.isEmpty()) throw IllegalStateException("Can't show video without cache")

        viewModelScope.launch {
            val video = InMemoryCache.clickedVideos.first { it.videoId == videoId }
            val currentUser = userRepository.fetchLocalUser()

            viewState = viewState.copy(
                video = video,
                currentUser = currentUser
            )


            try {
                val comments =
                    commentsRepository.fetchCommentsForVideo(videoId = video.videoId, count = 20)
                viewState = viewState.copy(
                    comments = comments.items.map { it.mapToCommentCellModel() }
                )
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    private fun performSendComment(comment: String) {
        viewModelScope.launch {
            val storedUser = userRepository.fetchLocalUser()
            val userId = storedUser.userId
            val currentComments = viewState.comments.toMutableList()
            currentComments.add(
                CommentCellModel(
                    messageId = -1,
                    userId = userId,
                    text = comment,
                    dateAdded = "Только что",
                    userName = storedUser.name,
                    avatar = storedUser.avatar
                )
            )

            viewState = viewState.copy(
                comments = currentComments
            )


            videoId?.let {
                commentsRepository.addCommentForVideo(
                    userId = userId,
                    videoId = it,
                    comment = comment
                )
            }
        }
    }

    private fun showComments() {
        viewModelScope.launch {
            viewAction = VideoAction.OpenComments
        }
    }

    private fun clearAction() {
        viewModelScope.launch {
            viewAction = null
        }
    }
}