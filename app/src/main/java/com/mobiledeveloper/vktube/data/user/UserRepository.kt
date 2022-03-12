package com.mobiledeveloper.vktube.data.user

import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFields
import com.vk.sdk.api.users.dto.UsersUserFull
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoGetCommentsResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource
) {

    suspend fun fetchAndSaveUser(): Boolean {
        return suspendCoroutine { continuation ->
            VK.execute(
                UsersService().usersGet(
                    userIds = listOf(VK.getUserId()),
                    fields = listOf(
                        UsersFields.BIRTHDATE, UsersFields.PHOTO_200,
                        UsersFields.SCREEN_NAME
                    )
                ),
                object : VKApiCallback<List<UsersUserFull>> {
                    override fun fail(error: Exception) {
                        VK.logout()
                        continuation.resume(false)
                    }

                    override fun success(result: List<UsersUserFull>) {
                        try {
                            localDataSource.saveUser(result.first())
                            continuation.resume(true)
                        } catch (e: java.lang.Exception) {
                            continuation.resume(false)
                        }
                    }
                })
        }
    }

    fun fetchLocalUser(): StoredUser {
        return localDataSource.loadUser()
    }
}