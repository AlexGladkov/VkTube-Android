package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.cache.InMemoryCache
import com.mobiledeveloper.vktube.data.clubs.ClubsLocalDataSource
import com.mobiledeveloper.vktube.data.clubs.ClubsRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.data.videos.VideosRepository
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.cell.mapToVideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState
import com.vk.dto.common.id.abs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val clubsRepository: ClubsRepository,
    private val videosRepository: VideosRepository,
    private val clubsLocalDataSource: ClubsLocalDataSource,
    private val userRepository: UserRepository
) : BaseViewModel<FeedState, FeedAction, FeedEvent>(initialState = FeedState()) {
    private val loadMoreController = LoadMoreController()

    override fun obtainEvent(viewEvent: FeedEvent) {
        when (viewEvent) {
            FeedEvent.ScreenShown -> fetchVideos()
            FeedEvent.ClearAction -> clearAction()
            is FeedEvent.VideoClicked -> obtainVideoClick(viewEvent.videoCellModel)
            is FeedEvent.OnScroll -> loadMoreController.handleScroll(viewEvent.lastVisibleItemIndex)
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
            viewState = viewState.copy(
                loading = true
            )
            try {
                val userId = try {
                    userRepository.fetchLocalUser().userId
                } catch (e: Exception) {
                    userRepository.fetchAndSaveUser()
                    userRepository.fetchLocalUser().userId
                }

                val localClubsIds = clubsLocalDataSource.loadClubsIds()
                val rawVideosJob = async {
                    if (localClubsIds.any())
                        videosRepository.fetchVideos(
                            groupIds = localClubsIds.map { it },
                            count = PAGE_SIZE
                        )
                    else
                        emptyList()
                }

                val clubs = clubsRepository.fetchClubs(userId)
                val (deletedClubs, newClubs) = withContext(Dispatchers.Default) {
                    val currentClubsIds = clubs.items.map { it.id.value }
                    clubsLocalDataSource.saveClubsIds(currentClubsIds)

                    val deletedClubs = localClubsIds.filter { it !in currentClubsIds }
                    val newClubs = currentClubsIds.filter { it !in localClubsIds }

                    Pair(deletedClubs, newClubs)
                }

                val newVideosJob = async {
                    if (newClubs.any())
                        videosRepository.fetchVideos(
                            groupIds = newClubs.map { it },
                            count = PAGE_SIZE
                        )
                    else
                        emptyList()
                }

                val rawVideos = rawVideosJob.await().filter { it.ownerId?.value !in deletedClubs }
                val newVideos = newVideosJob.await()

                val videos = withContext(Dispatchers.Default) {
                    (rawVideos + newVideos)
                        .mapNotNull { videoFull ->
                            val group =
                                clubs.items.firstOrNull { it.id.abs() == videoFull.ownerId?.abs() }
                            videoFull.mapToVideoCellModel(
                                userName = group?.name.orEmpty(),
                                userImage = group?.photo100.orEmpty(),
                                subscribers = group?.membersCount ?: 0
                            )
                        }.sortedByDescending { it.dateAdded }
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
        private val groups = mutableMapOf<Long, LoadedGroupInfo>()

        /**
         * [groupId,[offset1, offset2..]
         */
        private val loadedGroupsList = mutableMapOf<Long, List<Int>>()
        private val loadingListLock = Any()

        fun handleScroll(lastVisibleItemIndex: Int) {
            fun getGroupForLoad(): LoadedGroupInfo? {
                synchronized(loadingListLock) {
                    val itemsSize = viewState.items.size
                    val groupId =
                        viewState.items.getOrNull(
                            (lastVisibleItemIndex + PAGE_SIZE).coerceAtMost(itemsSize - 1)
                        )?.ownerId
                            ?: return null

                    val groupInfo = groups[groupId] ?: return null
                    if (!groupInfo.hasMore) return null
                    if (groupInfo.lastItemIndex > lastVisibleItemIndex + PAGE_SIZE) return null
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
                            groupId = -groupInfo.ownerId,
                            count = PAGE_SIZE,
                            offset = groupInfo.loadedCount
                        )
                        if (rawVideos.any()) {
                            val videos = rawVideos.mapNotNull { videoFull ->
                                videoFull.mapToVideoCellModel(
                                    userName = groupInfo.userName,
                                    userImage = groupInfo.userImage,
                                    subscribers = groupInfo.subscribers
                                )
                            }

                            val newItems = (viewState.items + videos).distinctBy { it.id }
                            viewState = viewState.copy(
                                items = newItems.sortedByDescending { it.dateAdded },
                                loading = false
                            )
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
                    groups.clear()
                    val size = items.size
                    items.reversed()
                        .forEachIndexed { index, item ->
                            val ownerId = item.ownerId
                            groups[ownerId] = groups[ownerId]?.let {
                                it.copy(
                                    loadedCount = it.loadedCount + 1,
                                    hasMore = (it.loadedCount + 1) % PAGE_SIZE == 0
                                )
                            } ?: LoadedGroupInfo(
                                ownerId = ownerId,
                                lastItemIndex = size - index - 1,
                                loadedCount = 1,
                                hasMore = false,
                                userName = item.userName,
                                userImage = item.userImage,
                                subscribers = item.subscribers
                            )
                        }
                }
            }
    }

    private data class LoadedGroupInfo(
        val ownerId: Long,
        val lastItemIndex: Int,
        val loadedCount: Int,
        val hasMore: Boolean,
        val userImage: String,
        val userName: String,
        val subscribers: Int
    )

    companion object {
        private const val PAGE_SIZE = 20
    }
}