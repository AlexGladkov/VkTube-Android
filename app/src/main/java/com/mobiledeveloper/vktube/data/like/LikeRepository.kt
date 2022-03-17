package com.mobiledeveloper.vktube.data.like

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.dto.common.id.UserId
import com.vk.dto.common.id.abs
import com.vk.dto.common.id.unaryMinus
import com.vk.sdk.api.likes.LikesService
import com.vk.sdk.api.likes.dto.LikesAddResponse
import com.vk.sdk.api.likes.dto.LikesDeleteResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LikeRepository @Inject constructor() {

    suspend fun unlike(videoId: Long, ownerId: Long) {
        return suspendCoroutine { continuation ->
            VK.execute(
                LikesService().likesDelete(
                    type = "video",
                    itemId = videoId.toInt(),
                    ownerId = UserId(ownerId).abs().unaryMinus()
                ),
                object : VKApiCallback<LikesDeleteResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWithException(error)
                    }
                    override fun success(result: LikesDeleteResponse) {
                        continuation.resume(Unit)
                    }
                }
            )
        }
    }
    suspend fun like(videoId: Long, ownerId: Long) {
        return suspendCoroutine { continuation ->
            VK.execute(
                LikesService().likesAdd(
                    type = "video",
                    itemId = videoId.toInt(),
                    ownerId = UserId(ownerId).abs().unaryMinus()
                ),
                object : VKApiCallback<LikesAddResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWithException(error)
                    }
                    override fun success(result: LikesAddResponse) {
                        continuation.resume(Unit)
                    }
                }
            )
        }
    }
}