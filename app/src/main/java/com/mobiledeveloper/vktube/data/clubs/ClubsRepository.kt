@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.clubs

import com.google.gson.*
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
import java.lang.reflect.Type
import javax.inject.Inject


class ClubsRepository @Inject constructor() {
    suspend fun fetchVideos(
        groupIds: List<Long>,
        count: Int
    ): List<VideoVideoFull> = withContext(Dispatchers.IO) {
        val result = try {
            fetchBatchVideos(groupIds.map { -it }, count)
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
                val code = chunk
                    .map { id ->
                        "API.video.get({\"owner_id\":$id,\"count\":$count,\"offset\":$offset})"
                    }.toString()

                val call = VKMethodCall.Builder()
                    .method("execute")
                    .args("code", "return $code;")
                    .version(manager.config.version)
                    .build()
                try {
                    GroupIdsMethodChainCall(
                        manager,
                        call
                    ).call(ChainArgs())?.filterNotNull() ?: emptyList()
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                    emptyList()
                }
            }.flatten()
        }

        /**
         * standard MethodChainCall check response field 'execute_errors' and throw exception when
         * response contains any error.
         * batch of group videos requests may contains execute_error for some group videos.
         * GroupIdsMethodChainCall overrides runRequest methods for ignore that errors.
         */
        private class GroupIdsMethodChainCall(manager: VKApiManager, call: VKMethodCall) :
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
                return parser?.parse(response) ?: emptyList()
            }
        }

        private class ResponseApiParser : VKApiResponseParser<List<VideoGetResponse?>> {
            override fun parse(response: String): List<VideoGetResponse?> {
                return try {
                    gson.fromJson(response, ApiVideoGetResponse::class.java).response
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    emptyList()
                }
            }

            companion object {
                private val gson = GsonBuilder()
                    .registerTypeAdapter(ApiVideoGetResponse::class.java, ResponseDeserializer())
                    .create()
            }
        }

        private data class ApiVideoGetResponse(val response: List<VideoGetResponse?>)

        private class ResponseDeserializer : JsonDeserializer<ApiVideoGetResponse> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): ApiVideoGetResponse {
                val result = mutableListOf<VideoGetResponse?>()
                if (json == null) return ApiVideoGetResponse(result)

                val jsonObject: JsonObject = json.asJsonObject
                if (jsonObject.has("response")) {
                    jsonObject.getAsJsonArray("response").forEach {
                        if (it == null || it.isJsonPrimitive)
                            result.add(null)
                        else {
                            result.add(GsonHolder.gson.fromJson(it, VideoGetResponse::class.java))
                        }
                    }
                }

                return ApiVideoGetResponse(result)
            }
        }

        companion object {
            private const val CHUNK_LIMIT = 25
        }
    }
}
