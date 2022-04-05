package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.requests.VKRequest
import com.vk.dto.common.id.UserId
import com.vk.dto.common.id.abs
import com.vk.dto.common.id.unaryMinus
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsGetObjectExtendedResponse
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetResponse
import com.vk.sdk.api.video.dto.VideoVideoFull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class VideoDataModel(
    val item: VideoVideoFull,
    val userImage: String,
    val userName: String
)

fun VideoVideoFull.mapToVideoDataModel(
    userImage: String,
    userName: String
) = VideoDataModel(
    item = this,
    userImage = userImage,
    userName = userName
)

class ClubsRepository @Inject constructor() {

    suspend fun fetchVideos(clubs: GroupsGetObjectExtendedResponse, count: Int): List<VideoDataModel> {
        val requests = clubs.items.map {
            VideoService().videoGet(count = count, ownerId = -it.id)
        }

        val listResponse = mutableListOf<VideoGetResponse>()
        requests.forEach {
            try {
                listResponse.add(fetchVideo(it))
            } catch (e: java.lang.Exception) {
                println(e.localizedMessage)
            }
        }

        val videoItems = mutableListOf<VideoDataModel>()
        listResponse.forEach { response ->
            videoItems.addAll(response.items.map { videoFull ->
                val group = clubs.items.firstOrNull { it.id.abs() == videoFull.ownerId?.abs() }
                videoFull.mapToVideoDataModel(
                    userName = group?.name.orEmpty(),
                    userImage = group?.photo100.orEmpty()
                )
            })
        }

        videoItems.sortBy { it.item.addingDate }
        return videoItems.reversed()
    }

    suspend fun fetchClubs(userId: Long): GroupsGetObjectExtendedResponse {
        return suspendCoroutine { continuation ->
            VK.execute(
                GroupsService().groupsGetExtended(userId = UserId(userId), count = 100),
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

    private suspend fun fetchVideo(videoGetRequest: VKRequest<VideoGetResponse>): VideoGetResponse {
        return suspendCoroutine { continuation ->
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