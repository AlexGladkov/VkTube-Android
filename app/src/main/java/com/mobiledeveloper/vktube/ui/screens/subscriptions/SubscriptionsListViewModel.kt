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
import kotlinx.coroutines.*
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
            is SubscriptionsListEvent.Back -> saveIgnoreListAndGoBack()
        }
    }

    private fun saveIgnoreListAndGoBack() {
        viewModelScope.launch {

            val ignoreList = viewState.items.filter { it.isIgnored }.map { it.groupId }
            groupsLocalDataSource.saveIgnoreList(ignoreList)

            viewAction = SubscriptionsListAction.BackToFeed
        }
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
                    it.mapToSubscriptionCellModel(
                        name = it.name.orEmpty(),
                        imageUrl = it.photo200.orEmpty(),
                        id = it.id.value,
                        isIgnored = ignoreList.contains(it.id.value)
                    )
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

    fun GroupsGroupFull.mapToSubscriptionCellModel(
        imageUrl: String,
        name: String,
        id: Long,
        isIgnored: Boolean
    ): SubscriptionCellModel {
        return SubscriptionCellModel(
            groupId = id,
            groupIcon = imageUrl,
            groupName = name,
            isIgnored = isIgnored
        )
    }
}