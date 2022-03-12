package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.requests.VKRequest
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ClubsRepository @Inject constructor() {

    suspend fun fetchVideos(count: Int): VideoGetResponse {
        return suspendCoroutine { continuation ->
            VK.execute(VideoService().videoGet(count = count, ownerId = UserId(-113499203)),
                object : VKApiCallback<VideoGetResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWithException(error)
                    }

                    override fun success(result: VideoGetResponse) {
                        println("result ${result.items.count()}")
                        continuation.resume(result)
                    }
                })
        }
    }
}