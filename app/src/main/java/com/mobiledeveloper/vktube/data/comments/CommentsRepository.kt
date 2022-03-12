package com.mobiledeveloper.vktube.data.comments

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetCommentsResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CommentsRepository @Inject constructor() {

    suspend fun fetchCommentsForVideo(videoId: Long, count: Int): VideoGetCommentsResponse {
        return suspendCoroutine { continuation ->
            VK.execute(
                VideoService().videoGetComments(
                    videoId = videoId.toInt(),
                    count = count,
                    ownerId = UserId(-113499203)
                ),
                object : VKApiCallback<VideoGetCommentsResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWithException(error)
                    }

                    override fun success(result: VideoGetCommentsResponse) {
                        continuation.resume(result)
                    }
                })
        }
    }

    suspend fun addCommentForVideo(userId: Long, videoId: Long, comment: String) {
        return suspendCoroutine {
            VK.execute(
                VideoService().videoCreateComment(
                    videoId = videoId.toInt(),
                    ownerId = UserId(userId),
                    message = comment
                )
            )

            it.resume(Unit)
        }
    }
}