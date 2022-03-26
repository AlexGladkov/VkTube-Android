package com.mobiledeveloper.vktube.data.user

import com.vk.api.sdk.VK
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("BlockingMethodInNonBlockingContext")
class UserRepository @Inject constructor(
    private val localDataSource: UserLocalDataSource
) {
    suspend fun fetchAndSaveUser(): Boolean = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val request = UsersService().usersGet(
                userIds = listOf(VK.getUserId()),
                fields = listOf(
                    UsersFields.BIRTHDATE, UsersFields.PHOTO_200,
                    UsersFields.SCREEN_NAME
                )
            )
            try {
                val result = VK.executeSync(request)
                println("User ${result.first()}")
                localDataSource.saveUser(result.first())
                continuation.resume(true)
            } catch (ex: Throwable) {
                VK.logout()
                continuation.resume(false)
            }
        }
    }

    fun fetchLocalUser(): StoredUser {
        return localDataSource.loadUser()
    }
}