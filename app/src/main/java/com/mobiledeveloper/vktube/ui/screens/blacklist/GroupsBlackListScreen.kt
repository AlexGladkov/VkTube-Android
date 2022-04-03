package com.mobiledeveloper.vktube.ui.screens.blacklist

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.*
import com.mobiledeveloper.vktube.ui.screens.blacklist.models.BlackListAction
import com.mobiledeveloper.vktube.ui.screens.blacklist.models.BlackListEvent
import com.mobiledeveloper.vktube.ui.screens.blacklist.models.BlackListState
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun GroupsBlackListScreen(
    navController: NavController,
    viewModel: GroupsBlackListViewModel
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(initial = null)

    BackHandler(enabled = true){
        viewModel.obtainEvent(BlackListEvent.Back)
    }

    Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
        GroupsView(
            viewState = viewState,
            onGroupClick = {},
            onScroll = {}
    )}

    LaunchedEffect(key1 = viewAction, block = {
        viewModel.obtainEvent(BlackListEvent.ClearAction)
        when (viewAction) {
            is BlackListAction.BackToFeed -> {
                navController.navigate(NavigationTree.Root.Main.name)
            }
            null -> {
                // ignore
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        viewModel.obtainEvent(BlackListEvent.ScreenShown)
    })
}

@Composable
private fun GroupsView(
    viewState: BlackListState, onGroupClick: (GroupCellModel) -> Unit,
    onScroll: (lastVisibleItemIndex: Int) -> Unit
) {
    val configuration = LocalConfiguration.current

    val previewSize = remember(configuration.orientation) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val height = configuration.screenHeightDp.dp / 3
            Size(height * 16 / 9, height)
        } else {
            Size(configuration.screenWidthDp.dp + 1.dp, configuration.screenWidthDp.dp / 16 * 9)
        }
    }
    if (viewState.loading) {
        LoadingView(previewSize)
    } else {
        GroupView(viewState, previewSize, onGroupClick, onScroll)
    }
}

@Composable
private fun GroupView(
    viewState: BlackListState,
    previewSize: Size,
    onGroupClick: (GroupCellModel) -> Unit,
    onScroll: (lastVisibleItemIndex: Int) -> Unit
) {
    val state: LazyListState = rememberLazyListState()
    val lastVisibleItemIndex = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    LaunchedEffect(lastVisibleItemIndex) {
        onScroll(lastVisibleItemIndex)
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(
            items = viewState.items,
            key = { item -> item.groupId }
        ) { viewModel ->
            GroupCell(viewModel, previewSize) {
                onGroupClick.invoke(viewModel)
            }
        }
    }
}

@Composable
private fun LoadingView(previewSize: Size) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(10) {
            item {
                GroupGrayCell(previewSize)
            }
        }
    }
}