@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsFields
import com.vk.sdk.api.groups.dto.GroupsGroupFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClubsRepository @Inject constructor() {

    suspend fun fetchClubs(userId: Long): List<GroupsGroupFull> =
        withContext(Dispatchers.IO) {
            var requestedCount = 0
            val result = mutableListOf<GroupsGroupFull>()
            while (true) {
                val request = GroupsService().groupsGetExtended(
                    userId = UserId(userId),
                    count = PAGE_SIZE,
                    offset = requestedCount,
                    fields = listOf(GroupsFields.MEMBERS_COUNT)
                )
                val response = VK.executeSync(request)

                result.addAll(response.items)
                requestedCount += PAGE_SIZE
                if (requestedCount >= response.count) break
            }
            result.distinctBy { it.id }
        }

    companion object {
        private const val PAGE_SIZE = 100
    }

}
