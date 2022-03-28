@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.chain.ChainArgs
import com.vk.api.sdk.chain.MethodChainCall
import com.vk.api.sdk.internal.ApiCommand
import com.vk.api.sdk.okhttp.OkHttpMethodCall
import com.vk.api.sdk.requests.VKRequest
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.GsonHolder
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsFields
import com.vk.sdk.api.groups.dto.GroupsGetObjectExtendedResponse
import com.vk.sdk.api.video.dto.VideoGetResponse
import com.vk.sdk.api.video.dto.VideoVideoFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClubsRepository @Inject constructor() {
    suspend fun fetchVideos(
        groupIds: List<UserId>,
        count: Int
    ): List<VideoVideoFull> = withContext(Dispatchers.IO) {
        val result = try {
            fetchBatchVideos(groupIds.map { -it.value }, count)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            emptyList()
        }
        withContext(Dispatchers.Default) {
            result.map { response ->
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

    private suspend fun fetchBatchVideos(
        groupIds: List<Long>,
        count: Int
    ): List<VideoGetResponse> =
        withContext(Dispatchers.IO) {
            VK.executeSync(GroupsVideosCommand(groupIds, count = count, offset = 0))
        }

    class GroupsVideosCommand(
        private val ownerIds: List<Long>,
        private val count: Int,
        private val offset: Int
    ) : ApiCommand<List<VideoGetResponse>>() {
        override fun onExecute(manager: VKApiManager): List<VideoGetResponse> {
            return ownerIds.toList().chunked(CHUNK_LIMIT).map { chunk ->
                val chunkIds = chunk.joinToString(separator = ",")

                val call = VKMethodCall.Builder()
                    .method("execute.groupsVideos")
                    .args("groupIds", chunkIds)
                    .args("count", count)
                    .args("offset", offset)
                    .version(manager.config.version)
                    .build()
                try {
                    GroupIdsMethodChainCall(
                        manager,
                        call
                    ).call(ChainArgs())?.filterNotNull()?: emptyList()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    emptyList()
                }
            }.flatten()
        }

        /**
         * response without check execute_errors
         */
        class GroupIdsMethodChainCall(manager: VKApiManager, call: VKMethodCall) :
            MethodChainCall<List<VideoGetResponse?>>(
                manager,
                manager.executor,
                OkHttpMethodCall.Builder().from(call),
                manager.config.deviceId.value,
                manager.config.lang,
                ResponseApiParser()
            ) {
            /**
             * response without check api execute_errors
             */
            override fun runRequest(mc: OkHttpMethodCall): List<VideoGetResponse?> {
                val response = okHttpExecutor.execute(mc).response
                return parser?.parse(response)?: emptyList()
            }
        }

        private class ResponseApiParser : VKApiResponseParser<List<VideoGetResponse?>> {
            override fun parse(response: String): List<VideoGetResponse?> {
                return try {
                    GsonHolder.gson.fromJson(response, ApiVideoGetResponse::class.java).response
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    emptyList()
                }
            }
        }

        private data class ApiVideoGetResponse(val response: List<VideoGetResponse?>)

        companion object {
            private const val CHUNK_LIMIT = 25
        }
    }
}
