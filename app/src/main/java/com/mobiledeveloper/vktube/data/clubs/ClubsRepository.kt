@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.api.sdk.requests.VKRequest
import com.vk.dto.common.id.UserId
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

class ClubsRepository @Inject constructor() {
    suspend fun fetchVideos(
        groupIds: List<UserId>,
        count: Int
    ): List<VideoVideoFull> = withContext(Dispatchers.IO) {
        val requests = groupIds.map {
            VideoService().videoGet(count = count, ownerId = -it)
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
                response.items
            }.flatten().sortedByDescending { it.addingDate }
        }
    }

    suspend fun fetchClubs(userId: Long): GroupsGetObjectExtendedResponse =
        withContext(Dispatchers.IO) {
            val request = GroupsService().groupsGetExtended(
                userId = UserId(userId),
                count = 100,
                fields = listOf(GroupsFields.MEMBERS_COUNT)
            )

            VK.executeSync(request)
        }

    private suspend fun fetchVideo(videoGetRequest: VKRequest<VideoGetResponse>): VideoGetResponse =
        withContext(Dispatchers.IO) {
            VK.executeSync(videoGetRequest)
        }
}