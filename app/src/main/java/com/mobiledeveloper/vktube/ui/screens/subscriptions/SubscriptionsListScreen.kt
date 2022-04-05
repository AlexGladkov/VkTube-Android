package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.*
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionCellModel
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListAction
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListEvent
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListState
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun SubscriptionsListScreen(
    navController: NavController,
    viewModel: SubscriptionsListViewModel
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(initial = null)

    val addToIgnore = fun (id: Long) {
        viewModel.obtainEvent(SubscriptionsListEvent.Add(id))
    }

    val removeFromIgnore = fun (id: Long) {
        viewModel.obtainEvent(SubscriptionsListEvent.Remove(id))
    }

    val toggleIgnore = fun (item: SubscriptionCellModel) {
        viewModel.obtainEvent(SubscriptionsListEvent.ToggleIgnore(item))
    }

    BackHandler(enabled = true){
        viewModel.obtainEvent(SubscriptionsListEvent.Back)
    }

    Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
        SubscriptionsView(
            viewState = viewState,
            addToIgnore,
            toggleIgnore,
            removeFromIgnore
    )}

    LaunchedEffect(key1 = viewAction, block = {
        viewModel.obtainEvent(SubscriptionsListEvent.ClearAction)
        when (viewAction) {
            is SubscriptionsListAction.BackToFeed -> {
                navController.navigate(NavigationTree.Root.Main.name)
            }
            null -> {
                // ignore
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        viewModel.obtainEvent(SubscriptionsListEvent.ScreenShown)
    })

}

@Composable
private fun SubscriptionsView(
    viewState: SubscriptionsListState,
    addToIgnore: (Long) -> Unit,
    toggleIgnore: (SubscriptionCellModel) -> Unit,
    removeFromIgnore: (Long) -> Unit
) {
    if (viewState.loading) {
        LoadingView()
    } else {
        SubscriptionView(viewState, addToIgnore, removeFromIgnore, toggleIgnore)
    }
}

@Composable
private fun SubscriptionView(
    viewState: SubscriptionsListState,
    addToIgnore: (Long) -> Unit,
    removeFromIgnore: (Long) -> Unit,
    toggleIgnore: (SubscriptionCellModel) -> Unit
) {
     LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(
            items = viewState.items,
            key = { item -> item.groupId }
        ) { viewModel ->
            SubscriptionCell(viewModel, addToIgnore, removeFromIgnore, toggleIgnore)
        }
    }
}


@Composable
private fun LoadingView() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(10) {
            item {
                SubscriptionGrayCell()
            }
        }
    }
}