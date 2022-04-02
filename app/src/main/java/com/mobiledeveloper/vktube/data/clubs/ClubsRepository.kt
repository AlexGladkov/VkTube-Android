@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsFields
import com.vk.sdk.api.groups.dto.GroupsGetObjectExtendedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClubsRepository @Inject constructor() {

    suspend fun fetchClubs(userId: Long): GroupsGetObjectExtendedResponse =
        withContext(Dispatchers.IO) {
            val request = GroupsService().groupsGetExtended(
                userId = UserId(userId),
                count = 100,
                fields = listOf(GroupsFields.MEMBERS_COUNT)
            )

            VK.executeSync(request)
        }
}
