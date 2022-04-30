package com.mobiledeveloper.vktube.ui.screens.subscriptions

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.clubs.ClubsLocalDataSource
import com.mobiledeveloper.vktube.data.clubs.ClubsRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.navigation.items
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

    private var sortBy:SortBy = SortBy.NameAndIgnored

    sealed class SortBy(){
        object Name: SortBy()
        object Ignored: SortBy()
        object NameAndIgnored: SortBy()
    }

    override fun obtainEvent(viewEvent: SubscriptionsListEvent) {
        when (viewEvent) {
            SubscriptionsListEvent.ScreenShown -> onScreenShown()
            SubscriptionsListEvent.ClearAction -> clearAction()
            is SubscriptionsListEvent.Back -> goBack()
            is SubscriptionsListEvent.GroupClick -> toggleIgnored(viewEvent.item)
            SubscriptionsListEvent.ToggleAll -> toggleAll()
        }
    }

    private fun goBack() {
        clearAction()
        viewAction = SubscriptionsListAction.BackToFeed
    }

    private fun remove(id: List<Long>) {
        viewModelScope.launch {
            val ignoreList = groupsLocalDataSource.loadIgnoreList() as MutableList
            id.forEach { ignoreList.remove(it) }
            groupsLocalDataSource.saveIgnoreList(ignoreList)
        }
    }

    private fun add(id: List<Long>) {
        viewModelScope.launch {
            val ignoreList = groupsLocalDataSource.loadIgnoreList() as MutableList
            id.forEach { ignoreList.add(it) }
            groupsLocalDataSource.saveIgnoreList(ignoreList)
        }
    }

    private fun toggleIgnored(item:SubscriptionCellModel) {
        if (item.isIgnored) remove(listOf(item.groupId)) else add(listOf(item.groupId))
        val updatedItems =
            viewState.items.map { if (it.groupId == item.groupId) item.copy(isIgnored = !item.isIgnored) else it }

        viewState = viewState.copy(
            items = sort(updatedItems),
            ignoreAll = areAllIgnored(updatedItems)
        )
    }

    private fun toggleAll() {
        val allIgnored = areAllIgnored(viewState.items)
        viewState = viewState.copy(
            items = sort(if (allIgnored) watchAll() else ignoreAll()),
            ignoreAll = !allIgnored
        )
    }

    private fun watchAll(): List<SubscriptionCellModel> {
        remove(viewState.items.map { it.groupId })
        return viewState.items.map { it.copy(isIgnored = false) }
    }

    private fun ignoreAll(): List<SubscriptionCellModel> {
        add(viewState.items.map { it.groupId })
        return viewState.items.map { it.copy(isIgnored = true) }
    }

    private fun areAllIgnored(items: List<SubscriptionCellModel>): Boolean{
        return items.all { it.isIgnored }
    }

    private fun sort(items: List<SubscriptionCellModel>): List<SubscriptionCellModel> {
        return when(sortBy){
            is SortBy.Name -> items.sortedBy { it.groupName }
            is SortBy.Ignored -> items.sortedBy { it.isIgnored }
            is SortBy.NameAndIgnored -> items.sortedWith(compareBy({ it.isIgnored }, { it.groupName }))
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
                   mapToSubscriptionCellModel(it, ignoreList)
                }

                viewState = viewState.copy(
                    items = sort(groups),
                    loading = false,
                    ignoreAll = areAllIgnored(groups)
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