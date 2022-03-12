package com.mobiledeveloper.vktube.data.user

import android.content.Context
import android.content.SharedPreferences
import com.mobiledeveloper.vktube.R
import com.vk.sdk.api.users.dto.UsersUserFull
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class StoredUser(
    val userId: Long,
    val name: String,
    val avatar: String
)

fun UsersUserFull.mapToStoredUser(): StoredUser {
    return StoredUser(
        userId = id.value,
        name = screenName.orEmpty(),
        avatar = photo200.orEmpty()
    )
}

class UserLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.app_name), 0)
    }

    private val json by lazy {
        Json
    }

    fun saveUser(user: UsersUserFull) {
        val storedUser = user.mapToStoredUser()
        val storedString = json.encodeToString(storedUser)

        sharedPreferences.edit()
            .putString(USER_KEY, storedString)
            .apply()
    }

    fun loadUser(): StoredUser {
        val storedString = sharedPreferences.getString(USER_KEY, "").orEmpty()
        return json.decodeFromString(storedString)
    }

    companion object {
        private const val USER_KEY = "user_key"
    }
}