package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.videos.GetUserVideoUseCase
import com.mobiledeveloper.vktube.data.videos.VideosRepository
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellGroupInfo
import com.mobiledeveloper.vktube.ui.common.cell.mapToVideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import com.vk.dto.common.id.abs
import com.vk.sdk.api.groups.dto.GroupsGroupFull
import com.vk.sdk.api.video.dto.VideoVideoFull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val videosRepository: VideosRepository,
    private val getUserVideoUseCase: GetUserVideoUseCase
) : BaseViewModel<FeedState, FeedAction, FeedEvent>(initialState = FeedState()) {
    private val loadMoreController = LoadMoreController()

    override fun obtainEvent(viewEvent: FeedEvent) {
        when (viewEvent) {
            FeedEvent.ScreenShown -> onScreenShown()
            FeedEvent.ClearAction -> clearAction()
            is FeedEvent.VideoClicked -> obtainVideoClick(viewEvent.videoCellModel)
            is FeedEvent.OnScroll -> loadMoreController.handleScroll(
                viewEvent.lastVisibleItemIndex,
                viewEvent.screenItemsCount
            )
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

    private fun onScreenShown() {
        if (viewState.items.any()) return
        fetchVideos()
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            viewState = viewState.copy(
                loading = true
            )
            try {
                val userClubsWithVideos = getUserVideoUseCase(PAGE_SIZE)
                val clubs = userClubsWithVideos.clubs
                val rawVideos = userClubsWithVideos.videos

                val videos = mapItemsToModelItems(clubs, rawVideos)
                viewState = viewState.copy(
                    items = videos,
                    loading = false
                )

                loadMoreController.fillGroups(videos)
            } catch (ex: Exception) {
                viewState = viewState.copy(
                    loading = false
                )
            }
        }
    }

    private suspend fun mapItemsToModelItems(
        clubs: List<GroupsGroupFull>,
        rawVideos: List<VideoVideoFull>
    ) =
        withContext(Dispatchers.Default) {
            rawVideos
                .groupBy { it.ownerId }
                .mapNotNull { (ownerId, items) ->
                    val group =
                        clubs.firstOrNull { it.id.abs() == ownerId?.abs() }
                    items.mapNotNull {
                        it.mapToVideoCellModel(
                            userName = group?.name.orEmpty(),
                            userImage = group?.photo100.orEmpty(),
                            subscribers = group?.membersCount ?: 0
                        )
                    }
                }.flatten().sortedByDescending { it.dateAdded }
        }


    private inner class LoadMoreController {
        private var groups = mutableMapOf<Long, LoadedGroupInfo>()

        /**
         * [groupId,[offset1, offset2..]
         */
        private val loadedGroupsList = mutableMapOf<Long, List<Int>>()
        private val loadingListLock = Any()

        fun handleScroll(lastVisibleItemIndex: Int, screenItemsCount: Int) {
            fun getGroupsForLoad(): List<LoadedGroupInfo> {
                val itemsSize = viewState.items.size
                val preloadIndex =
                    (screenItemsCount * PRELOAD_SCREENS_COUNT + lastVisibleItemIndex)
                        .coerceAtMost(itemsSize - 1)

                synchronized(loadingListLock) {
                    val groupsForLoad =
                        groups
                            .values
                            .filter { it.hasMore && it.lastItemIndex <= preloadIndex }
                            .filter { loadedGroupsList[it.groupInfo.id]?.maxOrNull() ?: 0 < it.loadedCount }
                    groupsForLoad.forEach { groupInfo ->
                        val groupId = groupInfo.groupInfo.id

                        val loadingOffsets = loadedGroupsList[groupId] ?: emptyList()

                        loadedGroupsList[groupId] = loadingOffsets + listOf(groupInfo.loadedCount)
                    }

                    return groupsForLoad
                }
            }

            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        val groupsForLoad = getGroupsForLoad()
                        val groupsInfo = groupsForLoad
                            .map {
                                VideosRepository.LoadSettings(
                                    groupId = -it.groupInfo.id.absoluteValue,
                                    count = PAGE_SIZE,
                                    offset = it.loadedCount
                                )
                            }
                        val rawVideos = videosRepository.fetchBatchVideoFulls(groupsInfo)

                        val videos = rawVideos
                            .groupBy { it.ownerId }
                            .mapNotNull { (ownerId, items) ->
                                val group =
                                    groupsForLoad.firstOrNull { it.groupInfo.id.absoluteValue == ownerId?.value?.absoluteValue }

                                items.mapNotNull {
                                    it.mapToVideoCellModel(
                                        userName = group?.groupInfo?.userName.orEmpty(),
                                        userImage = group?.groupInfo?.userImage.orEmpty(),
                                        subscribers = group?.groupInfo?.subscribers ?: 0
                                    )
                                }
                            }.flatten()

                        if (videos.any()) {
                            val newItems = (viewState.items + videos)
                                .distinctBy { it.id }
                                .sortedByDescending { it.dateAdded }
                            viewState = viewState.copy(
                                items = newItems,
                                loading = false
                            )
                            synchronized(loadingListLock) {
                                groupsForLoad.forEach { group ->
                                    val groupId = group.groupInfo.id
                                    val hasMore =
                                        newItems.any { it.ownerId == groupId }
                                    groups[groupId] = group.copy(hasMore = hasMore)
                                }

                            }
                            fillGroups(viewState.items)
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        suspend fun fillGroups(items: List<VideoCellModel>) =
            withContext(Dispatchers.Default) {
                synchronized(loadingListLock) {
                    val newGroups = mutableMapOf<Long, LoadedGroupInfo>()
                    val size = items.size
                    items.reversed()
                        .forEachIndexed { index, item ->
                            val groupInfo = item.groupInfo
                            val groupId = groupInfo.id

                            newGroups[groupId] = newGroups[groupId]?.let {
                                val hasMore = groups[groupId]?.hasMore?: true
                                it.copy(
                                    loadedCount = it.loadedCount + 1,
                                    hasMore = hasMore
                                )
                            } ?: LoadedGroupInfo(
                                groupInfo = groupInfo.copy(),
                                lastItemIndex = size - index - 1,
                                loadedCount = 1,
                                hasMore = groups[groupId]?.hasMore ?: true
                            )
                        }
                    groups = newGroups.filter { it.value.hasMore }.toMutableMap()
                }
            }
    }

    private data class LoadedGroupInfo(
        val lastItemIndex: Int,
        val loadedCount: Int,
        val hasMore: Boolean,
        val groupInfo: VideoCellGroupInfo
    )

    companion object {
        private const val PAGE_SIZE = 20
        private const val PRELOAD_SCREENS_COUNT = 4
    }
}