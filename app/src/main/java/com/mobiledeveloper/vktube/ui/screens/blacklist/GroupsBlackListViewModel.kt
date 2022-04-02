package com.mobiledeveloper.vktube.ui.screens.blacklist

import androidx.lifecycle.viewModelScope
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.data.clubs.ClubsLocalDataSource
import com.mobiledeveloper.vktube.data.clubs.ClubsRepository
import com.mobiledeveloper.vktube.data.user.UserRepository
import com.mobiledeveloper.vktube.ui.common.cell.mapToGroupCellModel
import com.mobiledeveloper.vktube.ui.screens.blacklist.models.BlackListAction
import com.mobiledeveloper.vktube.ui.screens.blacklist.models.BlackListEvent
import com.mobiledeveloper.vktube.ui.screens.blacklist.models.BlackListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class GroupsBlackListViewModel @Inject constructor(
    private val groupsRepository: ClubsRepository,
    private val groupsLocalDataSource: ClubsLocalDataSource,
    private val userRepository: UserRepository
) : BaseViewModel<BlackListState, BlackListAction, BlackListEvent>(initialState = BlackListState()) {


    override fun obtainEvent(viewEvent: BlackListEvent) {
        when (viewEvent) {
            BlackListEvent.ScreenShown -> onScreenShown()
            BlackListEvent.ClearAction -> clearAction()
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

                val groups = groupsRepository.fetchClubs(userId).map {
                    it.mapToGroupCellModel(
                        name = it.name ?: "",
                        imageUrl = it.photo200 ?: "",
                        id = it.id.value
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
}