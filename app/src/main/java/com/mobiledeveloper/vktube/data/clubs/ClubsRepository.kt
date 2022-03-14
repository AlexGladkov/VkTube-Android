package com.mobiledeveloper.vktube.data.clubs

import com.mobiledeveloper.vktube.ui.common.cell.mapToVideoCellModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.requests.VKRequest
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsGetResponse
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

    suspend fun fetchVideos(clubs: List<UserId>, count: Int): List<VideoDataModel> {
        val requests = clubs.map {
            println("Video get ${it.value}")
            VideoService().videoGet(count = count, ownerId = it)
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
        listResponse.forEach {
            videoItems.addAll(it.items.map { videoFull ->
                videoFull.mapToVideoDataModel(
                    userName = "",
                    userImage = ""
                )
            })
        }

        videoItems.sortBy { it.item.addingDate }
        return videoItems.reversed()
    }

    suspend fun fetchClubs(userId: Long): GroupsGetResponse {
        return suspendCoroutine { continuation ->
            VK.execute(
                GroupsService().groupsGet(userId = UserId(userId), count = 100),
                object : VKApiCallback<GroupsGetResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWithException(error)
                    }

                    override fun success(result: GroupsGetResponse) {
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