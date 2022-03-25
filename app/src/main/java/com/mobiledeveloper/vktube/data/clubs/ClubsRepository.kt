package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.requests.VKRequest
import com.vk.dto.common.id.UserId
import com.vk.dto.common.id.abs
import com.vk.dto.common.id.unaryMinus
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsFields
import com.vk.sdk.api.groups.dto.GroupsGetObjectExtendedResponse
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetResponse
import com.vk.sdk.api.video.dto.VideoVideoFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class VideoDataModel(
    val item: VideoVideoFull,
    val userImage: String,
    val userName: String,
    val subscribers: Int
)

fun VideoVideoFull.mapToVideoDataModel(
    userImage: String,
    userName: String,
    subscribers: Int
) = VideoDataModel(
    item = this,
    userImage = userImage,
    userName = userName,
    subscribers = subscribers
)

class ClubsRepository @Inject constructor() {
    suspend fun fetchVideos(
        clubs: GroupsGetObjectExtendedResponse,
        count: Int
    ): List<VideoDataModel> = withContext(Dispatchers.IO) {
        val requests = clubs.items.map {
            VideoService().videoGet(count = count, ownerId = -it.id)
        }

        val listResponse = requests.map {
            async {
                try {
                    fetchVideo(it)
                } catch (e: java.lang.Exception) {
                    println(e.localizedMessage)
                    VideoGetResponse(0, emptyList())
                }
            }
        }.awaitAll()

        withContext(Dispatchers.Default) {
            listResponse.map { response ->
                response.items.map { videoFull ->
                    val group = clubs.items.firstOrNull { it.id.abs() == videoFull.ownerId?.abs() }
                    videoFull.mapToVideoDataModel(
                        userName = group?.name.orEmpty(),
                        userImage = group?.photo100.orEmpty(),
                        subscribers = group?.membersCount ?: 0
                    )
                }
            }.flatten().sortedByDescending { it.item.addingDate }
        }
    }

    suspend fun fetchClubs(userId: Long): GroupsGetObjectExtendedResponse =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                VK.execute(
                    GroupsService().groupsGetExtended(
                        userId = UserId(userId),
                        count = 100,
                        fields = listOf(GroupsFields.MEMBERS_COUNT)
                    ),
                    object : VKApiCallback<GroupsGetObjectExtendedResponse> {
                        override fun fail(error: Exception) {
                            continuation.resumeWithException(error)
                        }

                        override fun success(result: GroupsGetObjectExtendedResponse) {
                            continuation.resume(result)
                        }
                    })
            }
        }

    private suspend fun fetchVideo(videoGetRequest: VKRequest<VideoGetResponse>): VideoGetResponse =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                VK.execute(request = videoGetRequest,
                    object : VKApiCallback<VideoGetResponse> {
                        override fun fail(error: Exception) {
                            continuation.resumeWithException(error)
                        }

                        override fun success(result: VideoGetResponse) {
                            continuation.resume(result)
                        }
                    })
            }
        }
}