package com.mobiledeveloper.vktube.data.comments

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.dto.common.id.UserId
import com.vk.dto.common.id.abs
import com.vk.dto.common.id.unaryMinus
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetCommentsResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CommentsRepository @Inject constructor() {

    suspend fun fetchCommentsForVideo(ownerId: Long, videoId: Long, count: Int): VideoGetCommentsResponse {
        return suspendCoroutine { continuation ->
            VK.execute(
                VideoService().videoGetComments(
                    videoId = videoId.toInt(),
                    count = count,
                    ownerId = UserId(ownerId).abs().unaryMinus()
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

    suspend fun addCommentForVideo(ownerId: Long, videoId: Long, comment: String) {
        return suspendCoroutine {
            VK.execute(
                VideoService().videoCreateComment(
                    videoId = videoId.toInt(),
                    ownerId = UserId(ownerId).abs().unaryMinus(),
                    message = comment
                ),
                object : VKApiCallback<Int>{
                    override fun fail(error: Exception) {
                        println("Error Comment $error")
                        it.resumeWithException(error)
                    }
                    override fun success(result: Int) {
                        it.resume(Unit)
                    }
                }
            )
        }
    }
}