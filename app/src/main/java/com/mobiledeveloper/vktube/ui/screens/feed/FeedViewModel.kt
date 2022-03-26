package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.clubs.ClubsRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.cell.mapToVideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState
import com.vk.dto.common.id.abs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val clubsRepository: ClubsRepository,
    private val userRepository: UserRepository
) : BaseViewModel<FeedState, FeedAction, FeedEvent>(initialState = FeedState()) {
    override fun obtainEvent(viewEvent: FeedEvent) {
        when (viewEvent) {
            FeedEvent.ScreenShown -> fetchVideos()
            FeedEvent.ClearAction -> clearAction()
            is FeedEvent.VideoClicked -> obtainVideoClick(viewEvent.videoCellModel)
        }
    }

    private fun obtainVideoClick(videoCellModel: VideoCellModel) {
        viewModelScope.launch {
            InMemoryCache.clickedVideos.add(videoCellModel)
            viewAction = FeedAction.OpenVideoDetail(videoCellModel.videoId)
        }
    }

    private fun clearAction() {
        viewModelScope.launch {
            viewAction = null
        }
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            val userId = try {
                userRepository.fetchLocalUser().userId
            } catch (e: Exception) {
                userRepository.fetchAndSaveUser()
                userRepository.fetchLocalUser().userId
            }

            val clubs = clubsRepository.fetchClubs(userId)
            val rawVideos =
                clubsRepository.fetchVideos(groupIds = clubs.items.map { it.id }, count = 20)
            val videos = withContext(Dispatchers.Default) {
                rawVideos.mapNotNull { videoFull ->
                    val group = clubs.items.firstOrNull { it.id.abs() == videoFull.ownerId?.abs() }
                    videoFull.mapToVideoCellModel(
                        userName = group?.name.orEmpty(),
                        userImage = group?.photo100.orEmpty(),
                        subscribers = group?.membersCount ?: 0
                    )
                }
            }
            viewState = viewState.copy(
                items = videos
            )
        }
    }
}