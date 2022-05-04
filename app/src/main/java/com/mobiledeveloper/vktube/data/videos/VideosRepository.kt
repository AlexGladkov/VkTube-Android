@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.videos

import com.google.gson.*
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
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
import com.vk.dto.common.id.unaryMinus
import com.vk.sdk.api.GsonHolder
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetResponse
import com.vk.sdk.api.video.dto.VideoVideoFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import javax.inject.Inject
import kotlin.math.absoluteValue

class VideosRepository @Inject constructor(val videoLocalDataSource: VideosLocalDataSource) {
    suspend fun fetchVideos(
        groupIds: List<Long>,
        count: Int
    ): List<VideoVideoFull> {
        return fetchBatchVideoFulls(groupIds.map {
            LoadSettings(
                groupId = -it.absoluteValue,
                count = count,
                offset = 0
            )
        })
    }

    suspend fun fetchVideos(
        groupId: Long,
        count: Int,
        offset: Int = 0
    ): List<VideoVideoFull> = withContext(Dispatchers.IO) {
        val request = VideoService().videoGet(
            count = count,
            ownerId = -UserId(groupId.absoluteValue),
            offset = offset
        )
        fetchVideo(request).items
    }

    private suspend fun fetchVideo(videoGetRequest: VKRequest<VideoGetResponse>): VideoGetResponse =
        withContext(Dispatchers.IO) {
            VK.executeSync(videoGetRequest)
        }

    suspend fun fetchBatchVideoFulls(
        groupsParams: List<LoadSettings>
    ): List<VideoVideoFull> {
        val result = try {
            fetchBatchVideos(groupsParams)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            emptyList()
        }
        return withContext(Dispatchers.Default) {
            result.map { response ->
                response.items
            }.flatten()
        }
    }

    private suspend fun fetchBatchVideos(
        groupsParams: List<LoadSettings>
    ): List<VideoGetResponse> =
        withContext(Dispatchers.IO) {
            VK.executeSync(GroupsVideosCommand(groupsParams))
        }

    class GroupsVideosCommand(
        private val groupsParams: List<LoadSettings>
    ) : ApiCommand<List<VideoGetResponse>>() {
        override fun onExecute(manager: VKApiManager): List<VideoGetResponse> {
            return groupsParams
                .chunked(CHUNK_LIMIT)
                .map { chunk ->
                    val code = chunk
                        .map { settings ->
                            val params = mapOf(
                                "owner_id" to settings.groupId,
                                "count" to settings.count,
                                "offset" to settings.offset
                            ).map { "${it.key}:${it.value}" }.joinToString(",")
                            """API.video.get({$params})"""
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

    fun saveVideo(video: VideoCellModel){
        val videos = videoLocalDataSource.loadVideos() as MutableList
        videos.add(video)
        videoLocalDataSource.saveVideos(videos)
    }

    fun loadVideos(): List<VideoCellModel> {
        return videoLocalDataSource.loadVideos()
    }

    fun clearVideos() {
        videoLocalDataSource.clearVideos()
    }
    data class LoadSettings(val groupId: Long, val count: Int, val offset: Int = 0)
}
