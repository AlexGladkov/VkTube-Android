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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SubscriptionsListViewModel @Inject constructor(
    private val groupsRepository: ClubsRepository,
    private val groupsLocalDataSource: ClubsLocalDataSource,
    private val userRepository: UserRepository
) : BaseViewModel<SubscriptionsListState, SubscriptionsListAction, SubscriptionsListEvent>(initialState = SubscriptionsListState()) {

    private var groups: List<SubscriptionCellModel> = emptyList()

    private var sortBy: SortBy = SortBy.NameAndIgnored

    enum class SortBy { Name, Ignored, NameAndIgnored }

    override fun obtainEvent(viewEvent: SubscriptionsListEvent) {
        when (viewEvent) {
            SubscriptionsListEvent.ScreenShown -> onScreenShown()
            SubscriptionsListEvent.ClearAction -> clearAction()
            is SubscriptionsListEvent.Back -> goBack()
            is SubscriptionsListEvent.GroupClicked -> toggleIgnored(viewEvent.item)
            is SubscriptionsListEvent.SearchTextChanged -> search(viewEvent.searchBy)
            SubscriptionsListEvent.ToggleAllisClicked -> toggleAll()
        }
    }

    private fun goBack() {
        clearAction()
        viewAction = SubscriptionsListAction.BackToFeed
    }

    private fun search(searchBy: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val ignoreList = groupsLocalDataSource.loadIgnoreList()
            groups = groups.map {
                it.copy(isIgnored = ignoreList.contains(it.groupId))
            }
            val searchString = searchBy.lowercase(Locale.getDefault())
            val items = sort(groups.filter {
                it.groupName.lowercase(Locale.getDefault())
                    .contains(searchString)
            })
            viewState = viewState.copy(
                items = items,
                allAreIgnored = areAllIgnored(items)
            )
        }
    }

    private fun remove(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.Default) {
            val ignoreList = groupsLocalDataSource.loadIgnoreList() - ids
            groupsLocalDataSource.saveIgnoreList(ignoreList)
        }
    }

    private fun add(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.Default) {
            val ignoreList = groupsLocalDataSource.loadIgnoreList() + ids
            groupsLocalDataSource.saveIgnoreList(ignoreList)
        }
    }

    private fun toggleIgnored(item:SubscriptionCellModel) {
        if (item.isIgnored) remove(listOf(item.groupId)) else add(listOf(item.groupId))
        val updatedItems =
            viewState.items.map { if (it.groupId == item.groupId) item.copy(isIgnored = !item.isIgnored) else it }

        viewState = viewState.copy(
            items = sort(updatedItems),
            allAreIgnored = areAllIgnored(updatedItems)
        )
    }

    private fun toggleAll() {
        viewModelScope.launch(Dispatchers.Default) {
            val allIgnored = areAllIgnored(viewState.items)
            viewState = viewState.copy(
                items = sort(if (allIgnored) watchAll(viewState.items) else ignoreAll(viewState.items)),
                allAreIgnored = !allIgnored
            )
        }
    }

    private fun watchAll(items: List<SubscriptionCellModel>): List<SubscriptionCellModel> {
        remove(items.map { it.groupId })
        return viewState.items.map { it.copy(isIgnored = false) }
    }

    private fun ignoreAll(items: List<SubscriptionCellModel>): List<SubscriptionCellModel> {
        add(items.map { it.groupId })
        return viewState.items.map { it.copy(isIgnored = true) }
    }

    private fun areAllIgnored(items: List<SubscriptionCellModel>): Boolean{
        return items.all { it.isIgnored }
    }

    private fun sort(items: List<SubscriptionCellModel>): List<SubscriptionCellModel> {
        return when(sortBy){
            SortBy.Name -> items.sortedBy { it.groupName }
            SortBy.Ignored -> items.sortedBy { it.isIgnored }
            SortBy.NameAndIgnored -> items.sortedWith(compareBy({ it.isIgnored }, { it.groupName }))
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
        viewModelScope.launch(Dispatchers.Default) {
            viewState = viewState.copy(loading = true)
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

                this@SubscriptionsListViewModel.groups = groups
                viewState = viewState.copy(
                    items = sort(groups),
                    loading = false,
                    allAreIgnored = areAllIgnored(groups)
                )

            } catch (ex: Exception) {
                viewState = viewState.copy(loading = false)
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