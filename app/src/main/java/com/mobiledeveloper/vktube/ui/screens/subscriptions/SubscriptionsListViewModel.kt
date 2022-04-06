package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.clubs.ClubsLocalDataSource
import com.mobiledeveloper.vktube.data.clubs.ClubsRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionCellModel
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListAction
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListEvent
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListState
import com.vk.sdk.api.groups.dto.GroupsGroupFull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsListViewModel @Inject constructor(
    private val groupsRepository: ClubsRepository,
    private val groupsLocalDataSource: ClubsLocalDataSource,
    private val userRepository: UserRepository
) : BaseViewModel<SubscriptionsListState, SubscriptionsListAction, SubscriptionsListEvent>(initialState = SubscriptionsListState()) {

    override fun obtainEvent(viewEvent: SubscriptionsListEvent) {
        when (viewEvent) {
            SubscriptionsListEvent.ScreenShown -> onScreenShown()
            SubscriptionsListEvent.ClearAction -> clearAction()
            is SubscriptionsListEvent.Back -> goBack()
            is SubscriptionsListEvent.GroupClick -> toggleIgnored(viewEvent.item)
        }
    }

    private fun goBack() {
        clearAction()
        viewAction = SubscriptionsListAction.BackToFeed
    }

    private fun remove(id: Long) {
        viewModelScope.launch {
            val ignoreList = groupsLocalDataSource.loadIgnoreList() as MutableList
            ignoreList.remove(id)
            groupsLocalDataSource.saveIgnoreList(ignoreList)
        }
    }

    private fun add(id: Long) {
        viewModelScope.launch {
            val ignoreList = groupsLocalDataSource.loadIgnoreList() as MutableList
            ignoreList.add(id)
            groupsLocalDataSource.saveIgnoreList(ignoreList)
        }
    }

    private fun toggleIgnored(item:SubscriptionCellModel) {
        if (item.isIgnored) remove(item.groupId) else add(item.groupId)
        viewState = viewState.copy(
            items = viewState.items.map { if (it.groupId == item.groupId) item.copy(isIgnored = !item.isIgnored) else it }
        )
    }

    private fun clearAction() {
        viewModelScope.launch {
            viewAction = null
        }
    }

    private fun onScreenShown() {
        if (viewState.items.any()) return
        fetchGroups()
    }

    private fun fetchGroups() {
        viewModelScope.launch {
            viewState = viewState.copy(
                loading = true
            )
            try {
                val userId = try {
                    userRepository.fetchLocalUser().userId
                } catch (e: Exception) {
                    userRepository.fetchAndSaveUser()
                    userRepository.fetchLocalUser().userId
                }

                val ignoreList = groupsLocalDataSource.loadIgnoreList()

                val groups = groupsRepository.fetchClubs(userId).map {
                   mapToSubscriptionCellModel(it, ignoreList)
                }

                viewState = viewState.copy(
                    items = groups,
                    loading = false
                )

            } catch (ex: Exception) {
                viewState = viewState.copy(
                    loading = false
                )
            }
        }
    }

    private fun mapToSubscriptionCellModel(group: GroupsGroupFull, ignoreList: List<Long>): SubscriptionCellModel {
        return SubscriptionCellModel(
            groupName = group.name.orEmpty(),
            groupIcon = group.photo200.orEmpty(),
            groupId = group.id.value,
            isIgnored = ignoreList.contains(group.id.value)
        )
    }
}