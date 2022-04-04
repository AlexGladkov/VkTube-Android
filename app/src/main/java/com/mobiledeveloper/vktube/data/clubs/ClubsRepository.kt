@file:Suppress("BlockingMethodInNonBlockingContext")

package com.mobiledeveloper.vktube.data.clubs

import com.vk.api.sdk.VK
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.groups.GroupsService
import com.vk.sdk.api.groups.dto.GroupsFields
import com.vk.sdk.api.groups.dto.GroupsFilter
import com.vk.sdk.api.groups.dto.GroupsGroupFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClubsRepository @Inject constructor() {

    /**
     * на тек. момент (02.04.2022) из-за бага в апи вк метод возвращает не все группы
     * баг: при запросе групп с оффсетом метод возвращает группы из прошлых оффсетов
     * фильтры GroupsFilter.GROUPS, GroupsFilter.PUBLICS позволяют эту проблему решить
     */

    suspend fun fetchClubs(userId: Long, ignoreList: List<Long> = emptyList()): List<GroupsGroupFull> =
        withContext(Dispatchers.IO) {
            var requestedCount = 0
            val result = mutableListOf<GroupsGroupFull>()
            while (true) {
                val request = GroupsService().groupsGetExtended(
                    userId = UserId(userId),
                    count = PAGE_SIZE,
                    offset = requestedCount,
                    fields = listOf(GroupsFields.MEMBERS_COUNT),
                    filter = listOf(GroupsFilter.GROUPS, GroupsFilter.PUBLICS)
                )
                val response = VK.executeSync(request)

                result.addAll(response.items)
                requestedCount += PAGE_SIZE
                if (requestedCount >= response.count) break
            }
            result.filter { it.id.value !in ignoreList }.distinctBy { it.id }
        }

    companion object {
        private const val PAGE_SIZE = 100
    }

}
