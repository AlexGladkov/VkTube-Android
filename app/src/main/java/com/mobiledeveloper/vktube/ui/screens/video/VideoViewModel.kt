package com.mobiledeveloper.vktube.ui.screens.video

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.comments.CommentsRepository
import com.mobiledeveloper.vktube.data.like.LikeRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.ui.screens.feed.models.CommentCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoAction
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoEvent
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoViewState
import com.mobiledeveloper.vktube.utils.DateUtil
import com.vk.sdk.api.wall.dto.WallWallComment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val dateUtil: DateUtil,
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
            VideoEvent.CloseCommentsClick -> closeComments()
        }
    }

    private var webView: WeakReference<View>? = null

    fun getWebView(context: Context, video: VideoCellModel, onVideoLoading: () -> Unit): View {
        if (webView?.get() == null) {
            val data = """
                        <html>
                            <head>
                            <style>
                                .frame {
                                    width: 100%;
                                    height: 100vh;
                                    overflow: auto;
                                }
                            </style>
                          
                            </head>
                            <body style="background:black">
                                <iframe id="video" class="frame" src="${video.videoUrl}&autoplay=1" frameborder="0" allow="autoplay"/>
                            </body>
                        </html>
                        """
            val view = WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mediaPlaybackRequiresUserGesture = false
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        onVideoLoading.invoke()
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        onVideoLoading.invoke()
                        super.onPageFinished(view, url)
                    }
                }
                webChromeClient = object : WebChromeClient(){

                }
                loadData(data, "text/html", "utf-8")
            }
            webView = WeakReference(view)
        }
        val view = webView?.get()!!
        (view.parent as ViewGroup?)?.removeView(view)
        return view
    }

    private fun performVideoLoading() {
        viewModelScope.launch {
            delay(500)
            updateLoading(viewState.isLoadingVideo)?.let { loading ->
                viewState = viewState.copy(isLoadingVideo = loading)
            }
        }
    }

    private fun updateLoading(loading: Boolean?): Boolean? = when (loading) {
        true -> false
        false -> null
        null -> true
    }

    private fun performLike() {
        viewModelScope.launch {
            val video: VideoCellModel = viewState.video ?: return@launch

            if (video.likesByMe) {
                viewState = viewState.copy(
                    video = video.copy(
                        likesByMe = false,
                        likes = video.likes - 1
                    ),
                )
                likeRepository.unlike(video.videoId, video.ownerId)
            } else {
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
            val ownerId = video.ownerId

            viewState = viewState.copy(
                video = video,
                currentUser = currentUser
            )

            val comments =
                commentsRepository.fetchCommentsForVideo(
                    ownerId = ownerId,
                    videoId = video.videoId,
                    count = 20
                )
            viewState = viewState.copy(
                comments = comments.items.map { it.mapToCommentCellModel(dateUtil) }
            )
            InMemoryCache.comments[videoId!!] = comments.items.map { it.mapToCommentCellModel(dateUtil) }
        }
    }

    private fun performSendComment(comment: String) {
        viewModelScope.launch {
            val storedUser = userRepository.fetchLocalUser()
            val userId = storedUser.userId
            val ownerId = viewState.video?.ownerId ?: return@launch
            val currentComments = viewState.comments.toMutableList()

            videoId?.let {
                commentsRepository.addCommentForVideo(
                    ownerId = ownerId,
                    videoId = it,
                    comment = comment
                )
            }

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
            InMemoryCache.comments[videoId!!] = currentComments

            viewState = viewState.copy(
                comments = currentComments
            )
        }
    }

    private fun WallWallComment.mapToCommentCellModel(dateUtil: DateUtil): CommentCellModel = CommentCellModel(
        messageId = id,
        userId = fromId.value,
        text = text,
        userName = "Unowned user",
        dateAdded = dateUtil.getTimeAgo(this.date),
        avatar = ""
    )

    private fun showComments() {
        viewModelScope.launch {
            viewAction = VideoAction.OpenComments
        }
    }

    private fun closeComments() {
        viewModelScope.launch {
            viewAction = VideoAction.CloseComments
        }
    }

    private fun clearAction() {
        viewModelScope.launch {
            viewAction = null
        }
    }
}