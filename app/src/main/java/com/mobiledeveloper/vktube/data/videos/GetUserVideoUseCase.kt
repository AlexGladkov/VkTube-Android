package com.mobiledeveloper.vktube.data.videos

import com.mobiledeveloper.vktube.data.clubs.ClubsLocalDataSource
import com.mobiledeveloper.vktube.data.clubs.ClubsRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.vk.sdk.api.groups.dto.GroupsGroupFull
import com.vk.sdk.api.video.dto.VideoVideoFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserVideoUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val clubsRepository: ClubsRepository,
    private val videosRepository: VideosRepository,
    private val clubsLocalDataSource: ClubsLocalDataSource,
) {
    suspend operator fun invoke(pageSize: Int): UserClubsWithVideos = withContext(Dispatchers.IO) {
        val userId = try {
            userRepository.fetchLocalUser().userId
        } catch (e: Exception) {
            userRepository.fetchAndSaveUser()
            userRepository.fetchLocalUser().userId
        }

        val ignoreList = clubsLocalDataSource.loadIgnoreList()

        val localClubsIds = clubsLocalDataSource.loadClubsIds().filter { it !in ignoreList }
        val rawVideosJob = async {
            if (localClubsIds.any())
                videosRepository.fetchVideos(
                    groupIds = localClubsIds.map { it },
                    count = pageSize
                )
            else
                emptyList()
        }

        val clubsInfo = fetchClubs(userId, ignoreList)

        val newClubsRawVideosJob = async {
            if (clubsInfo.newSubscribedClubsIds.any())
                videosRepository.fetchVideos(
                    groupIds = clubsInfo.newSubscribedClubsIds.map { it },
                    count = pageSize
                )
            else
                emptyList()
        }

        val rawVideos = rawVideosJob.await()
            .filter { it.ownerId?.value !in clubsInfo.unsubscribedClubsIds }
        val newClubsRawVideos = newClubsRawVideosJob.await()

        withContext(Dispatchers.Default) {
            UserClubsWithVideos(clubsInfo.onlineClubs, rawVideos + newClubsRawVideos)
        }
    }


    private suspend fun fetchClubs(userId: Long, ignoreList: List<Long>): ClubsInfo {
        val clubs = clubsRepository.fetchClubs(userId).filter { it.id.value !in ignoreList }
        val cachedClubIds = clubsLocalDataSource.loadClubsIds()
        return withContext(Dispatchers.Default) {
            val onlineClubIds = clubs.map { it.id.value }
            clubsLocalDataSource.saveClubsIds(onlineClubIds)

            ClubsInfo(clubs, cachedClubIds)
        }
    }

    private class ClubsInfo(
        val onlineClubs: List<GroupsGroupFull>,
        val cachedClubIds: List<Long>
    ) {
        val onlineClubIds = onlineClubs.map { it.id.value }
        val unsubscribedClubsIds = cachedClubIds.filter { it !in onlineClubIds }
        val newSubscribedClubsIds = onlineClubIds.filter { it !in cachedClubIds }
    }
}

data class UserClubsWithVideos(val clubs: List<GroupsGroupFull>, val videos: List<VideoVideoFull>)