package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.videos.GetUserVideoUseCase
import com.mobiledeveloper.vktube.data.videos.VideosRepository
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellGroupInfo
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.cell.mapToVideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState
import com.mobiledeveloper.vktube.utils.DateUtil
import com.mobiledeveloper.vktube.utils.NumberUtil
import com.vk.dto.common.id.abs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val dateUtil: DateUtil,
    private val numberUtil: NumberUtil,
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

                val videos = withContext(Dispatchers.Default) {
                    rawVideos
                        .groupBy { it.ownerId }
                        .mapNotNull { (ownerId, items) ->
                            val group =
                                clubs.firstOrNull { it.id.abs() == ownerId?.abs() }
                            items.mapNotNull {
                                it.mapToVideoCellModel(
                                    userName = group?.name.orEmpty(),
                                    userImage = group?.photo100.orEmpty(),
                                    subscribers = group?.membersCount ?: 0,
                                    dateUtil = dateUtil,
                                    numberUtil = numberUtil
                                )
                            }
                        }.flatten().sortedByDescending { it.dateAdded }
                }
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

    private inner class LoadMoreController {
        private var groups = mutableMapOf<Long, LoadedGroupInfo>()

        /**
         * [groupId,[offset1, offset2..]
         */
        private val loadedGroupsList = mutableMapOf<Long, List<Int>>()
        private val loadingListLock = Any()

        fun handleScroll(lastVisibleItemIndex: Int, screenItemsCount: Int) {
            fun getGroupForLoad(): LoadedGroupInfo? {
                val itemsSize = viewState.items.size
                val preloadIndex =
                    (screenItemsCount * PRELOAD_SCREENS_COUNT + lastVisibleItemIndex)
                        .coerceAtMost(itemsSize - 1)
                val groupId = viewState.items.getOrNull(preloadIndex)?.ownerId ?: return null
                synchronized(loadingListLock) {
                    val groupInfo = groups[groupId] ?: return null
                    if (!groupInfo.hasMore) return null
                    if (groupInfo.lastItemIndex > preloadIndex) return null
                    val offset = groupInfo.loadedCount

                    val loadingOffsets = loadedGroupsList[groupId] ?: emptyList()
                    val maxLoadingOffset =
                        (loadedGroupsList[groupId] ?: emptyList()).maxOrNull() ?: 0

                    if (maxLoadingOffset >= offset) return null

                    loadedGroupsList[groupId] = loadingOffsets + listOf(groupInfo.loadedCount)

                    return groupInfo
                }
            }

            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        val groupInfo = getGroupForLoad() ?: return@withContext
                        val rawVideos = videosRepository.fetchVideos(
                            groupId = groupInfo.groupInfo.id,
                            count = PAGE_SIZE,
                            offset = groupInfo.loadedCount
                        )
                        if (rawVideos.any()) {
                            val videos = rawVideos.mapNotNull { videoFull ->
                                videoFull.mapToVideoCellModel(
                                    userName = groupInfo.groupInfo.userName,
                                    userImage = groupInfo.groupInfo.userImage,
                                    subscribers = groupInfo.groupInfo.subscribers,
                                    dateUtil = dateUtil,
                                    numberUtil = numberUtil
                                )
                            }

                            val newItems = (viewState.items + videos).distinctBy { it.id }
                            viewState = viewState.copy(
                                items = newItems.sortedByDescending { it.dateAdded },
                                loading = false
                            )
                            groups[groupInfo.groupInfo.id] = groupInfo.copy(hasMore = videos.any())
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
                                it.copy(
                                    loadedCount = it.loadedCount + 1,
                                    hasMore = groups[groupId]?.hasMore ?: ((it.loadedCount + 1) % PAGE_SIZE == 0)
                                )
                            } ?: LoadedGroupInfo(
                                groupInfo = groupInfo.copy(),
                                lastItemIndex = size - index - 1,
                                loadedCount = 1,
                                hasMore = groups[groupId]?.hasMore ?: false
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